#ifndef PRISONERSDILEMMA_GAMEOUTCOME_H
#define PRISONERSDILEMMA_GAMEOUTCOME_H

#include <vector>
#include <string>
#include <memory>

class Strategy;
class RoundOutcome;

struct GameOutcome {
    explicit GameOutcome(std::vector<std::string> players_names);
    void addRoundOutcome(RoundOutcome);
    size_t players_amount;
    std::vector<int> score_by_id;
    std::vector<std::string> strategy_name_by_id;
};


#endif //PRISONERSDILEMMA_GAMEOUTCOME_H
