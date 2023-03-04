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

    [[nodiscard]] pthread_rwlock_t *getRwlock();

    void setDestination(URLResource*);
    void append(char*, size_t) override;
    void incUseCount();
    void decUseCount();
    [[nodiscard]] bool isCompleteHeaders() const;

private:
    std::string resBuffer;
    int status = -1;
    size_t msg_len = 0, num_headers = HTTPMessage::MAX_HEADERS_NUMBER;
    int minor_version = 0;
    char *msg = nullptr;

    bool completeHeaders = false;
    pthread_rwlock_t rwlock;

    size_t totalLength = 0;
    URLResource* destination = nullptr;
    int useCount = 0;
};


#endif //PROXY_RESPONSE_H
