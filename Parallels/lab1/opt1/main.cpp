#include <iostream>
#include <fstream>
#include <cstring>
#include <numeric>
#include <cmath>
#include <chrono>


class Matrix
{
public:
    Matrix(size_t, size_t);
    Matrix(const Matrix&);
    ~Matrix();

    double norm() const;
    static double dotProduct(const Matrix&, const Matrix&);

    Matrix& add(const Matrix&);
    Matrix& subtract(const Matrix&);
    Matrix& multiplyLeft(const Matrix&);
    Matrix& multiplyRight(const Matrix&);
    Matrix& multiplyByScalar(double);
    Matrix& divideByScalar(double);
    Matrix& copy(const Matrix&);

    bool equals(const Matrix&, double);
    void read(const char* filename);
    void set(double, size_t, size_t);
    void setSizeX(size_t);
    void setSizeY(size_t);
    void print();

    size_t getSizeX() const;
    size_t getSizeY() const;

private:
    size_t buf_capacity;
    size_t sizeX_;
    size_t sizeY_;
    double* data_;
    double* buf;
};
class BadMatrixSizeException : public std::exception {

};
Matrix::Matrix(size_t x, size_t y) : sizeX_(x), sizeY_(y), data_(new double[sizeX_ * sizeY_]), buf(new double[sizeX_ * sizeY_]) {
    buf_capacity = sizeX_*sizeY_;
    for (size_t i = 0; i < sizeY_; i++)
        for (size_t j = 0; j < sizeX_; j++)
            data_[i*sizeX_+j] = (i == j) ? 1 : 0;
}
Matrix::Matrix(const Matrix& other) : sizeX_(other.sizeX_), sizeY_(other.sizeY_),
                                      data_(new double[sizeX_ * sizeY_]), buf(new double[sizeX_ * sizeY_]) {
    buf_capacity = sizeX_*sizeY_;
    std::copy(other.data_, other.data_+sizeX_*sizeY_, data_);
}
Matrix::~Matrix() {
    delete[] data_;
    delete[] buf;
}
Matrix& Matrix::add(const Matrix &other) {
    if ( sizeX_ == other.sizeX_ && sizeY_ == other.sizeY_) {
#pragma omp for
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
#pragma omp for
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
        if (buf_capacity < sizeX_*other.sizeY_) {
            delete[] buf;
            buf = new double[sizeX_ * other.sizeY_];
            memset(buf, 0, sizeX_*other.sizeY_);
        } else {
            memset(buf, 0, buf_capacity* sizeof(double));
        }
#pragma omp for collapse(2)
        for (size_t i = 0; i < other.sizeY_; i++)
            for (size_t j = 0; j < sizeX_; j++)
                for (size_t k = 0; k < sizeY_; k++)
                    buf[i * sizeX_ + j] += other.data_[i*other.sizeX_ + k] * data_[k * sizeX_ + j];

        std::swap(buf, data_);
        buf_capacity = sizeX_*sizeY_;
        sizeY_ = other.sizeY_;
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::multiplyRight(const Matrix &other) {
    if (sizeX_ == other.sizeY_) {
        if (buf_capacity < sizeX_*other.sizeY_) {
            delete[] buf;
            buf = new double[sizeY_ * other.sizeX_];
            memset(buf, 0, sizeY_*other.sizeX_);
        } else {
            memset(buf, 0, buf_capacity* sizeof(double));
        }
#pragma omp for collapse(2)
        for (size_t i = 0; i < sizeY_; i++)
            for (size_t j = 0; j < other.sizeX_; j++)
                for (size_t k = 0; k < sizeY_; k++)
                    buf[i * sizeX_ + j] += data_[i * sizeX_ + k] * other.data_[k * other.sizeX_ + j];

        std::swap(buf, data_);
        buf_capacity = sizeX_*sizeY_;
        sizeX_ = other.sizeX_;
    } else {
        throw BadMatrixSizeException();
    }
    return *this;
}
Matrix& Matrix::multiplyByScalar(double scalar) {
    for (size_t i = 0; i < sizeX_*sizeY_; i++) {
        this->data_[i] *= scalar;
    }
    return *this;
}

Matrix &Matrix::divideByScalar(double scalar) {
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

double Matrix::dotProduct(const Matrix &a, const Matrix &b) {
    return std::inner_product(a.data_, a.data_+a.sizeX_*b.sizeY_, b.data_, 0.0);
}

Matrix& Matrix::copy(const Matrix &other) {
    if (buf_capacity < other.buf_capacity) {
        delete[] buf;
        buf = new double[other.buf_capacity];
        buf_capacity = other.buf_capacity;
    }
    if (sizeY_*sizeX_ < other.sizeX_* sizeX_){
        delete data_;
        data_ = new double [other.sizeX_*other.sizeY_];
    }
    std::copy(other.data_, other.data_ + sizeX_ * sizeY_, data_);
    sizeX_ = other.sizeX_;
    sizeY_ = other.sizeY_;

    return *this;
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
    Matrix cache;
    Matrix cacheYn;
    Matrix cacheAyn;
    unsigned int total_iterations = 0;
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
        cacheYn.copy(*x).multiplyLeft(*A).subtract(*b);
        cacheAyn.copy(cacheYn).multiplyLeft(*A);
        double tn = Matrix::dotProduct(cacheYn, cacheAyn)/Matrix::dotProduct(cacheAyn, cacheAyn);
        x->subtract(cacheYn.multiplyByScalar(tn));
    }
    std::cout << total_iterations;
    return x;
}

bool SLESolver::finished(const Matrix& x) {
    total_iterations++;
    cache.copy(x);
    auto p = cache.multiplyLeft(*A).subtract(*b).norm()/b_norm;
    return p < prescision;
}

SLESolver::SLESolver(Matrix *A, Matrix *b) : A(A), b(b), cache(*b), cacheYn(*b), cacheAyn(*b){
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
    size_t Nx = 50, Ny = 50;

    Matrix b(1, Nx*Ny);
    for (size_t i = 0; i < b.getSizeX(); i++) {
        b.set(0, i, 0);
    }
    b.set(-9.0, 9, 9);
    b.set(9.0, 39, 39);

    Matrix A(Nx*Ny, Nx*Ny);
    HeatMatrixInit(A, Nx);

    SLESolver solver(&A, &b);

    std::cout << getenv("OMP_NUM_THREADS") << " ";


    auto start = std::chrono::high_resolution_clock::now();

#pragma omp parallel
        auto x = solver.resolve();

    auto end =  std::chrono::high_resolution_clock::now();

    auto ms_int = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);

    if(b.equals(x->multiplyLeft(A), 0.00001))
        std::cout << " correct\n";
    std::cout << ms_int.count() << "ms\n ";
    delete x;
}