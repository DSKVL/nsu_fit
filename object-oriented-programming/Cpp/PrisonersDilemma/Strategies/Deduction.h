#ifndef PRISONERSDILEMMA_DEDUCTION_H
#define PRISONERSDILEMMA_DEDUCTION_H

#include <array>
#include <vector>

#include "../Headers/Strategy.h"
#include "../Headers/PayoffsRule.h"

class Deduction : public Strategy {
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~Deduction() override = default;
private:
    static float probability_of_cooperation(const std::vector<Response>&,
                        const std::vector<Response>&, const std::vector<Response>&) ;
    const std::string name = "Deduction";
    PayoffsRule rule;
    int id = 0;
};

#endif //PRISONERSDILEMMA_DEDUCTION_H
