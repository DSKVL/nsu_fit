#include "../headers/ServerConnection.h"


#include "../headers/Response.h"
#include "../headers/Request.h"
#include "../headers/URLResource.h"
#include "../headers/Proxy.h"

#include <sys/poll.h>
#include <sys/socket.h>
#include <unistd.h>
#include <cstring>
#include <iostream>

void ServerConnection::setClient(URLResource *client_) {
    client = client_;
    receiveMessage->setDestination(client_);
}
void ServerConnection::setRequest(Request* request ) {
    this->sendMessage = request;
    (*this->events) |= POLLOUT;
}
HTTPMessage *ServerConnection::receive() {
    ssize_t readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0);

    if (readCount == -1) {
        std::cerr << strerror(errno) << "\n";
        valid = false;
        return receiveMessage;
    }

    try {
        receiveMessage->append(buffer, static_cast<size_t>(readCount));
    } catch (std::invalid_argument& e) {
        std::cerr << e.what();
        valid = false;
        return receiveMessage;
    }

    return receiveMessage;
}
bool ServerConnection::isValid() const {
    return valid;
}
int ServerConnection::getSourceFd() const {
    return sourceFd;
}
void ServerConnection::send() {
    auto writtenCount = ::send(sourceFd, sendMessage->getMessage().data() + sendMessagePosition,
                               sendMessage->getMessage().length() - sendMessagePosition, 0);
    sendMessagePosition += static_cast<size_t>(writtenCount);

    if (sendMessagePosition != sendMessage->getMessage().length())
        return;

    (*events) &= ~POLLOUT;
    (*events) |= POLLIN;
    shutdown(sourceFd, SHUT_WR);
}
ServerConnection::ServerConnection(int fd, short *events, Proxy& proxy)
    : sourceFd(fd), events(events), proxy(proxy), receiveMessage(new Response(this)) {}

ServerConnection::~ServerConnection() {
    if (!receiveMessage->isCompleted()) {
        std::cout << "Server is being destroyed, message yet to be completed. Deleting clients.\n";
        client->connectionBreak();
        delete receiveMessage;
        proxy.cache.erase(sendMessage->getURL());
    }
    close(sourceFd);
}

void ServerConnection::setValid(bool v) {
    valid = v;
}

HTTPMessage *ServerConnection::flushIn() {
    ssize_t readCount = 1;

    while (readCount != 0) {
        readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0);
        if (readCount == -1) {
            valid = false;
            return nullptr;
        }

        try {
            receiveMessage->append(buffer, static_cast<size_t>(readCount));
        } catch (std::invalid_argument&) {
            valid = false;
            return nullptr;
        }
    }

    return receiveMessage;
}
