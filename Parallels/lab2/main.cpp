#define LENGTH_LIMIT 32

#include <iostream>
#include <random>
#include <chrono>

template <class T>
void quickSort(T* a, const long n) {
    long i = 0, j = n-1;
    T pivot = a[n / 2];
    do {
        while (a[i] < pivot) {
            i++;
        }
        while (a[j] > pivot) {
            j--;
        }
        if (i <= j) {
            std::swap(a[i], a[j]);
            i++;
            j--;
        }
    } while (i <= j);
#pragma omp task if(j > LENGTH_LIMIT)
    if (j > 0) {
        quickSort(a, j+1);
    }

#pragma omp task if(i > LENGTH_LIMIT)
    if (n > i) {
        quickSort(a + i, n - i);
    }
}


int64_t* generate_data(size_t size) {
    std::random_device rd;
    std::mt19937 rng(rd());
    auto *mem = new int64_t[size];

    std::iota(mem, mem + size, 0);

    for (int64_t i = 0; i < size - 1; i++) {
        std::uniform_int_distribution<int> uni(i + 1, size - 1);
        std::swap(mem[i], mem[uni(rng)]);
    }

    return mem;
}

int main() {
    constexpr int n = 60000000;
    auto data = generate_data(n);

    auto start = std::chrono::high_resolution_clock::now();
#pragma omp parallel
    {
#pragma omp single
        {
            quickSort(data, n);
        }
    }
    auto end =  std::chrono::high_resolution_clock::now();

    auto ms_int = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);

    std::cout << ms_int.count() << "ms\n";
    delete data;
    return 0;
}
