#ifndef PROXY_URLRESOURCE_H
#define PROXY_URLRESOURCE_H

#include <set>

class ServerConnection;
class Proxy;
class ClientConnection;
class Response;

class URLResource {
public:
    URLResource(ServerConnection*, Proxy&);
    ~URLResource();
    void addSubscriber(ClientConnection*);
    void removeSubscriber(ClientConnection*);
    void dataReceived(Response*);
    void connectionBreak();
private:
    std::set<ClientConnection*> subscribers;

    ServerConnection* sourceConnection;
    bool cached = false;
    Response* resource = nullptr;
    Proxy& proxy;
};

#endif //PROXY_URLRESOURCE_H
