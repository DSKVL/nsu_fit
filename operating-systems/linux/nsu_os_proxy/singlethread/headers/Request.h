#ifndef PROXY_REQUEST_H
#define PROXY_REQUEST_H

#include "HTTPMessage.h"

class Connection;
class Request : public HTTPMessage {
public:
    Request() = default;
    explicit Request(Connection*);
    Request(const std::string&, Connection*);
    ~Request() override;

    [[nodiscard]] std::string getHost() const;
    [[nodiscard]] std::string getURL() const;
    [[nodiscard]] std::string_view getMethod() const;
    [[nodiscard]] char *getPath() const;
    [[nodiscard]] size_t getPathLen() const;

    void append(char*, size_t) override;
    void acceptProxy(Proxy& proxy) override;
private:
    std::string host;
    size_t method_len = 0, path_len = 0, num_headers = HTTPMessage::MAX_HEADERS_NUMBER;
    int minor_version = 0;
    char *method = nullptr, *path = nullptr;
};

#endif //PROXY_REQUEST_H
