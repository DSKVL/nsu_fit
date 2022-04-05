#include <openmpi/mpi.h>

MPI_ScatterV();
MPI_Send(ptr, N, MPI_FLOAT, rank,...);
MPI_Reduce(sendbuf, recbuf, 1, MPI_Float, MPI_SUM, 0, COMM_WORLD);
MPI_Gather();
MPI_Broadcast();


/*
 * ReduceAll для сборки нормы по частям
 *
 *
 */

#include <iostream>
#include <fstream>
#include <cstring>
#include <numeric>
#include <cmath>

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
Matrix::Matrix(size_t x, size_t y) : sizeX_(x), sizeY_(y), data_(new double[sizeX_ * sizeY_]{0}), buf(new double[sizeX_ * sizeY_]{0}) {
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
        buf = new double[sizeX_*other.sizeY_]{0};

        for (size_t i = 0; i < other.sizeY_; i++)
            for (size_t j = 0; j < sizeX_; j++)
                for (size_t k = 0; k < sizeY_; k++)
                    buf[i * sizeX_ + j] += other.data_[i*other.sizeX_ + k] * data_[k * sizeX_ + j];

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
        buf = new double[sizeY_*other.sizeX_]{0};

        for (size_t i = 0; i < sizeY_; i++)
            for (size_t j = 0; j < other.sizeX_; j++)
                for (size_t k = 0; k < sizeY_; k++)
                    buf[i * sizeX_ + j] += data_[i * sizeX_ + k] * other.data_[k * other.sizeX_ + j];

        std::swap(buf, data_);
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


int main(int argc, char** argv) {
    int size, rank;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    //На мастер ноде читается файл с матрицей и вектором
    size_t Nx = 50, Ny = 50;
    Matrix A(Nx*Ny, Nx*Ny);
    HeatMatrixInit(A, Nx);


    Matrix b(1, Nx*Ny);
    for (size_t i = 0; i < b.getSizeX(); i++) {
        b.set(0, i, 0);
    }
    b.set(-9.0, 9, 9);
    b.set(9.0, 39, 39);

    //ScatterV раскидываем на все части
    //ScatterV собираем
    //ReduceAll для сбора нормы
    //Gather
    //
    //!!!! БЕЗ ВТОРОГО СПОСОБА
    //

    MPI_Finalize();
    return 0;
}
