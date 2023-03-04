#include "Random.h"

#include <random>
#include <chrono>
#include <memory>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"
#include "../Headers/PayoffsRule.h"

Response Random::getResponse(const std::vector<Response> &, const std::vector<Response> &, const std::vector<Response> &) {
    static unsigned int seed = std::chrono::system_clock::now().time_since_epoch().count();
    static std::default_random_engine randomEngine(seed);

    return (randomEngine()%2) ? Response::DEFECT : Response::COOPERATE;
}

std::string Random::getName() const {
    return name;
}

int Random::getId() const {
    return id;
}

void Random::setId(int i) {
    id = i;
}

void Random::setPayoffs(const PayoffsRule) {}

namespace {
    std::function<std::unique_ptr<Strategy>()> createR = {[](){
        return std::make_unique<Random>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("Random", createR);
}

