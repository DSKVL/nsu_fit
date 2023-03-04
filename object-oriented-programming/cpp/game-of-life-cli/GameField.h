#ifndef GAMEOFLIFE_GAMEFIELD_H
#define GAMEOFLIFE_GAMEFIELD_H

#include <vector>
#include <cstddef>
#include "Cell.h"

class GameField {
public:
    GameField() = default;
    GameField(size_t height, size_t width);
    ~GameField() = default;

    [[nodiscard]] const Cell& at(size_t x, size_t y) const;
    void set(Cell, size_t x, size_t y);
    [[nodiscard]] size_t getHeight() const;
    [[nodiscard]] size_t getWidth() const;
    void print();
private:
    size_t height_ = 32;
    size_t width_ = 48;
    std::vector<std::vector<Cell>> cells_ = std::vector(height_, std::vector<Cell>(width_));
};


#endif //GAMEOFLIFE_GAMEFIELD_H
