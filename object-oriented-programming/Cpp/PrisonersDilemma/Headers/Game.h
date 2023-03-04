#ifndef PRISONERS_DILEMMA_GAME_H
#define PRISONERS_DILEMMA_GAME_H

#include <array>
#include <vector>
#include <memory>

#include "Strategy.h"
#include "PayoffsRule.h"

class RoundOutcome;
enum class Response;

class Game {
public:
    explicit Game() = default;
    void setPlayers(std::array<Strategy*,3> players);
    void setPayoffs(PayoffsRule);
    RoundOutcome runRound();
private:
    std::vector<Response> player0_history;
    std::vector<Response> player1_history;
    std::vector<Response> player2_history;
    PayoffsRule payoffs;
    std::array<Strategy*, 3> players;
};

#endif //PRISONERS_DILEMMA_GAME_H