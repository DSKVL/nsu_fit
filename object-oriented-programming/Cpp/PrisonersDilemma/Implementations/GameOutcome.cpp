#include "../Headers/GameOutcome.h"

#include "../Headers/Strategy.h"
#include "../Headers/RoundOutcome.h"

GameOutcome::GameOutcome(std::vector<std::string> players_names) : players_amount(players_names.size()) {
    for (const auto& name : players_names) {
        score_by_id.emplace_back(0);
        strategy_name_by_id.emplace_back(name);
    }
}

void GameOutcome::addRoundOutcome(RoundOutcome roundOutcome) {
    score_by_id[roundOutcome.id[0]] += roundOutcome.score[0];
    score_by_id[roundOutcome.id[1]] += roundOutcome.score[1];
    score_by_id[roundOutcome.id[2]] += roundOutcome.score[2];
}
