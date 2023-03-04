#ifndef PRISONERSDILEMMA_ROUNDOUTCOME_H
#define PRISONERSDILEMMA_ROUNDOUTCOME_H

#include <array>
#include <string>
#include <memory>

#include "Strategy.h"

struct RoundOutcome {
    explicit RoundOutcome(std::array<std::string, 3> names, std::array<int,3> ids);
    std::array<int, 3> score;
    std::array<int, 3> id;
    std::array<std::string, 3> name;
};


#endif //PRISONERSDILEMMA_ROUNDOUTCOME_H
