#ifndef PRISONERSDILEMMA_METASTRATEGY_H
#define PRISONERSDILEMMA_METASTRATEGY_H

#include <memory>

#include "Strategy.h"
#include "PayoffsRule.h"

enum class MetaStrategyMode {LINEAR, PARALLEL};

class Metastrategy : public Strategy {
public:
    Metastrategy(std::vector<std::unique_ptr<Strategy> >& substrategies,
                 MetaStrategyMode metastrategy_mode,
                 std::vector<size_t> substratigies_options,
                 std::string metastrategy_name);
    Response getResponse(const std::vector<Response>& my_history,
                         const std::vector<Response>& op1_history,
                         const std::vector<Response>& op2_history) override;
    std::string getName() const override;
    int getId() const override;
    void setId(int id) override;
    void setPayoffs(PayoffsRule) override;

    ~Metastrategy() override = default;
private:
    size_t current_step = 0;
    size_t current_strategy = 0;
    const std::vector<std::unique_ptr<Strategy> > substrategies;
    const MetaStrategyMode mode;
    const std::vector<size_t> substrategy_option;
    const std::string name;
    PayoffsRule rule;
    int id = 0;
};


#endif //PRISONERSDILEMMA_METASTRATEGY_H
