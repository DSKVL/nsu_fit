#include "../Headers/PayoffsRule.h"

#include <cstring>

#include "../Headers/Response.h"
PayoffsRule::PayoffsRule(int r[2][2][2][3]) {
    std::memcpy(payoffs, r, 24*sizeof(int));
}

std::array<int, 3> PayoffsRule::getPayoffs(Response r[3]) const {
    short idx1 = (r[0] == Response::COOPERATE) ? 1 : 0;
    short idx2 = (r[1] == Response::COOPERATE) ? 1 : 0;
    short idx3 = (r[2] == Response::COOPERATE) ? 1 : 0;
    return std::to_array(payoffs[idx1][idx2][idx3]);
}
