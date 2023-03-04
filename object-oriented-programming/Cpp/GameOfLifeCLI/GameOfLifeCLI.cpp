#include "GameOfLifeCLI.h"

#include "GameImporter.h"
#include "GameDumper.h"
#include <span>
#include <iostream>

GameOfLifeCLI::GameOfLifeCLI( char** argv, int argc ) {
    bool iterationsSet = false;
    for (std::string arg : std::span(argv+1, argc-1)) {
        if (arg.starts_with("-o=")) {
            if (exportDestination_.has_value())
                throw std::invalid_argument("Specify just one output file.\n");

            exportDestination_ = std::filesystem::path(arg.substr(3));
        } else if (arg.starts_with("--output=")) {
            if (exportDestination_.has_value())
                throw std::invalid_argument("Specify just one output file.\n");

            exportDestination_ = std::filesystem::path(arg.substr(8));
        } else if (arg.starts_with("-i=")) {
            if (iterationsSet)
                throw std::invalid_argument("Specify totalIterations once.\n");

            totalIterations = std::stoi(arg.substr(3));
            iterationsSet = true;
        } else if (arg.starts_with("--totalIterations=")) {
            if (iterationsSet)
                throw std::invalid_argument("Specify totalIterations once.\n");

            totalIterations = std::stoi(arg.substr(3));
            iterationsSet = true;
        } else {
            if (importSource_.has_value())
                throw std::invalid_argument("Specify just one input file.\n");

            importSource_ = std::filesystem::path(arg);
        }
    }
}

void GameOfLifeCLI::start() {
    if (importSource_.has_value())
        import();

    while(true) {
        std::string command;
        std::cin >> command;

        if (command.starts_with("dump")) {
            dump(command.substr(4));
        } else if (command.starts_with("t")) {
            tick(command.substr(1));
        } else if (command.starts_with("tick")) {
            tick(command.substr(4));
        } else if (command == "help") {
            printHelp();
        } else if (command == "exit") {
            break;
        } else {
            std::cout << "Not a command. to see list of commands use \"help\".\n";
        }
    }
}

void GameOfLifeCLI::import() {
    auto path = importSource_.value();
    if (!exists(path) || !is_regular_file(path))
        throw std::invalid_argument("Specify correct input file.\n");
    GameImporter importer(path);
    importer.import();

    gameField = importer.getGameField();
    rule = importer.getRule();
}


void GameOfLifeCLI::tick(std::string arg) {
    int i = 1;
    if (!arg.empty() && !arg.starts_with(" ")) {
        std::cout << "Specify valid number.\n";
        return;
    }
    if (!arg.empty()) {
        i = std::stoi(arg.substr(1));
    }
    for (int j = 0; j < i && iterations < totalIterations ; j++, iterations++) {
        gameField = calculator.next(gameField);
    }

    std::cout << rule.to_string() << " " << iterations << "\n";
    gameField.print();
}

void GameOfLifeCLI::dump(std::string arg) {
    std::filesystem::path path;
    if (!arg.empty() && !arg.starts_with(" ")) {
        std::cout << "Specify valid number.\n";
        return;
    }
    if (arg.empty()) {
        if (!exportDestination_.has_value()) {
            std::cout << "Specify file.";
            return;
        }
        path = exportDestination_.value();
    }
    path = std::filesystem::path(arg.substr(1));
    GameDumper dumper(path);
    dumper.dump(rule, gameField);
}

void GameOfLifeCLI::printHelp() {
    //TODO print help
}

