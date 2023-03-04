#include "../headers/URLResource.h"
#include "../headers/ClientConnection.h"
#include "../headers/Response.h"
#include "../headers/Proxy.h"

URLResource::URLResource(ServerConnection *server, Proxy &proxy) : sourceConnection(server), proxy(proxy) {}

void URLResource::addSubscriber(ClientConnection *subscriber) {
    pthread_mutex_lock(&mutex);
    subscribers.insert(subscriber);
    pthread_mutex_unlock(&mutex);
}

void URLResource::connectionBreak() {
    for (const auto &item: subscribers) {
        proxy.closeConnection(item->getSourceFd());
        subscribers.erase(item);
    }
}

void URLResource::removeSubscriber(ClientConnection *sub) {
    subscribers.erase(sub);
}

URLResource::~URLResource() {
    delete resource;
}

 pthread_cond_t *URLResource::getCond() {
    return &cond;
}

 pthread_mutex_t *URLResource::getMutex() {
    return &mutex;
}

Response *URLResource::getResponse() {
    return resource;
}

void URLResource::setResponse(Response *resource) {
    pthread_mutex_lock(&mutex);
    URLResource::resource = resource;
    pthread_mutex_unlock(&mutex);

}

