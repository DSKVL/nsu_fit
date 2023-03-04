#ifndef PROXY_SERVERCONNECTION_H
#define PROXY_SERVERCONNECTION_H

#include <csignal>
#include "Connection.h"
class URLResource;
class Request;
class Response;
class Proxy;

class ServerConnection : public Connection {
public:
    ServerConnection(int, Proxy&);
    ~ServerConnection() override;
    [[nodiscard]] HTTPMessage* receive() override;
    [[nodiscard]] HTTPMessage* flushIn() override;
    [[nodiscard]] Response* getReceiveMessage();
    void send() override;
    [[nodiscard]] bool isValid() const override;
    [[nodiscard]] int getSourceFd() const override;
    [[nodiscard]] char *getBuffer();
    [[nodiscard]] pthread_t* getThread();

    void setValid(bool) override;
    void setClient(URLResource *);
    void setRequest(Request*);

private:
    int sourceFd;
    bool valid = true;

    char buffer[BUFFER_SIZE]{0};

    Response* receiveMessage;
    Request* sendMessage = nullptr;
    URLResource* client = nullptr;
    Proxy& proxy;

    pthread_t thread;
};


#endif //PROXY_SERVERCONNECTION_H
