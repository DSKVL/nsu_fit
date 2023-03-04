#ifndef PRISONERSDILEMMA_STRATEGY_H
#define PRISONERSDILEMMA_STRATEGY_H

#include <string>
#include <vector>

class PayoffsRule;
enum class Response;

class Strategy {
public:
    virtual Response getResponse(const std::vector<Response>&, const std::vector<Response>&, const std::vector<Response>&) = 0;
    virtual std::string getName() const = 0;
    virtual int getId() const = 0;
    virtual void setId(int id) = 0;
    virtual void setPayoffs(PayoffsRule rule) = 0;

    virtual ~Strategy() = 0;
};

#endif //PRISONERSDILEMMA_STRATEGY_H
