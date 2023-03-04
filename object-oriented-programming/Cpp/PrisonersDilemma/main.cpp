#include "Headers/IterativePrisonersDilemma.h"

int main(int argc, char const *argv[])
{
    IterativePrisonersDilemma game(argc, argv);
    if (game.isValid()) {
        game.start();
    }
    game.start();
    return 0;
}
