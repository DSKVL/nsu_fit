#include "../headers/ServerConnection.h"


#include "../headers/Response.h"
#include "../headers/Request.h"
#include "../headers/URLResource.h"
#include "../headers/Proxy.h"

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
}
HTTPMessage *ServerConnection::receive() {
    ssize_t readCount = recv(sourceFd, buffer, BUFFER_SIZE, 0);
    if (readCount == -1) std::cerr << strerror(errno) << "\n";
    receiveMessage->append(buffer, readCount);

    return receiveMessage;
}
bool ServerConnection::isValid() const {
    return valid;
}
int ServerConnection::getSourceFd() const {
    return sourceFd;
}
void ServerConnection::send() {
    auto writtenCount = ::send(sourceFd, sendMessage->getMessage().data(), sendMessage->getMessage().length(), 0);
    shutdown(sourceFd, SHUT_WR);
}
ServerConnection::ServerConnection(int fd, Proxy& proxy)
    : sourceFd(fd), proxy(proxy), receiveMessage(new Response(this)) {}

ServerConnection::~ServerConnection() {
    if (!receiveMessage->isCompleted()) {
        delete receiveMessage;
        client->connectionBreak();
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
        receiveMessage->append(buffer, readCount);
    }

    return receiveMessage;
}

Response *ServerConnection::getReceiveMessage() {
    return receiveMessage;
}

char *ServerConnection::getBuffer() {
    return buffer;
}

 pthread_t* ServerConnection::getThread() {
    return &thread;
}
