#ifndef PRISONERSDILEMMA_PLAYERSMANAGER_H
#define PRISONERSDILEMMA_PLAYERSMANAGER_H

#include <string>
#include <vector>
#include <array>
#include <memory>

#include "Strategy.h"

class PlayersManager {
public:
    explicit PlayersManager() = default;
    std::vector<std::array<Strategy*, 3> > getTours();
    void setPlayers(const std::vector<std::string>& players_names);
    ~PlayersManager() = default;
private:
    std::vector<std::unique_ptr<Strategy> > players;
};

#endif //PRISONERSDILEMMA_PLAYERSMANAGER_H
