#ifndef PROXY_CLIENTCONNECTION_H
#define PROXY_CLIENTCONNECTION_H

#include "Connection.h"
class Proxy;
class Response;
class Request;

class ClientConnection : public Connection {
public:
    explicit ClientConnection(int, short*, Proxy&);
    ~ClientConnection() override;
    void setBuffer(Response *response);
    void setValid(bool) override;
    void send() override;

    [[nodiscard]] HTTPMessage* receive() override;
    [[nodiscard]] HTTPMessage* flushIn() override;
    [[nodiscard]] bool isValid() const override;
    [[nodiscard]] int getSourceFd() const override;

private:
    Proxy& proxy;
    int sourceFd;
    short* events;
    bool valid = true;

    char buffer[BUFFER_SIZE]{0};
    Request* receiveMessage;

    Response* sendMessage = nullptr;
    size_t sendMessagePosition = 0;
};


#endif //PROXY_CLIENTCONNECTION_H
