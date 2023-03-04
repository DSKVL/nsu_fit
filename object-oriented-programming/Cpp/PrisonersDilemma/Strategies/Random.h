#ifndef PRISONERSDILEMMA_RANDOM_H
#define PRISONERSDILEMMA_RANDOM_H

#include "../Headers/Strategy.h"

class Random : public Strategy{
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~Random() override = default;
private:
    const std::string name = "Random";
    int id = 0;
};


#endif //PRISONERSDILEMMA_RANDOM_H
