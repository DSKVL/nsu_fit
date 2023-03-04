#include "Deduction.h"

#include <cmath>

#include "../Headers/Factory.h"
#include "../Headers/Response.h"

Response Deduction::getResponse(const std::vector<Response> &my_history,
                                const std::vector<Response> &op1_history,
                                const std::vector<Response> &op2_history) {
    if (!op1_history.empty()) {
        auto op1_coop_prob = probability_of_cooperation(op1_history, op2_history, my_history);
        auto op2_coop_prob = probability_of_cooperation(op2_history, op1_history, my_history);

        if (op1_coop_prob != NAN && op2_coop_prob != NAN) {
            if (op1_coop_prob > 0.5 && op2_coop_prob > 0.5) {
                Response coop[3] = {Response::COOPERATE, Response::COOPERATE, Response::COOPERATE};
                Response def[3] = {Response::DEFECT, Response::COOPERATE, Response::COOPERATE};
                if (rule.getPayoffs(coop)[0] > rule.getPayoffs(def)[0]) {
                    return Response::COOPERATE;
                } else {
                    return Response::DEFECT;
                }
            } else if (op1_coop_prob <= 0.5 && op2_coop_prob <= 0.5) {
                Response coop[3] = {Response::COOPERATE, Response::DEFECT, Response::DEFECT};
                Response def[3] = {Response::DEFECT, Response::DEFECT, Response::DEFECT};
                if (rule.getPayoffs(coop)[0] > rule.getPayoffs(def)[0]) {
                    return Response::COOPERATE;
                } else {
                    return Response::DEFECT;
                }
            } else {
                Response coop[3] = {Response::COOPERATE, Response::DEFECT, Response::COOPERATE};
                Response def[3] = {Response::DEFECT, Response::DEFECT, Response::COOPERATE};
                if (rule.getPayoffs(coop)[0] > rule.getPayoffs(def)[0]) {
                    return Response::COOPERATE;
                } else {
                    return Response::DEFECT;
                }
            }
        }
    }
    return Response::COOPERATE;
}

std::string Deduction::getName() const {
    return name;
}

int Deduction::getId() const {
    return id;
}

void Deduction::setId(int i) {
    id = i;
}

float Deduction::probability_of_cooperation(const std::vector<Response> &analyzed_history,
                                                             const std::vector<Response> &op1_history,
                                                             const std::vector<Response> &op2_history) {
    std::array<int, 2> CC_coop_count = {}, CD_coop_count = {}, DD_coop_count = {};
    std::array<float, 3> probabilities = {NAN, NAN, NAN};
    auto steps = analyzed_history.size();

    for (size_t i = 1; i < steps; i++) {
        if ((op1_history[i-1] == Response::COOPERATE) && (op2_history[i-1] == Response::COOPERATE)) {
            CC_coop_count[1]++;
            if(analyzed_history[i] == Response::COOPERATE) {
                CC_coop_count[0]++;
            }
        } else if((op1_history[i-1] == Response::DEFECT) && (op2_history[i-1] == Response::DEFECT)) {
            DD_coop_count[1]++;
            if(analyzed_history[i] == Response::COOPERATE) {
                DD_coop_count[0]++;
            }
        } else {
            CD_coop_count[1]++;
            if(analyzed_history[i] == Response::COOPERATE) {
                CD_coop_count[0]++;
            }
        }
    }

    if (CC_coop_count[1] != 0) {
        probabilities[0] = (static_cast<float>(CC_coop_count[0])/static_cast<float>(CC_coop_count[1]));
    }
    if (CD_coop_count[1] != 0) {
        probabilities[1] = (static_cast<float>(CD_coop_count[0])/static_cast<float>(CD_coop_count[1]));
    }
    if (DD_coop_count[1] != 0) {
        probabilities[2] = (static_cast<float>(DD_coop_count[0])/static_cast<float>(DD_coop_count[1]));
    }

    if (op1_history[steps-1] == Response::COOPERATE && op2_history[steps-1] == Response::COOPERATE) {
        return probabilities[0];
    } else if(op1_history[steps-1] == Response::DEFECT && op2_history[steps-1] == Response::DEFECT) {
        return probabilities[1];
    }

    return probabilities[2];
}

void Deduction::setPayoffs(PayoffsRule r) {
    this->rule = r;
}

namespace {
    std::function<std::unique_ptr<Strategy>()> createD = {[](){
            return std::make_unique<Deduction>();
    }};

    bool b = Factory<std::string, Strategy>::getInstance()
            ->registerCreator("Deduction", createD);
}