#ifndef GAMEOFLIFE_AUTOMATONCALCULATOR_H
#define GAMEOFLIFE_AUTOMATONCALCULATOR_H


#include "Rule.h"

class GameField;
class Cell;

class AutomatonCalculator {
public:
    AutomatonCalculator() = default;
    GameField next(const GameField&);
private:
    Cell nextCellAt(size_t, size_t, const GameField&);
    Rule rule_;
public:
    explicit AutomatonCalculator(const Rule &rule);
};


#endif //GAMEOFLIFE_AUTOMATONCALCULATOR_H
