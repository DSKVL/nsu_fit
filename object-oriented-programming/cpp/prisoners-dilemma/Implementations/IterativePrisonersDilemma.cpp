#include "../Headers/IterativePrisonersDilemma.h"

#include <iostream>

#include "../Headers/FileManager.h"
#include "../Headers/GameExceptions.h"
#include "../Headers/RoundOutcome.h"

IterativePrisonersDilemma::IterativePrisonersDilemma(int argc, const char **argv) : console(argc, argv){
    std::vector<std::string> players_names;
    Mode console_mode;
    std::string configs_dir_name, matrix_file_name;
    PayoffsRule rule;

    try {
        players_names = console.parsePlayers();
        total_steps = console.parseSteps();
        console_mode = console.parseMode();
        configs_dir_name = console.parseConfigs();
        matrix_file_name = console.parsePayoffsFile();
    } catch (bad_command_line_argument_exception &e) {
        std::cout << e.what();
        is_valid = false;
        return;
    }
    try {
        FileManager::registerMetastrategies(configs_dir_name);
    } catch (bad_configs_exception &e) {
        std::cout << e.what();
        is_valid = false;
        return;
    }
    try {
        playersManager.setPlayers(players_names);
    } catch (bad_players_exception &e) {
        std::cout << e.what();
        is_valid = false;
        return;
    }

    try {
        rule = FileManager::parsePayoffsFile(matrix_file_name);
    } catch (bad_payoffs_file_format &e) {
        std::cout << e.what();
        is_valid = false;
        return;
    }
    console.setMode(console_mode);
    engine.setPayoffs(rule);
}

GameOutcome IterativePrisonersDilemma::start() {
    if (is_valid) {
    GameOutcome outcome(console.parsePlayers());
    auto tours = playersManager.getTours();
    for (const auto& tour : tours) {
        engine.setPlayers(tour);
        for (size_t i = 0; i < total_steps; i++) {
            if (console.nextStep()) {
                auto round_outcome = engine.runRound();
                outcome.addRoundOutcome(round_outcome);
                console.printRoundOutcome(round_outcome);
            } else {
                goto QUIT;
            }
        }
    }
    QUIT:
    console.printGameOutcome(outcome);
    return outcome;
    }
}

bool IterativePrisonersDilemma::isValid() const {
    return is_valid;
}