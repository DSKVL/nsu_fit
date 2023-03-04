#include <iostream>
#include "GameOfLifeCLI.h"

int main(int argc, char** argv) {
    try {
        GameOfLifeCLI game(argv, argc);
        game.start();
    } catch (std::exception& e) {
        std::cout << e.what();
    }
    return 0;
}
