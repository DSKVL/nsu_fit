#include "../Headers/RoundOutcome.h"

RoundOutcome::RoundOutcome(std::array<std::string, 3> names, std::array<int, 3> ids) :
            score{0,0,0}, id(ids), name(names) {}
