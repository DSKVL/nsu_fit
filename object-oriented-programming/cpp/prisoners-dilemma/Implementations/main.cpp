#include <iostream>
#include "../Headers/ConsoleHandler.h"
#include "../Headers/PlayersManager.h"
#include "../Headers/FileManager.h"
#include "../Headers/Game.h"
#include "../Headers/GameExceptions.h"

int main(int argc, char const *argv[])
{
    try {
        ConsoleHandler console(argc,argv);
        FileManager file_manager;
        file_manager.registerMetastrategies(console.parseConfigs());
        PlayersManager players_manager(console.parsePlayers());
        auto players = players_manager.getPlayers();
        GameOutcome outcome(players);
        size_t steps = console.parseSteps();
        console.setMode(console.parseMode());
        auto payoffs = file_manager.parsePayoffsFile(console.parsePayoffsFile());
        Game round(payoffs);

        auto tours = players_manager.getTours();
        for (auto tour : *tours) {
            round.setPlayers(tour);
            for (size_t i = 0; i < steps; i++) {
                if (console.nextStep()) {
                    auto round_outcome = round.runRound();
                    outcome.addRoundOutcome(round_outcome);
                    console.printRoundOutcome(round_outcome);
                } else {
                    goto QUIT;
                }
            }
        }
        QUIT:
        console.printGameOutcome(outcome);
        delete tours;
        delete payoffs;
    } catch (bad_command_line_argument_exception &e) {
        std::cout << e.what();
        return -1;
    } catch (bad_players_exception &e) {
        std::cout << e.what();
        return -1;
    }
    return 0;
}
