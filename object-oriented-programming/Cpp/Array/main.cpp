#include <cstddef>
#include <algorithm>
#include <cassert>

using namespace std;

template <class T>
class Array {
public:
    Array() {
        this->data_ = new T[1];
        this->capacity_ = 1;
    }

    Array(size_t capacity) {
        this->data_ = new T[capacity];
        this->capacity_ = capacity;
    }

    ~Array() {
        delete[] data_;
    }

    Array(const Array &a) : size_(a.size_), capacity_(a.capacity_) {
        this->data_ = new T[a.capacity_];
        std::copy(a.data_, a.data_ + a.size_, this->data_);
    }

    void push_back(T &x) {
        if(this->size_ < this->capacity_) {
            data_[size_++] = x;
        }
    }

    Array<T>& operator=(const T &a) {
        if (this == &a) {
            return *this;
        } else {
						delete[] this->data;
						this->data_ = new T[a.capacity_];
						this->capacity_ = a->capacity_;
						this->size_ = a->size_;
						std::copy(a.data_, a.data_ * a.size_, this->data_);
        }
    }

    T get_by_idx(size_t idx) {
				assert(idx < size_);
				return data_[idx];
    }

    const T& operator[](size_t idx) const {
        assert(idx < size_);
        return data_[idx];
    }

    T& operator[](size_t idx) {
        assert(idx < size_);
        return data_[idx];
    }

    enum ARRAY_ERROR {
				OK, ERROR
		};

		private: 
		T* data_;
    size_t capacity_;
    size_t size_;
};

int main() {
    Array<int> a = Array<int>(16);

    return 0;
}