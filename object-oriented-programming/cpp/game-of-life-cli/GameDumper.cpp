#include "GameDumper.h"
#include "Rule.h"
#include "GameField.h"

GameDumper::GameDumper(std::filesystem::path &path) : out(path) {
}

void GameDumper::dump(const Rule &rule, const GameField &field) {
    out << "#Life 1.06\n#N My univers\n#R " << rule.to_string() << "\n";
    auto width = field.getWidth();
    auto height = field.getHeight();
    for (int j = 0; j < height; j++)
        for (int i = 0; i < width; i++)
            if (field.at(i, j).getStatus())
                out << std::to_string(i) << " " << std::to_string(j) << "\n";
    out.flush();
}



