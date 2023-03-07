#include <iostream>
#include <chrono>
#include <fstream>
#include <random>

int main() {
    constexpr int64_t N = 262144;
    std::ofstream log;
    log.open("log1");
    if (log.is_open()) {
            for (int i = 1; i < 32; i++){
                auto mem = new int64_t[N*i];
                if (mem != nullptr) {
                    for (int64_t j = 0; j < i; j++) {
                        std::iota(mem+j*N, mem+(j+1)*N, (j+1)*N);
                    }
                    std::iota(mem+(i-1)*N, mem+i*N-1, 1);
                    mem[i*N-1] = 0;

                    for (int64_t k = 0, i = 0; i < N; i++) k = mem[k];
                    auto start = std::chrono::high_resolution_clock::now();
                    for (int64_t k = 0, i = 0; i < N; i++) k = mem[k];
                    auto end = std::chrono::high_resolution_clock::now();
                    std::chrono::duration<double> elapsed_seconds = (end - start);

                    log << elapsed_seconds.count()/(N) << "\n";
                    std::cout << i << " " << elapsed_seconds.count()/(N)  << "\n ";
                    delete[] mem;
                }
            }
        }
        log.close();
    return 0;
}

