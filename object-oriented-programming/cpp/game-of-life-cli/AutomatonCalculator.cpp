#include "AutomatonCalculator.h"
#include "GameField.h"

AutomatonCalculator::AutomatonCalculator(const Rule &rule) : rule_(rule) {}

GameField AutomatonCalculator::next(const GameField &current) {
    auto width = current.getWidth();
    auto height = current.getHeight();

    GameField next(height, width);

    for (unsigned j = 0; j < height; j++)
        for (unsigned i = 0; i < width; i++)
            next.set(nextCellAt(i, j, current), i, j);

    return next;
}

Cell AutomatonCalculator::nextCellAt(size_t i, size_t j, const GameField& current) {
    auto height = current.getHeight();
    auto width = current.getWidth();
    auto currentCell = current.at(i, j);

    int aliveNeighbours = 0;
    if (current.at((i - 1) % width, (j - 1) % height).getStatus()) aliveNeighbours++;
    if (current.at( i,              (j - 1) % height).getStatus()) aliveNeighbours++;
    if (current.at((i + 1) % width, (j - 1) % height).getStatus()) aliveNeighbours++;
    if (current.at((i - 1) % width,  j              ).getStatus()) aliveNeighbours++;
    if (current.at((i + 1) % width,  j              ).getStatus()) aliveNeighbours++;
    if (current.at((i - 1) % width, (j + 1) % height).getStatus()) aliveNeighbours++;
    if (current.at( i,              (j + 1) % height).getStatus()) aliveNeighbours++;
    if (current.at((i + 1) % width, (j + 1) % height).getStatus()) aliveNeighbours++;

    return Cell(rule_.survives(currentCell.getStatus(), aliveNeighbours));
}

