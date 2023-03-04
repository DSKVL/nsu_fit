#include "../headers/Request.h"
#include "../headers/Proxy.h"

std::string_view Request::getMethod() const {
    return {method, method_len};
}

char *Request::getPath() const {
    return path;
}

size_t Request::getPathLen() const {
    return path_len;
}

Request::Request(Connection *source_) : HTTPMessage(source_) {}

std::string Request::getHost() const {
    return host;
}

std::string Request::getURL() const {
    return host + std::string(path, path_len);
}

Request::Request(const std::string &buf, Connection *source) : Request(source) {
    messageBuffer.append(buf);
}

void Request::append(char *buf, size_t n) {
    messageBuffer.append(buf, n);

    auto messageEnd = phr_parse_request(messageBuffer.c_str(), messageBuffer.length(),
                                        const_cast<const char **>(&method), &method_len,
                                        const_cast<const char **>(&path), &path_len,
                                        &minor_version, &headers[0], &num_headers, 0);
    switch (messageEnd) {
        //request is partial
        case -2:
            return;

            //TODO an error occured
        case -1:

            return;
        default:
            break;
    }

    completed = true;

    int i;
    for (i = 0; i < num_headers; i++)
        if (std::string_view(headers[i].name).starts_with("Host")) break;
    if (i == num_headers) return;

    host = std::string(headers[i].value, headers[i].value_len);
}

Request::~Request() = default;