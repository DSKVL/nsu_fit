#include "GameImporter.h"
#include <regex>
#include <optional>
#include "GameField.h"
#include <iterator>

GameImporter::GameImporter(const std::filesystem::path& path) : in(path) {
}

std::optional<std::string> GameImporter::getName() {
    return name;
}

GameField GameImporter::getGameField() {
    return gameField;
}

Rule GameImporter::getRule() {
    return rule;
}

void GameImporter::import() {
    in.seekg(0, std::ios::end);
    auto size = in.tellg();
    if (size >= FILE_SIZE_LIMIT)
        throw std::invalid_argument("Import file is too big.\n");

    std::string buffer(size, ' ');
    in.seekg(0);
    in.read(&buffer[0], size);
    std::smatch matches;

    std::regex cfgRegex("#Life 1\\.06\n(?:#N ([\\sa-zA-Z]+)\n)?(?:#R B(0?1?2?3?4?5?6?7?8?)\\\\S(0?1?2?3?4?5?6?7?8?)\n)?((?:-?\\d+ -?\\d+\n)*)");
    if (!std::regex_search(buffer, matches, cfgRegex))
        throw std::invalid_argument("Bad file format.\n");

    name = matches[1].str();
    std::array<bool, 9> born{false};
    std::array<bool, 9> survives{false};

    //ASCII code for '0' is 48
    for(char digit : matches[2].str())
        born[digit - 48] = true;

    for(char digit : matches[3].str())
        survives[digit - 48] = true;
    rule = Rule(born, survives);

    std::istringstream iss(matches[4].str());
    std::vector<std::string> nums((std::istream_iterator<std::string>(iss)),
                                     std::istream_iterator<std::string>());

    std::vector<int> aliveCellsCoordinatesX;
    std::vector<int> aliveCellsCoordinatesY;

    for (auto i = nums.begin(); i != nums.end(); i++) {
        aliveCellsCoordinatesX.emplace_back(std::stoi(*i));
        i++;
        aliveCellsCoordinatesY.emplace_back(std::stoi(*i));
    }

    auto maxHeight = *std::max_element(aliveCellsCoordinatesY.begin(), aliveCellsCoordinatesY.end());
    auto minHeight = *std::min_element(aliveCellsCoordinatesY.begin(), aliveCellsCoordinatesY.end());
    auto maxWidth = *std::max_element(aliveCellsCoordinatesX.begin(), aliveCellsCoordinatesX.end());
    auto minWidth = *std::min_element(aliveCellsCoordinatesX.begin(), aliveCellsCoordinatesX.end());

    auto height = maxHeight - minHeight + 10;
    auto width = maxWidth - minWidth + 10;

    gameField = GameField(height, width);
    for (int i = 0; i < aliveCellsCoordinatesX.size(); i++) {
        gameField.set(Cell(true), (aliveCellsCoordinatesX.at(i)+width)%width, (aliveCellsCoordinatesY.at(i)+height)%height);
    }
}
