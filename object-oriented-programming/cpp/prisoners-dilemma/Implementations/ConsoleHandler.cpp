#include "../Headers/ConsoleHandler.h"

#include <iostream>

#include "../Headers/GameExceptions.h"
#include "../Headers/GameOutcome.h"
#include "../Headers/RoundOutcome.h"

ConsoleHandler::ConsoleHandler(int argc, const char **argv) : mode(Mode::DETAILED) {
    for (int i = 1; i < argc; i++)
    {
        args.emplace_back(std::string(argv[i]));
    }
}

std::vector<std::string> ConsoleHandler::parsePlayers() {
    std::vector<std::string> players_list;
    for (const std::string& arg : args) {
        if (arg.compare(0, 2, "--") != 0) {
            players_list.emplace_back(arg);
        }
    }
    if (players_list.size() <3) {
        throw bad_players_exception("Specify at least three strategies.");
    }
    return players_list;
}

size_t ConsoleHandler::parseSteps() {
    bool steps_is_set = false;
    size_t steps = 1;
    for (const std::string &arg: args) {
        if (!arg.compare(0, 2, "--")) {
            if (!std::string(arg).compare(2, 6, "steps=")) {
                std::string::const_iterator it = arg.begin();
                while (it != arg.end() && !std::isdigit(*it)) ++it;
                if (it != arg.end()) {
                    if (!steps_is_set) {
                        steps = std::stoull(arg.substr(8));
                        steps_is_set = true;
                    } else {
                        throw bad_command_line_argument_exception(
                                "--steps= was used twice. You should use it once or use default value 1.");
                    }
                } else {
                    throw bad_command_line_argument_exception("You must write a valid unsigned number after --steps=");
                }
            }
        }
    }
    return steps;
}

Mode ConsoleHandler::parseMode() {
    bool mode_is_set = false;
    Mode m = Mode::DETAILED;
    int players_counter = 0;
    for (const std::string& arg : args) {
        if (!arg.compare(0, 2, "--")) {
            if (!arg.compare(2,5, "mode=")) {
                if (!arg.compare(7,8, "detailed") && arg.length() == 15) {
                    if (!mode_is_set) {
                        mode_is_set = true;
                    } else {
                        throw bad_command_line_argument_exception("--mode= was used two times, while it can be used once");
                    }
                } else if (!arg.compare(7,4, "fast") && arg.length() == 11) {
                    if (!mode_is_set) {
                        m = Mode::FAST;
                        mode_is_set = true;
                    } else {
                        throw bad_command_line_argument_exception("--mode= was used two times, while it can be used once");
                    }
                }
                else if (!arg.compare(7,10, "tournament") && arg.length() == 17)
                {
                    if (!mode_is_set) {
                        m = Mode::TOURNAMENT;
                        mode_is_set = true;
                    } else {
                        throw bad_command_line_argument_exception("--mode= was used two times, while it can be used once");
                    }
                }
                else
                {
                    throw bad_command_line_argument_exception("Wrong value for --mode. Try detailed, fast or tournament");
                }
            }
        } else {
            players_counter++;
        }
    }
    if (!mode_is_set & (players_counter > 3)) {
        m = Mode::TOURNAMENT;
    }
    return m;
}

std::string ConsoleHandler::parsePayoffsFile() {
    bool is_set = false;
    std::string res;
    for (const std::string& arg : args) {
        if (!arg.compare(0, 2, "--")) {
            if (!std::string(arg).compare(2,7, "matrix=")) {
                if (!is_set) {
                    res = arg.substr(9);
                    is_set = true;
                } else {
                    throw bad_command_line_argument_exception("--matrix= was used two times, while it can be used once");
                }
            }
        }
    }
    return res;
}

std::string ConsoleHandler::parseConfigs() {
    std::string configs;
    bool is_set = false;
    for (const std::string& arg : args) {
        if (!arg.compare(0, 2, "--")) {
            if (!std::string(arg).compare(2,8, "configs=")) {
                if (!is_set) {
                    configs = arg.substr(10);
                    is_set = true;
                } else {
                    throw bad_command_line_argument_exception("--configs= was used two times, while it can be used once");
                }
            }
        }
    }
    return configs;
}

void ConsoleHandler::setMode(Mode m) {
    mode = m;
}

bool ConsoleHandler::nextStep() {
    if (mode != Mode::FAST) {
        std::string input;
        std::cin >> input;
        if (input == "quit" | input == "q") {
            return false ;
        }
    }
    return true;
}

void ConsoleHandler::printGameOutcome( GameOutcome outcome) {
    std::cout << "Game scores:\n";
    for (int i = 0; i < outcome.players_amount; i++) {
        std::cout << outcome.strategy_name_by_id[i] << ", id: " << i << ", score: " << outcome.score_by_id[i] << "\n";
    }
}

void ConsoleHandler::printRoundOutcome(RoundOutcome outcome) {
    if (mode != Mode::FAST) {
        std::cout << "Round scores:\n" << outcome.name[0] << ", id: " << outcome.id[0] << ", score: "
                  << outcome.score[0] << "\n";
        std::cout << outcome.name[1] << ", id: " << outcome.id[1] << ", score: " << outcome.score[1] << "\n";
        std::cout << outcome.name[2] << ", id: " << outcome.id[2] << ", score: " << outcome.score[2] << "\n";
    }
}