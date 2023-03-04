#include <netinet/in.h>
#include <unistd.h>
#include <cstring>
#include <sys/socket.h>
#include <netdb.h>
#include <iostream>
#include <pthread.h>

#include "../headers/picohttpparser.h"
#include "../headers/Proxy.h"
#include "../headers/Connection.h"
#include "../headers/ServerConnection.h"
#include "../headers/Response.h"
#include "../headers/Request.h"
#include "../headers/ClientConnection.h"

Proxy::Proxy(int port) {
    if (-1 == (serverSocketDesc = socket(AF_INET, SOCK_STREAM, IPPROTO_IP))) {
        throw std::system_error(errno, std::generic_category());
    }

    const auto addr = (struct sockaddr_in) {
            .sin_family = AF_INET,
            .sin_port = htons(port),
            .sin_addr = {(in_addr_t) htonl(INADDR_ANY)},
            .sin_zero = {0}};

    if (-1 == bind(serverSocketDesc, (sockaddr *) &addr, sizeof addr)) {
        throw std::system_error(errno, std::generic_category());
    }
    listen(serverSocketDesc, 8);
}

struct ClientReceiveStaticArgs {
    ClientReceiveStaticArgs(Proxy *proxy, ClientConnection *connection) : proxy(proxy), connection(connection) {}

    Proxy *proxy;
    ClientConnection *connection;
};

void Proxy::start() {
    while (true) {
        int newClientFd = accept(serverSocketDesc, nullptr, nullptr);
        if (-1 == newClientFd) {
            std::cerr << "Unable to accept new connection\n";
            continue;
        }

        try {
            auto connection = new ClientConnection(newClientFd, *this);
            connections[newClientFd + CONNECTIONS_OFFSET] = connection;

            auto err = pthread_create(connection->getThread(), nullptr, handleClientStatic,
                                      new ClientReceiveStaticArgs(this, connection));
            err = pthread_detach(*connection->getThread());
        } catch (std::system_error &error) {
            std::cerr << "Unable to create new connection\n";
            close(newClientFd);
        }
    }
}


ServerConnection *Proxy::connectToServer(std::string_view host) {
    struct addrinfo hints{}, *res, *result;
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    hints.ai_protocol = 0;
    hints.ai_canonname = nullptr;
    hints.ai_addr = nullptr;
    hints.ai_next = nullptr;

    int err = getaddrinfo(host.data(), "80", &hints, &result);
    if (err != 0) {
        auto errorMessage = gai_strerror(err);
        throw std::system_error(errno, std::generic_category());
    }

    int sfd;
    for (res = result; res != nullptr; res = res->ai_next) {
        sfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
        if (sfd == -1)
            continue;
        if ((err = connect(sfd, res->ai_addr, res->ai_addrlen)) == 0)
            break;
        auto errorMessage = strerror(errno);
        close(sfd);
    }
    freeaddrinfo(result);
    if (sfd == -1) throw std::system_error(errno, std::generic_category());

    auto serv = new ServerConnection(sfd, *this);
    connections[sfd + CONNECTIONS_OFFSET] = serv;
    return serv;
}

struct HandleServerStaticArgs {
    Proxy *proxy;
    ServerConnection *connection;
    pthread_mutex_t *resourceMutex;
    pthread_cond_t *resourceCond;

    HandleServerStaticArgs(Proxy *proxy, ServerConnection *connection, pthread_mutex_t *resourceMutex,
                           pthread_cond_t *resourceCond) : proxy(proxy), connection(connection),
                                                           resourceMutex(resourceMutex), resourceCond(resourceCond) {}
};

Request *Proxy::buildRequest(Request &request) {
    auto newRequestBuffer = std::string("GET ");
    newRequestBuffer.append(request.getPath(), request.getPathLen())
            .append(" HTTP/1.0\r\n")
            .append("Host: ")
            .append(request.getHost()).append("\r\n")
            .append("Connection: close\r\n\r\n");
    return new Request(newRequestBuffer, request.getSource());
}

void Proxy::closeConnection(int fd) {
    delete connections[fd + CONNECTIONS_OFFSET];
    connections[fd + CONNECTIONS_OFFSET] = nullptr;
}


void *Proxy::handleClientStatic(void *args) {
    auto clientReceiveStaticArgs = (ClientReceiveStaticArgs *) args;
    auto proxy = clientReceiveStaticArgs->proxy;
    auto connection = clientReceiveStaticArgs->connection;
    pthread_cond_t *resourceCond;
    pthread_mutex_t *resourceMutex;

    auto request = (Request*) connection->receive();
    while (true) {
        if (!connection->isValid()) {
            proxy->closeConnection(connection->getSourceFd());
            free(args);
            return nullptr;
        }

        connection->receive();
        if (request->isCompleted())
            break;
    }

    if (!request->getMethod().starts_with("GET")) {
        proxy->closeConnection(connection->getSourceFd());
        free(args);
        return nullptr;
    }

    auto url = request->getURL();
    auto& cache = proxy->cache;
    auto cacheMutex = &proxy->cache_mutex;


    //TODO setting send massage
    pthread_mutex_lock(cacheMutex);
    try {
        auto resource = cache.at(url);
        pthread_mutex_unlock(cacheMutex);

        resourceMutex = resource.getMutex();
        resourceCond = resource.getCond();
        connection->setBuffer(resource.getResponse());
        resource.addSubscriber(connection);
        pthread_mutex_lock(resourceMutex);
    } catch (std::out_of_range &) {
        auto server = proxy->connectToServer(request->getHost());
        auto emplaceResult = cache.emplace(url, URLResource(server, *proxy));

        if (!emplaceResult.second) {
            proxy->closeConnection(server->getSourceFd());
            proxy->closeConnection(connection->getSourceFd());
            free(args);
            return nullptr;
        }

        auto resource = &(*emplaceResult.first).second;
        resourceMutex = resource->getMutex();
        resourceCond = resource->getCond();

        server->setRequest(buildRequest(*request));
        server->setClient(resource);
        resource->addSubscriber(connection);
        resource->setResponcse(server->getReceiveMessage());
        connection->setBuffer(server->getReceiveMessage());

        pthread_mutex_unlock(cacheMutex);


        pthread_mutex_lock(resourceMutex);

        pthread_create(server->getThread(), nullptr, handleServerStatic,
                       new HandleServerStaticArgs(proxy, server, resourceMutex, resourceCond));
        pthread_detach(*server->getThread());

    }

    while (connection->getSendMessage()->getMessage().empty())
        pthread_cond_wait(resourceCond, resourceMutex);
    pthread_mutex_unlock(resourceMutex);

    auto messageRWLock = connection->getSendMessage()->getRwlock();

    while (true) {
        pthread_mutex_lock(resourceMutex);
        while (connection->getSendMessage()->getMessage().size() == connection->getSendMessagePosition())
            pthread_cond_wait(resourceCond, resourceMutex);
        pthread_mutex_unlock(resourceMutex);

        pthread_rwlock_rdlock(messageRWLock);
        connection->send();
        pthread_rwlock_unlock(messageRWLock);

        if (!connection->isValid()) {
            proxy->closeConnection(connection->getSourceFd());
            break;
        }
    }

    delete clientReceiveStaticArgs;
    return nullptr;
}

void *Proxy::handleServerStatic(void *args) {
    auto handleServerStaticArgs = (HandleServerStaticArgs *) args;
    auto proxy = handleServerStaticArgs->proxy;
    auto connection = handleServerStaticArgs->connection;
    auto resourceCond = handleServerStaticArgs->resourceCond;
    auto resourceMutex = handleServerStaticArgs->resourceMutex;
    auto message = connection->getReceiveMessage();
    auto sourceFd = connection->getSourceFd();
    auto buffer = connection->getBuffer();
    auto messageRWLock = message->getRwlock();


    connection->send();

    while (!message->isCompleteHeaders()) {
        ssize_t readCount = recv(sourceFd, buffer, Connection::BUFFER_SIZE, 0);
        if (readCount == -1) std::cerr << strerror(errno) << "\n";
        //TODO error handling
        message->append(buffer, readCount);
    }

    //TODO check if 200

    pthread_mutex_lock(resourceMutex);
    while (!message->isCompleted()) {
        pthread_cond_broadcast(resourceCond);
        pthread_mutex_unlock(resourceMutex);

        ssize_t readCount = recv(sourceFd, buffer, Connection::BUFFER_SIZE, 0);
        if (readCount == -1) std::cerr << strerror(errno) << "\n";

        pthread_rwlock_wrlock(messageRWLock);
        message->append(buffer, readCount);
        pthread_rwlock_unlock(messageRWLock);
        pthread_mutex_lock(resourceMutex);
    }
    pthread_cond_broadcast(resourceCond);
    pthread_mutex_unlock(resourceMutex);

    proxy->closeConnection(connection->getSourceFd());

    delete handleServerStaticArgs;
    return nullptr;
}