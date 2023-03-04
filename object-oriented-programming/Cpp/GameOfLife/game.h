#ifndef GAME_H
#define GAME_H
#include <array>

#include "CellularAutomatonInterface.h"

struct Rule
{
    bool staying_alive[9] = {0, 0, 1, 1, 0, 0, 0, 0, 0};
    bool becoming_alive[9] = {0, 0, 0, 1, 0, 0, 0, 0, 0};
};

class Game : public CellularAutomatonInterface
{
public:
    Game();
    ~Game();

    void newGeneration() override;
    void clear() override;
    void reviveCell(unsigned x, unsigned y) override;
    void killCell(unsigned x, unsigned y) override;
    void setHeight(unsigned) override;
    void setWidth(unsigned) override;
    void setRule(int) override;
    unsigned getHeight() override;
    unsigned getWidth() override;
    bool isAlive(unsigned i, unsigned j) override;
private:
    Rule rule_;
    std::array<std::array<bool, 99>, 99> current_map_;
    std::array<std::array<bool, 99>, 99> next_map_;
    unsigned height_;
    unsigned width_;
    bool isAliveNextRound(unsigned i, unsigned j);
};

#endif // GAME_H
