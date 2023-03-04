#ifndef PROXY_CONNECTION_H
#define PROXY_CONNECTION_H

#include <cstddef>

class HTTPMessage;
class Connection {
public:
    [[nodiscard]] virtual HTTPMessage* receive() = 0;
    [[nodiscard]] virtual HTTPMessage* flushIn() = 0;
    virtual void send() = 0;
    virtual ~Connection() = default;

    [[nodiscard]] virtual bool isValid() const = 0;
    [[nodiscard]] virtual int getSourceFd() const = 0;

    const static size_t BUFFER_SIZE = 8192;

    virtual void setValid(bool b) = 0;
};

#endif //PROXY_CONNECTION_H
