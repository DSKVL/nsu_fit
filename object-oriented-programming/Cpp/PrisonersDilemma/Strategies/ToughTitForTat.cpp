#include "ToughTitForTat.h"

#include <memory>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"
#include "../Headers/PayoffsRule.h"

Response ToughTitForTat::getResponse(const std::vector<Response> &my_history,
                                     const std::vector<Response> &opponent1_history,
                                     const std::vector<Response> &opponent2_history) {
    if (opponent1_history.empty()) {
        return Response::COOPERATE;
    } else {
        return (opponent1_history.back() == Response::DEFECT || opponent2_history.back() == Response::DEFECT ) ?
                Response::DEFECT : Response::COOPERATE;
    }
}

std::string ToughTitForTat::getName() const {
    return name;
}

int ToughTitForTat::getId() const {
    return id;
}

void ToughTitForTat::setId(int i) {
    id = i;
}

void ToughTitForTat::setPayoffs(const PayoffsRule ) {}

namespace {
    std::function<std::unique_ptr<Strategy>()> createTTFT = {[](){
        return std::make_unique<ToughTitForTat>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("ToughTitForTat", createTTFT);
}