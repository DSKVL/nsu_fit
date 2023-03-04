#ifndef PROXY_HTTPMESSAGE_H
#define PROXY_HTTPMESSAGE_H

#include <string_view>
#include <string>

#include "picohttpparser.h"

class Connection;
class Proxy;

class HTTPMessage {
public:
    [[nodiscard]] bool isCompleted() const;
    [[nodiscard]] Connection* getSource() const;
    [[nodiscard]] std::string_view getMessage();

    virtual void acceptProxy(Proxy& proxy) = 0;
    virtual void append(char*, size_t) = 0;

    virtual ~HTTPMessage() = default;

    const static size_t MAX_HEADERS_NUMBER = 24;
protected:
    HTTPMessage() = default;
    explicit HTTPMessage(Connection*);

    std::string messageBuffer;

    struct phr_header headers[HTTPMessage::MAX_HEADERS_NUMBER]{};
    bool completed = false;

    Connection* source = nullptr;
};

#endif //PROXY_HTTPMESSAGE_H
