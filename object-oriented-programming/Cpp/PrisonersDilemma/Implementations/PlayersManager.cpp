#include "../Headers/PlayersManager.h"

#include <string>

#include "../Headers/Factory.h"

std::vector<std::array<Strategy*, 3> > PlayersManager::getTours() {
    std::vector<std::array<Strategy*, 3> > tours;

    for (auto i = 0; i < players.size() - 2; i++) {
        for (auto j = i + 1; j < players.size() - 1; j++){
            for (auto k = j + 1; k < players.size(); k++) {
                tours.push_back({players[i].get(), players[j].get(), players[k].get()});
            }
        }
    }
    return tours;
}

void PlayersManager::setPlayers(const std::vector<std::string> &players_names) {
    int i = 0;
    for (const auto& name: players_names) {
        players.emplace_back(Factory<std::string, Strategy>::getInstance()->createProductById(name));
        players[i]->setId(i);
        i++;
    }
}
