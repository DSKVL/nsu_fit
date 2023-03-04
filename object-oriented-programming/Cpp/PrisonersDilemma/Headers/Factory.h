#ifndef PRISONERSDILEMMA_FACTORY_H
#define PRISONERSDILEMMA_FACTORY_H

#include <memory>

#include "FlatMap.h"
#include "GameExceptions.h"

template<class Id, class Product>
class Factory {
public:

    static Factory* getInstance() {
        static Factory<Id, Product> f;
        return &f;
    }

    std::unique_ptr<Product> createProductById(const Id& id) {
        try {
            return creators_.at(id)();
        } catch (std::out_of_range &e) {
            throw bad_players_exception("Wrong player name.");
        }
    }

    bool registerCreator(const Id& id,
                         std::function<std::unique_ptr<Product>()> creator){
        creators_.insert(id, creator);
        return true;
    }


private:
    FlatMap<Id, std::function<std::unique_ptr<Product>()> > creators_;
};


#endif //PRISONERSDILEMMA_FACTORY_H
