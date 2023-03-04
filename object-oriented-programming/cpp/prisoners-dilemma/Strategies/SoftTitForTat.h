#ifndef PRISONERSDILEMMA_SOFTTITFORTAT_H
#define PRISONERSDILEMMA_SOFTTITFORTAT_H

#include "../Headers/Strategy.h"

class SoftTitForTat : public Strategy {
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~SoftTitForTat() override = default;
private:
    const std::string name = "SoftTitForTat";
    int id = 0;
};


#endif //PRISONERSDILEMMA_SOFTTITFORTAT_H
