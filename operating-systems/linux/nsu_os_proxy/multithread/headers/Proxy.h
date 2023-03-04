#ifndef PROXY_PROXY_H
#define PROXY_PROXY_H

#include <string>
#include <set>
#include <string_view>
#include <array>
#include <functional>
#include <sys/poll.h>
#include <memory>

#include "picohttpparser.h"

#include "URLResource.h"

class Request;
class Connection;
class ServerConnection;
class ClientConnection;

class Proxy {
public:
    explicit Proxy(int port);
    [[noreturn]] void start();
    int acceptConnection();
    ServerConnection* connectToServer(std::string_view);
    void processMessage(Request *);
    static void processMessage(Response *);
    static Request* buildRequest(Request&);
    pthread_mutex_t cache_mutex = PTHREAD_MUTEX_INITIALIZER;
    std::unordered_map<std::string, URLResource> cache;
    void closeConnection(int);

private:
    static void* handleClientStatic(void*);
    static void* handleServerStatic(void*);


    static const int TOTAL_CONNECTIONS = 1020;
    std::array<Connection*, TOTAL_CONNECTIONS> connections{nullptr};
    int serverSocketDesc;

    static const int CONNECTIONS_OFFSET = -4;
};

#endif //PROXY_PROXY_H
