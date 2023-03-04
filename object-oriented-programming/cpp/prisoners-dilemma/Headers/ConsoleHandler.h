#ifndef PRISONERSDILEMMA_CONSOLEHANDLER_H
#define PRISONERSDILEMMA_CONSOLEHANDLER_H

#include <vector>
#include <string>

class GameOutcome;
class RoundOutcome;

enum class Mode {DETAILED, FAST, TOURNAMENT};

class ConsoleHandler {
public:
    ConsoleHandler (int argc, char const *argv[]);
    std::vector<std::string> parsePlayers();
    size_t parseSteps();
    Mode parseMode();
    std::string parsePayoffsFile();
    std::string parseConfigs();
    void setMode(Mode);
    bool nextStep();
    void printGameOutcome(GameOutcome);
    void printRoundOutcome(RoundOutcome);
private:
    std::vector<std::string> args;
    Mode mode;
};


#endif //PRISONERSDILEMMA_CONSOLEHANDLER_H
