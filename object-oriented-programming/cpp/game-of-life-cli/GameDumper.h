#ifndef GAMEOFLIFE_GAMEDUMPER_H
#define GAMEOFLIFE_GAMEDUMPER_H


#include <filesystem>
#include <fstream>

class Rule;
class GameField;

class GameDumper {
public:
    explicit GameDumper(std::filesystem::path&);
    ~GameDumper() = default;

    void dump(const Rule& rule, const GameField&);
private:
    std::ofstream out;
};


#endif //GAMEOFLIFE_GAMEDUMPER_H
