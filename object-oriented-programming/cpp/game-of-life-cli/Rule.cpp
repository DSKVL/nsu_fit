#include "Rule.h"

Rule::Rule(const std::array<bool, 9> &born, const std::array<bool, 9> &survive) : born_(born), survive_(survive) {}

bool Rule::survives(bool alive, int neighboursCount) {
    return alive ? survive_.at(neighboursCount) : born_.at(neighboursCount);
}

std::string Rule::to_string() const {
    std::string res;
    res.append("B");
    for (int i = 0; i < 9; i++) {
        if (born_[i])
            res.append(std::to_string(i));
    }
    res.append("\\S");
    for (int i = 0; i < 9; i++) {
        if (survive_[i])
            res.append(std::to_string(i));
    }
    return res;
}
