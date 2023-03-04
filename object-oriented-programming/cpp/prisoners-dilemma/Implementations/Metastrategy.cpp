#include "../Headers/Metastrategy.h"

#include <utility>
#include <numeric>

#include "../Headers/Response.h"

Metastrategy::Metastrategy(std::vector<std::unique_ptr<Strategy> >& substrategies,
                           MetaStrategyMode metastrategy_mode,
                           std::vector<size_t> substratigies_options,
                           std::string metastrategy_name) :
                                substrategies(std::move(substrategies)),
                                mode(metastrategy_mode),
                                substrategy_option(std::move(substratigies_options)),
                                name(std::move(metastrategy_name)), id(0) {}

Response Metastrategy::getResponse(const std::vector<Response> &my_history,
                                   const std::vector<Response> &op1_history,
                                   const std::vector<Response> &op2_history) {
    switch (mode) {
        case MetaStrategyMode::LINEAR:
            if (current_step == substrategy_option[current_strategy]) {
                current_step = 0;
                current_strategy++;
            } else {
                current_step++;
            }
            return substrategies[current_strategy]->getResponse(my_history, op1_history, op2_history);
        case MetaStrategyMode::PARALLEL:
            size_t vote_points = 0;
            for (size_t i = 0; i < substrategies.size(); i++) {
                if (substrategies[i]->getResponse(my_history, op1_history, op2_history) == Response::COOPERATE) {
                    vote_points += substrategy_option[i];
                }
            }
            return (static_cast<float>(vote_points) / static_cast<float>(std::accumulate(substrategy_option.begin(), substrategy_option.end(), 0))) > 0.5 ?
                   Response::COOPERATE : Response::DEFECT;
    }
}

std::string Metastrategy::getName() const {
    return name;
}

int Metastrategy::getId() const {
    return id;
}

void Metastrategy::setId(int i) {
    id = i;
}

void Metastrategy::setPayoffs(PayoffsRule r) {
    rule = r;
}
