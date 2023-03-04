#ifndef GAMEOFLIFE_GAMEOFLIFECLI_H
#define GAMEOFLIFE_GAMEOFLIFECLI_H

#include <filesystem>
#include <optional>
#include "AutomatonCalculator.h"
#include "GameField.h"
#include "Rule.h"

class GameOfLifeCLI {
public:
    GameOfLifeCLI(char** argv, int argc);
    ~GameOfLifeCLI() = default;

    void start();
private:
    void import();
    void tick(std::string arg);
    void dump(std::string arg);
    void printHelp();

    Rule rule;
    GameField gameField;
    AutomatonCalculator calculator;

    std::optional<std::filesystem::path> importSource_;
    std::optional<std::filesystem::path> exportDestination_;
    int totalIterations = 0;
    int iterations = 0;
};

#endif //GAMEOFLIFE_GAMEOFLIFECLI_H
