#include <cstddef>
#include <iostream>
#include <fstream>
#include <cstring>
#include <cmath>
#include <chrono>
#include <mpi.h>
//#include "mpi/mpi.h"
#include <vector>
#include <algorithm>
#include <numeric>

struct Context {
    Context(size_t dataSize, size_t sendcountsMultiplier) : size(MPI::COMM_WORLD.Get_size()), 
                rank(MPI::COMM_WORLD.Get_rank()), sendcounts(getSendcounts(dataSize, size, sendcountsMultiplier)), 
                displaces(getDisplaces(sendcounts)), sendcounts_vector(getSendcounts(dataSize, size)),
                displaces_vector(getDisplaces(sendcounts_vector)), local_recvcount(sendcounts.at(rank)),
                local_recvcount_vector(sendcounts_vector.at(rank)),
                local_offset(displaces.at(rank)), local_offset_vector(displaces_vector.at(rank)) {};
    
    
    size_t size, rank;
    std::vector<int> sendcounts, displaces, sendcounts_vector, displaces_vector;
    size_t local_recvcount, local_recvcount_vector, local_offset, local_offset_vector;
private:
    
    //возвращает вектор, в котором на итой позиции стоит количество "элементов" для итого процесса
    std::vector<int> getSendcounts(int dataSize, int processNumber, int multiplier=1) {
        auto sendcounts = std::vector<int>(); // количество элементов на группу
        auto extraData = dataSize % processNumber; // количество широких столбцов/строк поширше
        auto commonData = dataSize / processNumber; // минимальная ширина/выстоа

        for (int i = 0 ; i < extraData; i++)
            sendcounts.push_back((commonData + 1) * multiplier);
        for (int i = extraData; i < processNumber; i++)
            sendcounts.push_back(commonData * multiplier);
        return sendcounts;
    }

    //возвращает вектор, в котором на итой позиции стоит сдвижка
    std::vector<int> getDisplaces(const std::vector<int> &sendcounts) {
        auto displaces = sendcounts;
        std::exclusive_scan(sendcounts.begin(), sendcounts.end(),
                            displaces.begin(), 0);
        return displaces;
    }
};

class Vector;

class Matrix {

public:
    Matrix(size_t, size_t, const Context&);
    Matrix(const Matrix&);
    ~Matrix();
    inline double atLocal(size_t x, size_t y) const {
        return local_data_[x + y*width];
    };
    inline double* getData() const          { return data_; };
    inline size_t getWidth() const          { return width; };
    inline void setWidth(size_t width)      { this->width = width; };    
    inline void setHeight(size_t height)    { this->height = height; };

    Matrix& operator=(const Matrix&);

    void read();
    void print(std::string filename);

    void setBuf(double *buf);
    void scatter(const Context&);
private:
    size_t width;
    size_t height;
    double *data_;

    double *local_data_;
    const Context& ctx;
    friend class Vector;
};

class Vector {
public:
    Vector(size_t, const Context&);
    Vector(Matrix&&);
    Vector(const Vector&);
    ~Vector();
    Vector& operator=(const Vector&);

    void multiply(const Matrix&);
    void multiply(double);
    void subtract(const Vector&);

    double dotProduct(const Vector&) const;
    double Norm() const;
    
    double* getData() { return data_; }

    void bcast();
    void allgather();
private:
    double *data_;
    double *local_buf_;
    size_t size_;
    size_t offset_;
    const Context& ctx;
};

Vector::Vector(size_t N, const Context& ctx) : data_(new double[N]{}), 
    local_buf_(new double[N]{}), size_(N), ctx(ctx) {}

Vector::~Vector() {
    delete[] data_;
    delete[] local_buf_;
}

//TODO shady stuff here
Vector::Vector(Matrix &&other) : data_(other.data_), size_(other.width*other.height),
    ctx(other.ctx) {
    other.data_ = nullptr;
    other.local_data_ = nullptr;
}

//TODO offsets
void Vector::multiply(const Matrix &mtx) {
    //double sum = 0;
    std::fill(local_buf_, local_buf_+size_, 0);
    for (size_t i = 0; i < ctx.local_recvcount_vector; i++) {
        for (size_t j = 0; j < mtx.width; j++)
            local_buf_[i] += mtx.atLocal(i, j) * data_[j];
    }
    //buf_[i] = sum;
    allgather();
}

void Vector::allgather() {   
    MPI::COMM_WORLD.Allgatherv(local_buf_, ctx.local_recvcount_vector, 
                               MPI::DOUBLE, data_, 
                               ctx.sendcounts_vector.data(), 
                               ctx.displaces_vector.data(), MPI::DOUBLE);
}

void Vector::multiply(double scalar) {
    for (size_t i = 0; i < this->ctx.local_recvcount_vector; i++)
        local_buf_[i] = data_[ctx.local_offset_vector + i] * scalar;
    allgather();
}

void Vector::subtract(const Vector &other) {
    for (size_t i = 0; i < this->ctx.local_recvcount_vector; i++)
        local_buf_[i] = data_[ctx.local_offset_vector + i] - other.data_[ctx.local_offset_vector + i];
    allgather();
}

double Vector::Norm() const {
    return dotProduct(*this);
}

double Vector::dotProduct(const Vector &other) const {
    double sum = 0;

    for (size_t i = 0; i < this->ctx.local_recvcount_vector; i++)
        sum += data_[ctx.local_offset_vector + i] * other.data_[ctx.local_offset_vector + i];
    double global_sum;
    MPI::COMM_WORLD.Allreduce(&sum, &global_sum, 1, MPI::DOUBLE, MPI::SUM);
    return global_sum;
}

Vector::Vector(const Vector &other) : data_(new double[other.size_]), 
       local_buf_(new double[other.size_]), size_(other.size_), ctx(other.ctx) {
    std::copy(other.data_, other.data_+size_, data_);
}

Vector &Vector::operator=(const Vector &other) {
    std::copy(other.data_, other.data_+size_, data_);
    return *this;
}

void Vector::bcast() {
    MPI::COMM_WORLD.Bcast(data_, size_, MPI::DOUBLE, 0);
}

Matrix::Matrix(size_t width, size_t height, const Context& context) :
        width(width), height(height),
        data_((context.rank == 0) ? new double[width * height]() : nullptr), // new double[]() - zero initialized
        local_data_(new double[context.local_recvcount]()), ctx(context) {
}

Matrix::~Matrix() {
    delete[] data_;
    delete[] local_data_;
}

void Matrix::print(const std::string filename) {
    if (data_ == nullptr) return;
    
    std::ofstream out(filename);
    for (size_t j = 0; j < height; j++) {
        for (size_t i = 0; i < width; i++)
            out << data_[j * width + i] << " ";

        out << std::endl;
    }
}

// void Matrix::read() {
//     std::ifstream file("mtx.txt");
//     for (size_t i = 0; i < size_; i++)
//         for (size_t j = 0; j < size_; j++)
//             file >> data_[i * size_ + j];

//     file.close();
// }

void Matrix::scatter(const Context &ctx) {
    MPI::COMM_WORLD.Scatterv(data_, 
        ctx.sendcounts.data(), ctx.displaces.data(), MPI::DOUBLE,
        local_data_, ctx.local_recvcount, MPI::DOUBLE, 0);
}

double criterion (const Matrix &A, Vector &x_n, 
                  Vector &x_n_copy, const Vector &b, double b_norm) {
    x_n_copy = x_n;
    x_n_copy.multiply(A);
    x_n_copy.subtract(b);
    return x_n_copy.Norm()/b_norm;
}

double tau (Vector &y_n, const Matrix &A) {
    static Vector A_(y_n);
    A_ = y_n;
    A_.multiply(A);
    return A_.dotProduct(y_n)/A_.dotProduct(A_);
}

Vector yn(Matrix &A, Vector &x_n, Vector &x_n_copy, Vector &b) {
    x_n_copy = x_n;
    x_n_copy.multiply(A);
    x_n_copy.subtract(b);
    return x_n_copy;
}

Vector xn(Vector &x_n, Matrix &A, Vector &b) {
    double tau_;
    double b_norm = b.Norm();
    Vector y_n(x_n);
    Vector x_n_copy(x_n);
    while (criterion(A, x_n, x_n_copy, b, b_norm) >= 0.0000001) {
        y_n = yn(A, x_n, x_n_copy, b);
        tau_ = tau(y_n, A);
        y_n.multiply(tau_);
        x_n.subtract(y_n);
    }
    return x_n;
}

void HeatMatrixInit(Matrix& A, size_t Nx) {
    size_t N = A.getWidth();
    auto data = A.getData();
    std::fill(data, data + N*N, 0);
    for (size_t i = 0; i < N; i++)
        data[i*N + i] = -4;
    for (size_t i = 0; i < N-1; i++)
        data[i*N + i +1] = 1;
    for (size_t i = 1; i < N; i++)
        data[i*N + i -1] = 1;
    for (size_t i = 0; i < N-Nx; i++)
        data[i*N + i +Nx] = 1;
    for (size_t i = Nx; i < N; i++)
        data[i*N + i -Nx] = 1;
}

int main() {
    using namespace MPI;
    Init();

//    constexpr size_t Nx = 50;
//    constexpr size_t SIZE = Nx*Nx;
//    Context ctx(SIZE, SIZE);
//    Matrix A(SIZE, SIZE, ctx);
//    HeatMatrixInit(A, SIZE);
//    Vector x_n(SIZE, ctx);
//    Matrix b(Nx, Nx, ctx);
//    b.getData()[9*Nx  + 9 ] = -9.0;
//    b.getData()[39*Nx + 39] =  9.0;
//    b.setHeight(Nx*Nx);
//    b.setWidth(1);
//    Vector bVec(std::move(b));
//    bVec.bcast();
//    x_n.bcast();

    constexpr size_t N = 16;
    Context ctx(N, N);
    Vector x_n(N, ctx);
    Vector bVec(N, ctx);
    std::fill(bVec.getData(), bVec.getData()+N, (double) N+1);
    Matrix A(N, N, ctx);
    auto d = A.getData();
    if (ctx.rank == 0) {
        std::fill(d, d + N*N, 1.0);
        for (int i = 0; i < N; i++)
            d[i*4+i] = 2.0;
    }
    bVec.bcast();
    std::cout << "bVec bcast\n";
    A.scatter(ctx); 
    std::cout << "A scatter\n";

    auto start = std::chrono::high_resolution_clock::now();
    auto res = xn(x_n, A, bVec);
    auto end = std::chrono::high_resolution_clock::now();
    auto time = std::chrono::duration_cast<std::chrono::milliseconds>(end-start);

    std::cout << time.count() << "\n";

    for (int i = 0; i < N; i++) 
	      std::cout << res.getData()[i] << " ";

    Finalize();
}
