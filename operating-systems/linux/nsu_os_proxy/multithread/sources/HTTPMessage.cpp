#include "../headers/HTTPMessage.h"

HTTPMessage::HTTPMessage(Connection *source_) : source(source_) {}
bool HTTPMessage::isCompleted() const {
    return completed;
}
std::string_view HTTPMessage::getMessage() {
    return messageBuffer;
}
Connection *HTTPMessage::getSource() const {
    return source;
}
