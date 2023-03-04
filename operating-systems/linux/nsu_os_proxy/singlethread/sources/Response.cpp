#include <iostream>
#include "../headers/Response.h"
#include "../headers/Proxy.h"

Response::Response(Connection* source_) : HTTPMessage(source_) {}
Response::Response(Connection *source_, URLResource* client)
        : HTTPMessage(source_), destination(client) {}
URLResource *Response::getDestination() const {
    return destination;
}
void Response::acceptProxy(Proxy &proxy) {
    proxy.processMessage(this);
}
int Response::getStatus() const {
    return status;
}
size_t Response::getMsgLen() const {
    return msg_len;
}
char *Response::getMsg() const {
    return msg;
}
int Response::getUseCount() const {
    return useCount;
}
void Response::incUseCount() {
    useCount++;
}
void Response::decUseCount() {
    useCount--;
}
void Response::append(char *buf, size_t n) {
    messageBuffer.append(buf, n);
    //std::cerr << messageBuffer.length() << " of " << totalLength << "\n";
    if (!completeHeaders) {
        auto messageEnd = phr_parse_response(messageBuffer.c_str(), messageBuffer.length(), &minor_version, &status,
                                             const_cast<const char **>(&msg), &msg_len,
                                             &headers[0], &num_headers, 0);

        switch (messageEnd) {
            //request is partial
            case -2:
                return;
            case -1:
                throw std::invalid_argument("An error occurred while parsing");
            default:
                break;
        }

        completeHeaders = true;

        int i;
        for (i = 0; i < num_headers; i++)
            if (std::string_view(headers[i].name).starts_with("Content-Length")) break;
        if (i == num_headers) return;

        totalLength = strtoul(headers[i].value, nullptr, 10) + messageEnd;
    }
    completed = messageBuffer.length() == totalLength;

}

void Response::setDestination(URLResource *d) {
    destination = d;
}

bool Response::isCompleteHeaders() const {
    return completeHeaders;
}

Response::~Response() = default;