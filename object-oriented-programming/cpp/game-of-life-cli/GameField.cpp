#include "GameField.h"
#include <stdexcept>
#include <iostream>

GameField::GameField(size_t height, size_t width)
    : height_(height), width_(width), cells_(std::vector(height, std::vector<Cell>(width))) {
}

const Cell &GameField::at(size_t x, size_t y) const {
    if (x >= width_ || y >= height_) {
        throw std::out_of_range("No such cell.");
    }

    return cells_.at(y).at(x);
}

void GameField::set(Cell val, size_t x, size_t y) {
    if (x >= width_ || y >= height_) {
        throw std::out_of_range("Game field is smaller.");
    }

    cells_[y][x] = val;
}

size_t GameField::getHeight() const {
    return height_;
}

size_t GameField::getWidth() const {
    return width_;
}

void GameField::print() {
    for (int j = 0; j < height_; j++)
        for (int i = 0; i < width_; i++)
            std::cout << (cells_.at(j).at(i).getStatus() ? "O" : ".");
}

