#include <iostream>
#include <functional>
#include <array>

#include "./headers/Proxy.h"

int main(int argc, char* argv[]) {
    if (argc < 1) {
        std::cout << "Specify port\n";
  		  return 1;
    }

    auto port = (int) std::strtol(argv[1], nullptr, 10);
    if (port > 2<<16 ) {
        std::cout << "Port must be less than" << (2<<16) << "\n";
    }

    try {
        auto server = new Proxy(port);
        server->start();
    } catch (std::system_error &error) {
        std::cout << error.what();
    }
}
