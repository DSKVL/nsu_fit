#ifndef PRISONERSDILEMMA_ALWAYSCOOPERATE_H
#define PRISONERSDILEMMA_ALWAYSCOOPERATE_H

#include "../Headers/Strategy.h"

class AlwaysCooperate : public Strategy {
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~AlwaysCooperate() override = default;
private:
    const std::string name = "AlwaysCooperate";
    int id = 0;
};


#endif //PRISONERSDILEMMA_ALWAYSCOOPERATE_H
