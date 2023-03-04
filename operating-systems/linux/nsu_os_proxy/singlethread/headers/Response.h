#ifndef PROXY_RESPONSE_H
#define PROXY_RESPONSE_H

#include "HTTPMessage.h"
class URLResource;
class Response : public HTTPMessage {
public:
    Response() = default;
    explicit Response(Connection*);
    explicit Response(Connection*, URLResource*);
    ~Response() override;

    [[nodiscard]] URLResource *getDestination() const;
    [[nodiscard]] int getStatus() const;
    [[nodiscard]] size_t getMsgLen() const;
    [[nodiscard]] char *getMsg() const;
    [[nodiscard]] int getUseCount() const;

    void setDestination(URLResource*);
    void append(char*, size_t) override;
    void acceptProxy(Proxy& proxy) override;
    void incUseCount();
    void decUseCount();

private:
    std::string resBuffer;
    int status = -1;
    size_t msg_len = 0, num_headers = HTTPMessage::MAX_HEADERS_NUMBER;
    int minor_version = 0;
    char *msg = nullptr;

    bool completeHeaders = false;
public:
    bool isCompleteHeaders() const;

private:
    size_t totalLength = 0;
    URLResource* destination = nullptr;
    int useCount = 0;
};


#endif //PROXY_RESPONSE_H
