#ifndef PROXY_CLIENTCONNECTION_H
#define PROXY_CLIENTCONNECTION_H

#include "Connection.h"
class Proxy;
class Response;
class Request;

class ClientConnection : public Connection {
public:
    explicit ClientConnection(int, Proxy&);
    ~ClientConnection() override;
    void setBuffer(Response *response);
    void setValid(bool) override;
    HTTPMessage* receive() override;
    [[nodiscard]] HTTPMessage* flushIn() override;
    void send() override;
    [[nodiscard]] bool isValid() const override;
    [[nodiscard]] int getSourceFd() const override;
    [[nodiscard]] Response* getSendMessage();
    [[nodiscard]] size_t getSendMessagePosition() const;
    [[nodiscard]] pthread_t* getThread();
private:
    Proxy& proxy;
    int sourceFd;
    bool valid = true;

    char buffer[BUFFER_SIZE]{0};
    Request* receiveMessage;

    Response* sendMessage = nullptr;
    size_t sendMessagePosition = 0;

    pthread_t thread;
};


#endif //PROXY_CLIENTCONNECTION_H
