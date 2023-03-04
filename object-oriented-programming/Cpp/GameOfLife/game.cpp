#include "game.h"
#include <algorithm>

Game::Game() : height_(32), width_(32) {
    for (int i = 0; i < 99; i++){
        for (int j = 0; j < 99; j++){
            current_map_[i][j] = false;
            next_map_[i][j] = false;
        }
    }
}

Game::~Game() {}

void Game::clear() {
    for (int i = 0; i < 99; i++){
        for (int j = 0; j < 99; j++){
            current_map_[i][j] = false;
            next_map_[i][j] = false;
        }
    }
}

unsigned Game::getHeight()
{
    return height_;
}

unsigned Game::getWidth()
{
    return width_;
}

void Game::setHeight(unsigned h)
{
    auto prev_h = height_;
    height_ = h;

    for (auto i = prev_h; i < height_; i++) {
        for (auto j = 0; j < width_; j++) {
            current_map_[i][j] = false;
            next_map_[i][j] = false;
        }
    }
}

void Game::setWidth(unsigned w)
{
    auto prev_w = width_;
    width_ = w;

    for (auto i = 0; i < height_; i++) {
        for (auto j = prev_w; j < width_; j++) {
            current_map_[i][j] = false;
            next_map_[i][j] = false;
        }
    }
}

bool Game::isAlive(unsigned int i, unsigned int j)
{
    return current_map_[i][j];
}

void Game::newGeneration()
{
    for (unsigned i = 0; i < height_; i++)
        for (unsigned j = 0; j < width_; j++)
        {
            current_map_[i][j] = next_map_[i][j];
        }
    for (unsigned i = 0; i < height_; i++)
        for (unsigned j = 0; j < width_; j++)
        {
            next_map_[i][j] = isAliveNextRound(i, j);
        }
}

void Game::reviveCell(unsigned x, unsigned y){
    current_map_[x][y] = true;
    next_map_[x][y] = true;
}

void Game::killCell(unsigned x, unsigned y){
    current_map_[x][y] = false;
    next_map_[x][y] = false;
}

void Game::setRule(int p)
{
    if (p / 9 == 0)
    {
        rule_.staying_alive[p] = !rule_.staying_alive[p];
    } else {
        rule_.becoming_alive[p%9] = !rule_.becoming_alive[p%9];
    }
}

bool Game::isAliveNextRound(unsigned i, unsigned j){
    int alive_neighbours = 0;
    alive_neighbours += current_map_[(i-1)%height_][(j-1)%width_];
    alive_neighbours += current_map_[(i-1)%height_][j];
    alive_neighbours += current_map_[(i-1)%height_][(j+1)%width_];
    alive_neighbours += current_map_[i][(j+1)%width_];
    alive_neighbours += current_map_[i][(j-1)%width_];
    alive_neighbours += current_map_[(i+1)%height_][(j-1)%width_];
    alive_neighbours += current_map_[(i+1)%height_][j];
    alive_neighbours += current_map_[(i+1)%height_][(j+1)%width_];

    if (current_map_[i][j])
    {
        return rule_.staying_alive[alive_neighbours];
    }
    else
    {
        return rule_.becoming_alive[alive_neighbours];
    }
}
