#include "AlwaysCooperate.h"

#include <memory>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"
#include "../Headers/PayoffsRule.h"

Response AlwaysCooperate::getResponse(const std::vector<Response> &, const std::vector<Response> &, const std::vector<Response> &) {
    return Response::COOPERATE;
}

std::string AlwaysCooperate::getName() const {
    return name;
}

void AlwaysCooperate::setId(int i) {
    id = i;
}

int AlwaysCooperate::getId() const {
    return id;
}

void AlwaysCooperate::setPayoffs(PayoffsRule) {}

namespace {
    std::function<std::unique_ptr<Strategy>()> createAC = {[]() {
        return std::make_unique<AlwaysCooperate>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("AlwaysCooperate", createAC);
}