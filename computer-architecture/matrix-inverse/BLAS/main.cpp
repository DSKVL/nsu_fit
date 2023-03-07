#include <iostream>
#include <cblas.h>
#include <vector>
#include <algorithm>
#include <random>
#include <ctime>
#include <fstream>
#include <chrono>

class Matrix
{
public:
    Matrix();
    Matrix(size_t);
    Matrix(const Matrix&);
    ~Matrix();

    void randomize();

    void add(const Matrix&);
    void subtract(const Matrix&);
    void multiplyRight(const Matrix&);
    void divide(float);
    void transpose();
    void inverse(size_t);

    float Norm1();
    float Norm2();

    void read();
    void print();


private:
    size_t size_;
    float* data_;
};

Matrix::Matrix() : size_(3), data_(new float[9]) {}

Matrix::Matrix(size_t N) : size_(N), data_((float*) aligned_alloc(32, size_*size_*sizeof(float))) {
    for (size_t i = 0; i < size_; i++) {
        data_[i * size_ + i] = 1;
    }
}

Matrix::Matrix(const Matrix& other) : size_(other.size_), data_((float*) aligned_alloc(32, size_*size_*sizeof(float))) {
    std::copy(other.data_, other.data_+size_*size_, data_);
}

Matrix::~Matrix() {
    delete[] data_;
}

void Matrix::randomize() {
    std::mt19937_64 random_eng;
    random_eng.seed(std::time(nullptr));

    auto L = new float[size_*size_];
    auto U = new float[size_*size_];
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = i; j < size_; j++) {
            U[i*size_ + j] = static_cast<float> (random_eng()%10);
            L[(size_-1 - i)* size_ + size_-1 - j] = static_cast<float>(random_eng()%10);
        }
    }

    cblas_sgemm(CblasRowMajor,CblasNoTrans, CblasNoTrans,  size_, size_, size_, 1, L, size_ , U, size_, 0, data_, size_);
    std::ofstream log;
    log.open ("mtx.txt");
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            log << data_[i * size_ + j] << " ";
        }
        log << std::endl;
    }
    log.close();
}

void Matrix::add(const Matrix &other) {
    cblas_saxpy(size_ * size_, 1, other.data_, 1, data_, 1);
}

void Matrix::subtract(const Matrix &other) {
    cblas_saxpy(size_ * size_, -1, other.data_, 1, data_, 1);
}

void Matrix::multiplyRight(const Matrix &other) {
    static auto *t = new float[size_* size_];
    cblas_sgemm(CblasRowMajor, CblasNoTrans, CblasNoTrans, size_, size_, size_, 1, data_, size_ , other.data_, size_, 0, t, size_);
    std::swap(data_, t);
}

void Matrix::divide(float scalar) {
    cblas_sscal(size_ * size_, 1/scalar, data_, 1);
}

void Matrix::transpose() {
    static auto t = new float[size_* size_];
    Matrix I(size_);
    cblas_sgemm(CblasRowMajor,CblasTrans, CblasNoTrans,  size_, size_, size_, 1, data_, size_ , I.data_, size_, 0, t, size_);
    std::swap(data_, t);
}

void Matrix::inverse(size_t N) {
    Matrix B(*this);
    B.transpose();
    B.divide(this->Norm1() * this->Norm2());

    Matrix BA(B);
    BA.multiplyRight(*this);

    Matrix R(size_);
    R.subtract(BA);

    Matrix Rn(size_);
    Matrix inv(size_);

    for (size_t i = 0; i < N; i++) {
        Rn.multiplyRight(R);
        inv.add(Rn);
    }

    inv.multiplyRight(B);
    std::swap(this->data_, inv.data_);
}

float Matrix::Norm1() {
    std::vector<float> sums;
    for (size_t i = 0; i < size_; i++) {
        sums.emplace_back(cblas_sasum(size_, data_+i*size_, 1));
    }
    return *std::max_element(sums.begin(), sums.end());
}

float Matrix::Norm2() {
    std::vector<float> sums;
    for (size_t i = 0; i < size_; i++) {
        sums.emplace_back(cblas_sasum(size_, data_+i, size_));
    }
    return *std::max_element(sums.begin(), sums.end());
}

void Matrix::print() {
    std::ofstream log;
    log.open ("log.txt");
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            log << data_[i * size_ + j] << " ";
        }
        log << std::endl;
    }
    log.close();
}

void Matrix::read() {
    std::ifstream file("mtx.txt");
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            file >> data_[i * size_ + j];
        }
    }
    file.close();
}

int main() {
    Matrix m(2048);
    m.read();
    auto start = std::chrono::steady_clock::now();
    m.inverse(10);
    auto end = std::chrono::steady_clock::now();
    std::chrono::duration<double> elapsed_seconds = end-start;
    std::cout << "elapsed time: " << elapsed_seconds.count() << "s\n";
    return 0;
}
