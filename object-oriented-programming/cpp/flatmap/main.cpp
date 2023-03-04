#include "FlatMap.hpp"
#include <iostream>

int main()
{
    FlatMap<int, int> a;

    auto i = new int;
    if (a[1] == *i) {
        std::cout << "right";
    }
    return 0;
}
