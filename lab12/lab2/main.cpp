#include <iostream>
#include <fstream>
#include <cstring>
#include <numeric>
#include <cmath>
#include <chrono>
#include <omp.h>

class Matrix
{
public:
    Matrix(size_t, size_t);
    Matrix(const Matrix&);
    ~Matrix();

    double norm() const;

    Matrix& add(const Matrix&);
    Matrix& subtract(const Matrix&);
    Matrix& multiplyLeft(const Matrix&);
    Matrix& multiplyRight(const Matrix&);
    Matrix& multiplyByScalar(double);
    Matrix& divideByScalar(double);

    bool equals(const Matrix&, double);
    void read(const char* filename);
    void set(double, size_t, size_t);
    void setSizeX(size_t);
    void setSizeY(size_t);
    void print();

    size_t getSizeX() const;
    size_t getSizeY() const;

private:
    size_t sizeX_;
    size_t sizeY_;
    double* data_;
    double* buf;
};
class BadMatrixSizeException : public std::exception {

};
Matrix::Matrix(size_t x, size_t y) : sizeX_(x), sizeY_(y), data_(new double[sizeX_ * sizeY_]()), buf(new double[sizeX_ * sizeY_]()) {
    for (size_t i = 0; i < sizeY_; i++)
        for (size_t j = 0; j < sizeX_; j++)
            data_[i*sizeX_+j] = (i == j) ? 1 : 0;
}
Matrix::Matrix(const Matrix& other) : sizeX_(other.sizeX_), sizeY_(other.sizeY_),
                                      data_(new double[sizeX_ * sizeY_]), buf(new double[sizeX_ * sizeY_]) {
    std::copy(other.data_, other.data_+sizeX_*sizeY_, data_);
}
Matrix::~Matrix() {
    delete[] data_;
    delete[] buf;
}
Matrix& Matrix::add(const Matrix &other) {
    if ( sizeX_ == other.sizeX_ && sizeY_ == other.sizeY_) {
#pragma omp parallel for
        for (size_t i = 0; i < sizeX_*sizeY_; i++) {
                this->data_[i] += other.data_[i];
        }
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::subtract(const Matrix &other) {
    if ( sizeX_ == other.sizeX_ && sizeY_ == other.sizeY_) {
#pragma omp parallel for
        for (size_t i = 0; i < sizeX_*sizeY_; i++) {
            this->data_[i] -= other.data_[i];
        }
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::multiplyLeft(const Matrix &other) {
    if (sizeY_ == other.sizeX_) {
        delete[] buf;
        buf = new double[sizeX_*other.sizeY_]();
#pragma omp parallel for collapse(2)
        for (size_t k = 0; k < sizeY_; k++)
            for (size_t i = 0; i < other.sizeY_; i++)
                for (size_t j = 0; j < sizeX_; j++)
                    buf[i * sizeX_ + j] += other.data_[i * other.sizeX_ + k] * data_[k * sizeX_ + j];


        std::swap(buf, data_);
        sizeY_ = other.sizeY_;
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::multiplyRight(const Matrix &other) {
    if (sizeX_ == other.sizeY_) {
        delete[] buf;
        buf = new double[sizeY_*other.sizeX_]();
#pragma omp parallel for collapse(2)
        for (size_t i = 0; i < sizeY_; i++)
            for (size_t j = 0; j < other.sizeX_; j++) {
                double res = 0;
                for (size_t k = 0; k < sizeY_; k++)
                    res += data_[i * sizeX_ + k] * other.data_[k * other.sizeX_ + j];
                buf[i * sizeX_ + j] += res;
            }

        std::swap(buf, data_);
        sizeX_ = other.sizeX_;
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::multiplyByScalar(double scalar) {
#pragma omp parallel for
    for (size_t i = 0; i < sizeX_*sizeY_; i++) {
            this->data_[i] *= scalar;
        }
    return *this;
}

Matrix &Matrix::divideByScalar(double scalar) {
#pragma omp parallel for
    for (size_t i = 0; i < sizeX_*sizeY_; i++) {
        this->data_[i] /= scalar;
    }
    return *this;
}

void Matrix::print() {
    for (size_t i = 0; i < sizeY_; i++) {
        for (size_t j = 0; j < sizeX_; j++) {
            std::cout << data_[i * sizeX_ + j] << " ";
        }
        std::cout << std::endl;
    }
}
void Matrix::read(const char* filename) {
    std::ifstream file(filename);
    for (size_t i = 0; i < sizeX_*sizeY_; i++) {
            file >> data_[i];
    }
    file.close();
}
void Matrix::set(double val, size_t x, size_t y) {
    data_[y*sizeX_ + x] = val;
}
size_t Matrix::getSizeX() const {
    return sizeX_;
}
size_t Matrix::getSizeY() const {
    return sizeY_;
}
double Matrix::norm() const {
    return std::sqrt(std::inner_product(data_, data_+sizeX_*sizeY_, data_, 0.0));
}

bool Matrix::equals(const Matrix &other, double prescision) {
    for (size_t i = 0; i < other.sizeY_*other.sizeX_; i++)
        if (data_[i] - other.data_[i] > prescision)
            return false;
    return true;
}

void Matrix::setSizeY(size_t y) {
    sizeY_ = y;
}
void Matrix::setSizeX(size_t x) {
    sizeX_ = x;
}

class SLESolver {
private:
    Matrix* A;
    Matrix* b;
    double b_norm = 0;
    double t = -0.01;
    double prescision = 0.00001;

    bool finished(const Matrix&);
public:
    SLESolver(Matrix* A, Matrix* b);
    Matrix* resolve();
};

Matrix* SLESolver::resolve() {
    auto x = new Matrix(1, b->getSizeY());
    x->set(0.0, 0, 0);
    while (!finished(*x)) {
        Matrix x_prev(*x);
        x->subtract(x_prev.multiplyLeft(*A)
                            .subtract(*b)
                            .multiplyByScalar(t));
    }
    return x;
}

bool SLESolver::finished(const Matrix& x) {
    Matrix x_temp(x);
    auto p = x_temp.multiplyLeft(*A).subtract(*b).norm()/b_norm;
    std::cout << p;
    return p < prescision;
}

SLESolver::SLESolver(Matrix *A, Matrix *b) : A(A), b(b){
    b_norm = b->norm();
}

void HeatMatrixInit(Matrix& A, size_t Nx) {
    size_t N = A.getSizeX();
    for (size_t i = 0; i < N; i++)
        for(size_t j = 0; j < N; j++)
        {
            if (i==j) A.set(-4.0, i, j);
            else if (i == j + Nx || j == i + Nx) A.set(1.0, i, j);
            else if ((i-j == 1 || i-j == -1) && (((i+1)%Nx != 1) || ((j+1)%Nx != 0)) && ((i+1)%Nx!=0 || (j+1)%Nx != 1)) A.set(1.0, i, j);
        }
}

int main() {
    using Time = std::chrono::time_point<std::chrono::high_resolution_clock>;
    using Diff = std::chrono::milliseconds;

    size_t Nx = 50, Ny = 50;

    Matrix b(1, Nx*Ny);
    for (size_t i = 0; i < b.getSizeX(); i++) {
        b.set(0, i, 0);
    }
    b.set(-9.0, 9, 9);
    b.set(9.0, 39, 39);

    Matrix A(Nx*Ny, Nx*Ny);
    HeatMatrixInit(A, Nx);

    std::cout << getenv("OMP_NUM_THREADS") << " ";

    SLESolver solver(&A, &b);

    double start = omp_get_wtime();
    auto x = solver.resolve();
    double end = omp_get_wtime();
    std::cout << end-start << "\n";

    delete x;
}