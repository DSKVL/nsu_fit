#ifndef GAMEOFLIFE_RULE_H
#define GAMEOFLIFE_RULE_H


#include <array>
#include <string>

class Rule {
public:
    Rule() = default;
    ~Rule() = default;

    Rule(const std::array<bool, 9> &born, const std::array<bool, 9> &survive);
    bool survives(bool alive, int neighboursCount);
    [[nodiscard]] std::string to_string() const;
private:
    std::array<bool, 9> born_{false, false, false, true, false, false, false, false, false};
    std::array<bool, 9> survive_{false, false, true, true, false, false, false, false, false};
};

#endif //GAMEOFLIFE_RULE_H
