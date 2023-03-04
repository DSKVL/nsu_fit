#include "../headers/URLResource.h"
#include "../headers/ClientConnection.h"
#include "../headers/Response.h"
#include "../headers/Proxy.h"

URLResource::URLResource(ServerConnection* server, Proxy& proxy) : sourceConnection(server), proxy(proxy) {}
void URLResource::addSubscriber(ClientConnection *subscriber) {
    subscribers.insert(subscriber);

    if (resource != nullptr) {
        subscriber->setBuffer(resource);
    }
}
void URLResource::dataReceived(Response* data_) {
    if (!data_->isCompleteHeaders()) {
        return;
    }

    resource = data_;

    for (const auto& subscriber: subscribers) {
        subscriber->setBuffer(data_);
    }
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

