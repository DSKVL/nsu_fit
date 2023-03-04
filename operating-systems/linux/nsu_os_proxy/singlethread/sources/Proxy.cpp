#include <netinet/in.h>
#include <unistd.h>
#include <cstring>
#include <sys/socket.h>
#include <netdb.h>
#include <iostream>

#include "../headers/picohttpparser.h"
#include "../headers/Proxy.h"
#include "../headers/Connection.h"
#include "../headers/ServerConnection.h"
#include "../headers/Response.h"
#include "../headers/Request.h"
#include "../headers/ClientConnection.h"

Proxy::Proxy(int port) : pollfds({0}) {
    if (-1 == (serverSocketDesc = socket(AF_INET, SOCK_STREAM, IPPROTO_IP))) {
        throw std::system_error(errno, std::generic_category());
    }

    const auto addr = (struct sockaddr_in) {
            .sin_family = AF_INET,
            .sin_port = htons(port),
            .sin_addr = {(in_addr_t) htonl(INADDR_ANY)},
            .sin_zero = {0}  };

    if (-1 == bind(serverSocketDesc, (sockaddr*) &addr, sizeof addr)) {
        throw std::system_error(errno, std::generic_category());
    }
    listen(serverSocketDesc, 8);
    pollfds.fill((pollfd) {.fd = -1, .events = POLLIN | POLLOUT | POLLHUP, .revents = 0 });
    pollfds[0] = (pollfd) {.fd = serverSocketDesc, .events = POLLIN, .revents = 0 };
}
void Proxy::start() {
    while (true) {
        int polled = poll(pollfds.data(), TOTAL_POLLS, -1);
        if (-1 == polled)
            throw std::system_error(errno, std::generic_category());

        if (pollfds[0].revents && POLLIN) {
            if (acceptConnection()) {
                perror("Can not accept");
            }
            polled--;
        }

        for (int i = 1; i < TOTAL_POLLS; i++) {
            bool polling = false;
            if (polled == 0) break;
            if (pollfds[i].revents & POLLIN) {
                auto message = connections[i-1]->receive();
                if (message != nullptr)
                    message->acceptProxy(*this);

                if (!connections[i-1]->isValid())
                    closeConnection(i+3);

                polling = true;
            }

            if (pollfds[i].revents & POLLOUT) {
                connections[i-1]->send();
                if (!connections[i-1]->isValid())
                    closeConnection(i+3);

                polling = true;
            }

            if (pollfds[i].revents & POLLHUP) {
                std::cout << "In pollhup section: ";
                auto message = connections[i-1]->flushIn();
                if (message != nullptr)
                    message->acceptProxy(*this);

                closeConnection(i+3);

                polling = true;
            }

            if (polling) polled--;
        }
    }
}
int Proxy::acceptConnection() {
    int newClientFd;
    if ((newClientFd = accept(serverSocketDesc, nullptr, nullptr)) == -1) {
        return newClientFd;
    }

    try {
        auto connection = new ClientConnection(newClientFd, &pollfds[newClientFd + POLLFDS_OFFSET].events, *this);
        connections[newClientFd + CONNECTIONS_OFFSET] = connection;
        std::cerr << "New connection. Now active " << ++connectionsCount << " connections.\n";
    } catch (std::system_error& error) {
        close(newClientFd);
        return 1;
    }

    pollfds[newClientFd + POLLFDS_OFFSET].events &= ~POLLOUT;
    pollfds[newClientFd + POLLFDS_OFFSET].fd = newClientFd;

    return 0;
}
ServerConnection* Proxy::connectToServer(std::string_view host) {
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
        sfd = socket(res->ai_family, res->ai_socktype,res->ai_protocol);
        if (sfd == -1)
            continue;
        if ((err = connect(sfd, res->ai_addr, res->ai_addrlen)) == 0)
            break;
        auto errorMessage = strerror(errno);
        close(sfd);
    }
    freeaddrinfo(result);
    if (sfd == -1) throw std::system_error(errno, std::generic_category());

    auto serv = new ServerConnection(sfd, &pollfds[sfd + POLLFDS_OFFSET].events, *this);
    connections[sfd + CONNECTIONS_OFFSET] = serv;
    pollfds[sfd + POLLFDS_OFFSET].revents &= ~POLLIN;
    pollfds[sfd + POLLFDS_OFFSET].fd = sfd;
    std::cerr << "New server connection. Active " << ++connectionsCount << "\n";
    return serv;
}
void Proxy::processMessage(Request *request) {
    if (!request->getMethod().starts_with("GET")) {
        closeConnection(request->getSource()->getSourceFd());
        return;
    }

    auto url = request->getURL();
    try {
        auto& resource = cache.at(url);
        resource.addSubscriber((ClientConnection*) request->getSource());
    } catch (std::out_of_range&) {
        auto server = connectToServer(request->getHost());
        auto emplaceResult = cache.emplace(url, URLResource(server, *this));

        if (emplaceResult.second) {
            server->setRequest(buildRequest(*request));
            server->setClient(&(*emplaceResult.first).second);

            (*emplaceResult.first).second.addSubscriber((ClientConnection*) request->getSource());
        } else {
            server->setValid(false);
        }
    }
}
void Proxy::processMessage(Response* response) {
    response->getDestination()->dataReceived(response);

    if (response->isCompleted())
        response->getSource()->setValid(false);
}
Request* Proxy::buildRequest(Request &request) {
    auto newRequestBuffer = std::string("GET ");
    newRequestBuffer.append(request.getPath(), request.getPathLen())
            .append(" HTTP/1.0\r\n")
            .append("Host: ")
            .append(request.getHost()).append("\r\n")
            .append("Connection: close\r\n\r\n");
    return new Request(newRequestBuffer, request.getSource());
}
void Proxy::closeConnection(int fd) {
    pollfds[fd + POLLFDS_OFFSET].events = POLLIN | POLLOUT | POLLHUP;
    pollfds[fd + POLLFDS_OFFSET].fd = -1;
    pollfds[fd + POLLFDS_OFFSET].revents = 0;
    delete connections[fd + CONNECTIONS_OFFSET];
    connections[fd + CONNECTIONS_OFFSET] = nullptr;
    std::cerr << "Deleted connection. Now " << --connectionsCount << " active.\n";
}

void Proxy::terminateProxy() {
    std::cerr << "trying terminate\n";
    for (int i = 0; i < Proxy::TOTAL_CONNECTIONS; i++) {
        if (connections[i] != nullptr)
            closeConnection(i + 4);
    }
    close(serverSocketDesc);
    std::cerr << "termination successful\n";
    exit(0);
}

