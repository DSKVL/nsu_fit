#include <iostream>
#include <fstream>
#include <iomanip>
#include <cassert>
#include <string>

long double pow(long double base, long double p) {
    if (p > 0) return base*pow(base, p - 1);
    else return 1;
}

long double fact(unsigned long long n) {
    if (n > 1) return n*fact(n - 1);
    else return 1;
}

int main(int argc, char **argv) {
    unsigned long long n = std::stoi(argv[1]);
    long double res = 1, x = std::stoi(argv[2]);
    double cpu_Hz = 2400000000ULL;

    union ticks {
        unsigned long long t64;
        struct s32 {
            long th, tl;
        } t32;
    } start, end;

    std::ofstream log;

    log.open("log", std::ios::app);

    log << x << " " << n << " ";

    asm("rdtsc\n":"=a"(start.t32.th), "=d"(start.t32.tl));

    for (unsigned long long i = 1; i <= n; i++) {
        res += (pow(x, i) / fact(i));
    }

    asm("rdtsc\n":"=a"(end.t32.th), "=d"(end.t32.tl));

    log << res << " " << (end.t64 - start.t64) / cpu_Hz << std::endl;
    std::cout << res << " " << (end.t64 - start.t64) / cpu_Hz << std::endl;
    log.close();

    return 0;
}