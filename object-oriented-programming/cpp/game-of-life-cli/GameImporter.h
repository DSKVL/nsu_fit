#ifndef GAMEOFLIFE_GAMEIMPORTER_H
#define GAMEOFLIFE_GAMEIMPORTER_H

#include <filesystem>
#include <fstream>
#include <optional>

#include "Rule.h"
#include "GameImporter.h"
#include "GameField.h"

class GameField;

class GameImporter {
public:
    explicit GameImporter(const std::filesystem::path&);
    void import();
    Rule getRule();
    GameField getGameField();
    std::optional<std::string> getName();
private:
    Rule rule;
    GameField gameField;
    std::optional<std::string> name;
    std::ifstream in;

    static constexpr size_t FILE_SIZE_LIMIT = 2 << 14;
};


#endif //GAMEOFLIFE_GAMEIMPORTER_H
