cmake_minimum_required(VERSION 3.24)
project(GameOfLife)

set(CMAKE_CXX_STANDARD 20)

add_executable(GameOfLife main.cpp GameDumper.cpp GameDumper.h Cell.cpp Cell.h GameOfLifeCLI.cpp GameOfLifeCLI.h GameImporter.cpp GameImporter.h Rule.cpp Rule.h GameField.cpp GameField.h AutomatonCalculator.cpp AutomatonCalculator.h)

add_subdirectory(tests)