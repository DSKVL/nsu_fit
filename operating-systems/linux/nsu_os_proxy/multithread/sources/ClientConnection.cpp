#include <unordered_map>
#include <sys/socket.h>

#include "../headers/ClientConnection.h"
#include "../headers/Proxy.h"
#include "../headers/Request.h"
#include "../headers/Response.h"

ClientConnection::ClientConnection(int fd, Proxy &proxy)
        : proxy(proxy), sourceFd(fd), receiveMessage(new Request(this)) {}

void ClientConnection::send() {
    auto writtenCount = ::send(sourceFd, sendMessage->getMessage().data() + sendMessagePosition,
                               sendMessage->getMessage().length() - sendMessagePosition, 0);
    sendMessagePosition += static_cast<size_t>(writtenCount);

    if (!sendMessage->isCompleted())
        return;

    //TODO maybe unnesesary
//    while (sendMessagePosition != sendMessage->getMessage().length())
//        ::send(sourceFd, sendMessage->getMessage().data() + sendMessagePosition,
//               sendMessage->getMessage().length() - sendMessagePosition, 0);

    valid = false;
}

void ClientConnection::setBuffer(Response *response) {
    if (this->sendMessage == nullptr) {
        this->sendMessage = response;
        response->incUseCount();
    }
}

HTTPMessage *ClientConnection::receive() {
    ssize_t readCount;
    while ((readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0)) == -1 && errno == EINTR);

    receiveMessage->append(buffer, static_cast<size_t>(readCount));
    if (receiveMessage->isCompleted()) {
        shutdown(sourceFd, SHUT_RD);
    }

    return receiveMessage;
}

bool ClientConnection::isValid() const {
    return valid;
}

int ClientConnection::getSourceFd() const {
    return sourceFd;
}

ClientConnection::~ClientConnection() {
    proxy.cache.at(receiveMessage->getURL()).removeSubscriber(this);
    sendMessage->decUseCount();

    delete receiveMessage;
    close(sourceFd);
}

void ClientConnection::setValid(bool v) {
    valid = v;
}

HTTPMessage *ClientConnection::flushIn() {
    ssize_t readCount;
    while ((readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0)) == -1 && errno == EINTR);

    receiveMessage->append(buffer, static_cast<size_t>(readCount));
    if (!receiveMessage->isCompleted()) {
        return nullptr;
    }

    return receiveMessage;
}

Response *ClientConnection::getSendMessage() {
    return sendMessage;
}

size_t ClientConnection::getSendMessagePosition() const {
    return sendMessagePosition;
}

pthread_t* ClientConnection::getThread() {
    return &thread;
}