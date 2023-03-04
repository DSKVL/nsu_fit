#ifndef PRISONERSDILEMMA_TOUGHTITFORTAT_H
#define PRISONERSDILEMMA_TOUGHTITFORTAT_H

#include "../Headers/Strategy.h"

class ToughTitForTat : public Strategy {
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~ToughTitForTat() override = default;
private:
    const std::string name = "ToughTitForTat";
    int id = 0;
};


#endif //PRISONERSDILEMMA_TOUGHTITFORTAT_H
