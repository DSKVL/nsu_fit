#include <unordered_map>
#include <poll.h>
#include <sys/socket.h>
#include <iostream>

#include "../headers/ClientConnection.h"
#include "../headers/Proxy.h"
#include "../headers/Request.h"
#include "../headers/Response.h"

ClientConnection::ClientConnection(int fd, short* events, Proxy& proxy)
    : proxy(proxy), sourceFd(fd), events(events), receiveMessage(new Request(this)) {}

void ClientConnection::send() {
    size_t len = sendMessage->getMessage().length() - sendMessagePosition;
    if (len > 2 << 12) len = 2 << 12;

    auto writtenCount = ::send(sourceFd, sendMessage->getMessage().data() + sendMessagePosition, len, 0);
    sendMessagePosition += static_cast<size_t>(writtenCount);

    if (sendMessagePosition == sendMessage->getMessage().length()){
        (*events) &= ~POLLOUT;
        return;
    }

    if (!sendMessage->isCompleted())
        return;
    valid = false;
}
void ClientConnection::setBuffer(Response *response) {
    if (this->sendMessage == nullptr) {
        this->sendMessage = response;
        response->incUseCount();
    }

    (*this->events) |= POLLOUT;
}
HTTPMessage *ClientConnection::receive() {
    ssize_t readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0);
    try {
        receiveMessage->append(buffer, static_cast<size_t>(readCount));
    } catch (std::invalid_argument&) {
        valid = false;
        return nullptr;
    }

    if (!receiveMessage->isCompleted()) {
        return nullptr;
    }

    (*this->events) &= ~POLLIN;
    shutdown(sourceFd, SHUT_RD);
    return receiveMessage;
}
bool ClientConnection::isValid() const {
    return valid;
}
int ClientConnection::getSourceFd() const {
    return sourceFd;
}

ClientConnection::~ClientConnection() {
    std::cout << "Client is being deleted.\n";
    proxy.cache.at(receiveMessage->getURL()).removeSubscriber(this);
    sendMessage->decUseCount();

    delete receiveMessage;
    close(sourceFd);
}

void ClientConnection::setValid(bool v) {
    valid = v;
}

HTTPMessage *ClientConnection::flushIn() {
    ssize_t readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0);

    try {
        receiveMessage->append(buffer, static_cast<size_t>(readCount));
    } catch (std::invalid_argument&) {
        valid = false;
        return nullptr;
    }
    if (!receiveMessage->isCompleted()) {
        return nullptr;
    }

    return receiveMessage;
}

