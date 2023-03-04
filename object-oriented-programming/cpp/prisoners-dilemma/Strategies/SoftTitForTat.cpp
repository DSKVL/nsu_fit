#include "SoftTitForTat.h"

#include <memory>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"
#include "../Headers/PayoffsRule.h"

Response SoftTitForTat::getResponse(const std::vector<Response> &my_history,
                                    const std::vector<Response> &opponent1_history,
                                    const std::vector<Response> &opponent2_history) {
    if (opponent1_history.empty()) {
        return Response::COOPERATE;
    } else {
        return (opponent1_history.back() == Response::DEFECT && opponent2_history.back() == Response::DEFECT ) ?
               Response::DEFECT : Response::COOPERATE;
    }
}

std::string SoftTitForTat::getName() const {
    return name;
}

int SoftTitForTat::getId() const {
    return id;
}

void SoftTitForTat::setId(int i) {
    id = i;
}

void SoftTitForTat::setPayoffs(const PayoffsRule) {}

namespace {
    std::function<std::unique_ptr<Strategy>()> createSTFT = {[](){
        return std::make_unique<SoftTitForTat>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("SoftTitForTat", createSTFT);
}
