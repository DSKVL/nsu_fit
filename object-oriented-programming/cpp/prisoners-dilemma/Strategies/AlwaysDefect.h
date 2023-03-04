#ifndef PRISONERSDILEMMA_ALWAYSDEFECT_H
#define PRISONERSDILEMMA_ALWAYSDEFECT_H

#include "../Headers/Strategy.h"

class AlwaysDefect : public Strategy {
public:
    Response getResponse(const std::vector<Response> &, const std::vector<Response>&, const std::vector<Response>&) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;
    ~AlwaysDefect() override = default;
private:
    const std::string name = "AlwaysDefect";
    int id = 0;
};

#endif //PRISONERSDILEMMA_ALWAYSDEFECT_H
