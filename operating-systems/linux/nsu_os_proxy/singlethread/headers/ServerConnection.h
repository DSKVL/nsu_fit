#ifndef PROXY_SERVERCONNECTION_H
#define PROXY_SERVERCONNECTION_H

#include "Connection.h"
class URLResource;
class Request;
class Response;
class Proxy;

class ServerConnection : public Connection {
public:
    ServerConnection(int , short *, Proxy&);
    ~ServerConnection() override;
    [[nodiscard]] HTTPMessage* receive() override;
    [[nodiscard]] HTTPMessage* flushIn() override;
    void send() override;
    [[nodiscard]] bool isValid() const override;
    [[nodiscard]] int getSourceFd() const override;

    void setValid(bool) override;
    void setClient(URLResource *);
    void setRequest(Request*);
private:
    int sourceFd;
    short* events;
    bool valid = true;

    char buffer[BUFFER_SIZE]{0};
    size_t sendMessagePosition = 0;

    Response* receiveMessage;
    Request* sendMessage = nullptr;
    URLResource* client = nullptr;
    Proxy& proxy;
};


#endif //PROXY_SERVERCONNECTION_H
