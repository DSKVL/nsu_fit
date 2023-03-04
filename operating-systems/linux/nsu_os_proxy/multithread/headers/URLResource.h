#ifndef PROXY_URLRESOURCE_H
#define PROXY_URLRESOURCE_H

#include <set>
#include <pthread.h>

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
    void connectionBreak();
    [[nodiscard]] pthread_cond_t *getCond();
    [[nodiscard]] pthread_mutex_t *getMutex();
    [[nodiscard]] Response* getResponse();

    void setResponse(Response *resource);

private:
    pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
    pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

    std::set<ClientConnection*> subscribers;

    ServerConnection* sourceConnection;
    bool cached = false;
    Response* resource = nullptr;
    Proxy& proxy;
};

#endif //PROXY_URLRESOURCE_H
