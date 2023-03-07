#include <iostream>
#include <cfloat>
#include <immintrin.h>
#include <xmmintrin.h>
#include <chrono>
#include <fstream>
#include <algorithm>
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
    float* buf_;

    float hsum(__m256);
    float hmax(__m256);
};

Matrix::Matrix() : size_(3), data_(new float[9]), buf_(new float[9]){}

Matrix::Matrix(size_t N) : size_(N), data_((float*) aligned_alloc(32, size_*size_*sizeof(float))),
                           buf_((float*) aligned_alloc(32, size_*size_*sizeof(float))){
    for (size_t i = 0; i < size_; i++) {
        data_[i * size_ + i] = 1;
    }
}

Matrix::Matrix(const Matrix& other) : size_(other.size_), data_((float*) aligned_alloc(32, size_*size_*sizeof(float))),
                                    buf_((float*) aligned_alloc(32, size_*size_*sizeof(float))){
    for (size_t i = 0; i < other.size_; i++) {
        for (size_t j = 0; j < other.size_/8; j++) {
            __m256 buf = _mm256_load_ps(other.data_ + i * size_ + j*8);
            _mm256_store_ps(this->data_ + i * size_ + j*8, buf);
        }
        for(size_t k = size_ & 0b11111111111111111111111111111000; k < size_; k++)
        {
            data_[i * size_ + k] = other.data_[i * size_ + k];
        }
    }
}


Matrix::~Matrix() {
    delete[] data_;
    delete[] buf_;
}

size_t Matrix::getSize() {
    return size_;
}

void Matrix::add(const Matrix &other) {
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_/8; j++) {
            __m256 bufA = _mm256_load_ps(this->data_ + i * size_ + j*8);
            __m256 bufB = _mm256_load_ps(other.data_ + i * size_ + j*8);
            __m256 resbuf = _mm256_add_ps(bufA, bufB);
            _mm256_store_ps(this->data_ + i * size_ + j*8, resbuf);
        }

        for(size_t k = size_ & 0b11111111111111111111111111111000; k < size_; k++)
        {
            data_[i * size_ + k] += other.data_[i * size_ + k];
        }
    }
}


void Matrix::subtract(const Matrix &other) {
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_/8; j++) {
            __m256 bufA = _mm256_load_ps(this->data_ + i * size_ + j*8);
            __m256 bufB = _mm256_load_ps(other.data_ + i * size_ + j*8);
            __m256 resbuf = _mm256_sub_ps(bufA, bufB);
            _mm256_store_ps(this->data_ + i * size_ + j*8, resbuf);
        }

        for(size_t k = size_ & 0b11111111111111111111111111111000; k < size_; k++)
        {
            data_[i * size_ + k] += other.data_[i * size_ + k];
        }
    }
}

void Matrix::multiplyRight(const Matrix &other) {
    __m256i vindex = _mm256_set_epi32(7*size_, 6*size_, 5*size_, 4*size_, 3*size_, 2*size_, size_, 0);
    memset(buf_, 0, sizeof(float) * size_*size_);
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            for (size_t k = 0; k < size_/8; k++) {
                __m256 bufA = _mm256_load_ps(data_ + i*size_ + k*8);
                __m256 bufB = _mm256_i32gather_ps(other.data_ + k*8*size_ + j, vindex, 8);
                __m256 res = _mm256_dp_ps(bufA, bufB, 0b00000000);
                buf_[i*size_+j] += res[0];
                buf_[i*size_+j] += res[1];
            }
            for (size_t k = size_ & 0b11111111111111111111111111111000; k < size_; k++) {
                buf_[i*size_+j] += data_[i*size_ + k] * other.data_[k*size_ + j];
            }
        }
    }
    std::swap(buf_, data_);
    std::cout << "Multiplication done\n";
}

void Matrix::divide(float scalar) {
    __m256 scalar_vec = _mm256_set1_ps(scalar);
    for (size_t i = 0; i < size_; i++)
    {
        for (size_t j = 0; j < size_/8; j++) {
            __m256 buf = _mm256_load_ps(data_ + i*size_ + j*8);
            __m256 resbuf = _mm256_div_ps(buf, scalar_vec);
            _mm256_store_ps(data_ + i*size_ + j*8, resbuf);
        }

        for(size_t k = size_ & 0b11111111111111111111111111111000; k < size_; k++)
        {
            data_[i*size_ + k] /= scalar;
        }
    }
}

void Matrix::transpose() {
    for (size_t i = 0; i < size_/4; i++) {
        __m128 vec1 = _mm_load_ps(data_ + i*size_*4 + i*4);
        __m128 vec2 = _mm_load_ps(data_ + (i + 1)*size_*4 + i*4);
        __m128 vec3 = _mm_load_ps(data_ + (i + 2)*size_*4 + i*4);
        __m128 vec4 = _mm_load_ps(data_ + (i + 3)*size_*4 + i*4);

        _MM_TRANSPOSE4_PS(vec1,vec2,vec3, vec4);

        _mm_store_ps(data_ + i*size_*4 + i*4, vec1);
        _mm_store_ps(data_ + (i + 1)*size_*4 + i*4, vec2);
        _mm_store_ps(data_ + (i + 2)*size_*4 + i*4, vec3);
        _mm_store_ps(data_ + (i + 3)*size_*4 + i*4, vec4);

        for (size_t j = i + 1; j < size_/4; j++) {
            vec1 = _mm_load_ps(data_ + i*size_*4 + j*4);
            vec2 = _mm_load_ps(data_ + (i + 1)*size_*4 + j*4);
            vec3 = _mm_load_ps(data_ + (i + 2)*size_*4 + j*4);
            vec4 = _mm_load_ps(data_ + (i + 3)*size_*4 + j*4);

            __m128 vec5 = _mm_load_ps(data_ + j*size_*4 + i*4);
            __m128 vec6 = _mm_load_ps(data_ + (j + 1)*size_*4 + i*4);
            __m128 vec7 = _mm_load_ps(data_ + (j + 2)*size_*4 + i*4);
            __m128 vec8 = _mm_load_ps(data_ + (j + 3)*size_*4 + i*4);

            _MM_TRANSPOSE4_PS(vec1,vec2,vec3, vec4);
            _MM_TRANSPOSE4_PS(vec5,vec6,vec7, vec8);

            _mm_store_ps(data_ + j*size_*4 + i*4, vec1);
            _mm_store_ps(data_ + (j + 1)*size_*4 + i*4, vec2);
            _mm_store_ps(data_ + (j + 2)*size_*4 + i*4, vec3);
            _mm_store_ps(data_ + (j + 3)*size_*4 + i*4, vec4);

            _mm_store_ps(data_ + i*size_*4 + j*4, vec5);
            _mm_store_ps(data_ + (i + 1)*size_*4 + j*4, vec6);
            _mm_store_ps(data_ + (i + 2)*size_*4 + j*4, vec7);
            _mm_store_ps(data_ + (i + 3)*size_*4 + j*4, vec8);
        }
        for (size_t k = size_ & 0b11111111111111111111111111111100; k < size_; k++) {
            std::swap(data_[i*size_+k], data_[k*size_+i]);
            std::swap(data_[(i + 1)*size_+k],data_[k*size_+i+1]);
            std::swap(data_[(i + 2)*size_+k],data_[k*size_+i+2]);
            std::swap(data_[(i + 3)*size_+k], data_[k*size_+i+3]);
        }
    }
    for (size_t i = size_ & 0b11111111111111111111111111111100; i < size_; i++) {
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
//
//float hsum(__m256 inp) {
//    __m256 sign_bit = _mm256_set1_ps(-0.0f);
//    __m256 inp_abs = _mm256_andnot_ps(sign_bit, inp);
//    __m256 half_sum1 = _mm256_hadd_ps(inp, inp_abs);
//    __m256 half_sum2 = _mm256_hadd_ps(inp, half_sum1);
//    __m256 shuffle = _mm256_shuffle_ps(half_sum2, half_sum2, )
//
//}

float Matrix::Norm1() {
    auto sums = new float[size_];
    for (size_t j = 0; j < size_; j++)
    {
        sums[j] = 0;
        for (size_t i = 0; i < size_; i++)
        {
            sums[j] += std::abs(this->data_[i*size_ + j]);
        }
    }
    return *std::max_element(sums, sums + size_);
}
float Matrix::Norm2() {
    auto sums = new float[size_];
    for (size_t i = 0; i < size_; i++)
    {
        sums[i] = 0;
        for (size_t j = 0; j < size_; j++)
        {
            sums[i] += std::abs(this->data_[i*size_ + j]);
        }
    }
    return *std::max_element(sums, sums + size_);
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
    std::ifstream file;
    file.open("mtx.txt");
    for (size_t i = 0; i < size_; i++) {
        for (size_t j = 0; j < size_; j++) {
            file >> data_[i * size_ + j];
        }
    }
}

int main() {
    Matrix m(2048 );
    m.read();
    auto start = std::chrono::steady_clock::now();
    m.inverse(10);
    auto end = std::chrono::steady_clock::now();
    std::chrono::duration<double> elapsed_seconds = end-start;
    std::cout << "elapsed time: " << elapsed_seconds.count() << "s\n";
    return 0;
}
