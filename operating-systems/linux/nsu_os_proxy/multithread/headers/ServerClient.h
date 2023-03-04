#ifndef PROXY_SERVERCLIENT_H
#define PROXY_SERVERCLIENT_H

class Response;
class ServerClient {
public:
    virtual void dataReceived(Response*) = 0;
};



#endif //PROXY_SERVERCLIENT_H
