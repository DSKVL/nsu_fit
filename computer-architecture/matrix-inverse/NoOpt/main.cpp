#include <iostream>
#include <cfloat>
#include <chrono>
#include <fstream>
#include <cstring>

class Matrix
{
public:
    Matrix();
    Matrix(size_t);
    Matrix(const Matrix&);
    ~Matrix();

    size_t getSize();
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
    float* buf;
};

Matrix::Matrix() : size_(3), data_(new float[9]), buf(new float[9]) {}

Matrix::Matrix(size_t N) : size_(N), data_(new float[size_ * size_]), buf(new float[size_ * size_]) {
    for (size_t i = 0; i < size_; i++) {
        data_[i * size_ + i] = 1;
    }
}
Matrix::Matrix(const Matrix& other) : size_(other.size_), data_(new float[size_ * size_]), buf(new float[size_ * size_]) {
    std::copy(other.data_, other.data_+size_*size_, data_);
}

Matrix::~Matrix() {
    delete[] data_;
    delete[] buf;
}

size_t Matrix::getSize() {
    return size_;
}

void Matrix::add(const Matrix &other) {
    for (size_t i = 0; i < other.size_; i++) {
        for (size_t j = 0; j < other.size_; j++) {
            this->data_[i * size_ + j] += other.data_[i * size_ + j];
        }
    }
}

void Matrix::subtract(const Matrix &other) {
    for (size_t i = 0; i < other.size_; i++) {
        for (size_t j = 0; j < other.size_; j++) {
            this->data_[i * size_ + j] -= other.data_[i * size_ + j];
        }
    }
}

void Matrix::multiplyRight(const Matrix &other) {
    memset(buf, 0, sizeof(float) * size_*size_);
    for(size_t k = 0; k < size_; k++)
        for (size_t i = 0; i < size_; i++)
            for (size_t j = 0; j < size_; j++)
                buf[i*size_ + j] += data_[i*size_ + k] * other.data_[k*size_ + j];
    std::swap(buf, data_);
    std::cout << "Multiplication done\n";
}

void Matrix::divide(float scalar) {
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            this->data_[i * size_ + j] /= scalar;
        }
    }
}

void Matrix::transpose() {
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = i+1; j < size_; j++)
        {
            std::swap(data_[i*size_ + j], data_[j*size_ + i]);
        }
    }
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
    auto sums = new float[size_];
    for (size_t j = 0; j < size_; j++)
    {
        for (size_t i = 0; i < size_; i++)
        {
            sums[j] += std::abs(this->data_[i*size_ + j]);
        }
    }
    float max = -FLT_MAX;
    for (size_t i = 0; i < size_; i++)
    {
        max = max < sums[i] ? sums[i] : max;
    }

    return max;
}

float Matrix::Norm2() {
    auto sums = new float[size_];
    for (size_t i = 0; i < size_; i++)
    {
        for (size_t j = 0; j < size_; j++)
        {
            sums[i] += std::abs(this->data_[i*size_ + j]);
        }
    }
    float max = -FLT_MAX;
    for (size_t i = 0; i < size_; i++)
    {
        max = max < sums[i] ? sums[i] : max;
    }

    return max;
}

void Matrix::print() {
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            std::cout << data_[i * size_ + j] << " ";
        }
        std::cout << std::endl;
    }
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
