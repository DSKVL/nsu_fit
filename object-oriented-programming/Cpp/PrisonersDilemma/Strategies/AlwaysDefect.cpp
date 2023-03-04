#include "AlwaysDefect.h"

#include <memory>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"
#include "../Headers/PayoffsRule.h"

Response AlwaysDefect::getResponse(const std::vector<Response> &, const std::vector<Response> &, const std::vector<Response> &) {
    return Response::DEFECT;
}

std::string AlwaysDefect::getName() const {
    return name;
}

int AlwaysDefect::getId() const {
    return id;
}

void AlwaysDefect::setId(int i) {
    id = i;
}

void AlwaysDefect::setPayoffs(PayoffsRule ) {}

namespace {
    std::function<std::unique_ptr<Strategy>()> createAD = {[](){
        return std::make_unique<AlwaysDefect>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("AlwaysDefect", createAD);
}