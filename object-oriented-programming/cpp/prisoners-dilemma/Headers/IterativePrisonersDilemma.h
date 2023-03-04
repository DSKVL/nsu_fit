#ifndef UNTITLED_ITERATIVEPRISONERSDILEMMA_H
#define UNTITLED_ITERATIVEPRISONERSDILEMMA_H

#include "ConsoleHandler.h"
#include "PlayersManager.h"
#include "Game.h"
#include "GameOutcome.h"

class IterativePrisonersDilemma {
public:
    IterativePrisonersDilemma(int argc, char const *argv[]);
    GameOutcome start();
    bool isValid() const;
private:
    ConsoleHandler console;
    PlayersManager playersManager;
    Game engine;
    size_t total_steps = 0;
    bool is_valid = true;
};


#endif //UNTITLED_ITERATIVEPRISONERSDILEMMA_H
