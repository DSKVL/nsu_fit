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
    void processMessage(Response *);
    static Request* buildRequest(Request&);
    std::unordered_map<std::string, URLResource> cache;
    void closeConnection(int);
    void terminateProxy();

private:
    static const int TOTAL_POLLS = 1021;
    std::array<pollfd, TOTAL_POLLS> pollfds;
    static const int TOTAL_CONNECTIONS = 1020;
    std::array<Connection*, TOTAL_CONNECTIONS> connections{nullptr};
    int serverSocketDesc;

    int connectionsCount = 0;
    static const int CONNECTIONS_OFFSET = -4;
    static const int POLLFDS_OFFSET = -3;
};

#endif //PROXY_PROXY_H
