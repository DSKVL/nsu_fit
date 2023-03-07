#include <iostream>
#include <chrono>
#include <fstream>
#include <random>
#include <numeric>
#include <array>
#include <algorithm>

int main() {
    std::ofstream log;
//    std::ofstream Ns;
//    log.open("log1");
//    Ns.open("log");
//    if (log.is_open()) {
//        log << std::setprecision(15);
//        for (size_t N = 8; N < 196608; N+=4) {
////            Ns << N <<  "\n ";
//            int64_t *mem;
//            try {
//                mem = new int64_t[N];
//            } catch (std::bad_alloc &e) {
//                std::cout << "bad alloc at " << N;
//            }
//
//            std::iota(mem, mem+N-1, 1);
//            mem[N - 1] = 0;
//            for (int K = 1; K <= 4; K++) {
//                for (int64_t k = 0, i = 0; i < N; i++) k = mem[k];
//                int64_t xr = 0;
//                auto start = std::chrono::high_resolution_clock::now();
//
//                for (int64_t k = 0, i = 0; i < N*K; i++){
//                    xr ^=k;
//                    k = mem[k];
//                }
//                auto end = std::chrono::high_resolution_clock::now();
//                std::chrono::duration<double> elapsed_seconds = ((end - start)/N)/K;
//                log << elapsed_seconds.count() << " ";
//          }
//
//            log <<  "\n";
//
//            delete[] mem;
//        }
//        log.close();
//        Ns.close();
//    }
//
//    log.open("log2");
//    if (log.is_open()) {
//        for (size_t N = 8; N < 196608; N+=4) {
////            log << N << " ";
//            int64_t *mem;
//            try {
//                mem = new int64_t[N];
//            } catch (std::bad_alloc &e) {
//                std::cout << "bad alloc at " << N;
//            }
//
//            mem[0] = N - 1;
//            for (int64_t j = 1; j < N; j++) {
//                mem[j] = j - 1;
//            }
//
//                for (int64_t k = 0, i = 0; i < N; i++) k = mem[k];
//                int64_t xr = 0;
//                auto start = std::chrono::high_resolution_clock::now();
//                for (int64_t k = 0, i = 0; i < N; i++) {
//                    xr ^=k;
//                    k = mem[k];
//                }
//            auto end = std::chrono::high_resolution_clock::now();
//                std::chrono::duration<double> elapsed_seconds = (end - start)/N;
//                log << elapsed_seconds.count() << " ";
//
//            log << "\n";
//
//            delete[] mem;
//        }
//    log.close();
//    }

    std::random_device rd;
    std::mt19937 rng(rd());
    log.open("log3");
    int64_t *mem = new int64_t[1500000];;
    if (log.is_open()) {
        for (size_t N = 1299464; N < 1500000; N+=1024) {

            std::iota(mem, mem + N, 0);

            for (int64_t i = 0; i < N-1; i++) {
                std::uniform_int_distribution<int> uni(i+1, N-1);
                std::swap(mem[i], mem[uni(rng)]);
            }

            for (int64_t k = 0, i = 0; i < N; i++) k = mem[k];
            int64_t xr = 0;

            std::array<double, 8> times{};
            for (int p = 0; p < 8; p++) {
                auto start = std::chrono::high_resolution_clock::now();
                for (int64_t k = 0, i = 0; i < N; i++) {
                    xr ^= k;
                    k = mem[k];
                }
                auto end = std::chrono::high_resolution_clock::now();
                std::chrono::duration<double> elapsed_seconds = (end - start);
                times[p] = elapsed_seconds.count()/N;
            }

            log << *std::min_element(times.begin(), times.end()) << "\n";
        }
        log.close();
    }
    delete mem;
    return 0;
}
