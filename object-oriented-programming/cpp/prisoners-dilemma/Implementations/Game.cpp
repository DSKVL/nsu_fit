#include "../Headers/Game.h"

#include "../Headers/RoundOutcome.h"
#include "../Headers/Response.h"

RoundOutcome Game::runRound() {
    RoundOutcome outcome({players[0]->getName(), players[2]->getName(), players[2]->getName()},
                         {players[0]->getId(), players[1]->getId(), players[2]->getId()});

    Response round_responses[] = {players[0]->getResponse(player0_history, player1_history, player2_history),
                                  players[1]->getResponse(player1_history, player0_history, player2_history),
                                  players[2]->getResponse(player2_history, player1_history, player0_history)};

    player0_history.emplace_back(round_responses[0]);
    player1_history.emplace_back(round_responses[1]);
    player2_history.emplace_back(round_responses[2]);

    auto round_outcome = payoffs.getPayoffs(round_responses);

    outcome.score[0] += round_outcome[0];
    outcome.score[1] += round_outcome[1];
    outcome.score[2] += round_outcome[2];

    return outcome;
}

void Game::setPlayers(std::array<Strategy*,3> pl) {
    player0_history.clear();
    player1_history.clear();
    player2_history.clear();
    players = pl;
    players[0]->setPayoffs(payoffs);
    players[1]->setPayoffs(payoffs);
    players[2]->setPayoffs(payoffs);
}

void Game::setPayoffs(PayoffsRule p) {
    this->payoffs = p;
}
