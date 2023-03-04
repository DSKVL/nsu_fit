#ifndef PRISONERSDILEMMA_PAYOFFSRULE_H
#define PRISONERSDILEMMA_PAYOFFSRULE_H

#include <array>

enum class Response;

class PayoffsRule {
public:
    PayoffsRule() = default;
    ~PayoffsRule() = default;
    explicit PayoffsRule(int[2][2][2][3]);

    std::array<int, 3> getPayoffs(Response[3]) const;

private:
    int payoffs[2][2][2][3] = {{{{1,1,1},{5,5,0}},{{5,0,5},{9,3,3}}},{{{0,5,5},{3,9,3}},{{3,3,9},{7,7,7}}}};;
};


#endif //PRISONERSDILEMMA_PAYOFFSRULE_H
