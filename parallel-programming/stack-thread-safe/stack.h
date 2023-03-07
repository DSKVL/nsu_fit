#ifndef LAB6_STACK_H
#define LAB6_STACK_H
#include <shared_mutex>
#include <functional>
#include <condition_variable>
#include <thread>

template<typename T>
class stack {
public:
    stack();
    ~stack();
    void put(const T&);
    T& pop();
    T& top() const;
    void map(std::function<void(T&)>);

private:
    T* data_;
    size_t size_ = 0;
    size_t capacity_ = 0;

    mutable std::shared_mutex mutex_;
    mutable std::condition_variable_any notEmptyCheck_;
    void resize();
};

#include <thread>
#include <condition_variable>
#include "stack.h"

template<typename T>
stack<T>::stack() {
    data_ = new T[1];
    capacity_ = 1;
    size_ = 0;
}

template<typename T>
stack<T>::~stack() {
    delete[] data_;
}

template<typename T>
void stack<T>::put(const T& value) {
    std::unique_lock<std::shared_mutex> locker(mutex_);
    if (size_ < capacity_) {
        resize();
    }
    data_[size_++] = value;
    if (size_ == 1) {
        notEmptyCheck_.notify_one();
    }

}
template<typename T>
T& stack<T>::pop() {
    std::unique_lock<std::shared_mutex> locker(mutex_);
    if (size_ == 0) {
        notEmptyCheck_.wait(locker, [&](){ return size_ != 0; });
    }
    return data_[--size_];
}

template<typename T>
T& stack<T>::top() const {
    {
        std::unique_lock<std::shared_mutex> locker(mutex_);
        if (size_ >= 1 ) return data_[size_-1];
    }
    {
        std::unique_lock<std::shared_mutex> locker(mutex_);
        if (size_ == 0) {
            notEmptyCheck_.wait(locker, [&]() { return size_ != 0; });
        }
        return data_[size_-1];
    }
}

template<typename T>
void stack<T>::map(std::function<void(T&)> functor) {
    std::lock_guard<std::shared_mutex> locker(mutex_);
    std::for_each(data_, data_+size_, functor);
}

template<typename T>
void stack<T>::resize() {
    T* t = new T[capacity_*2];
    std::copy(data_, data_ + size_, t);
    capacity_ *= 2;
    std::swap(data_, t);
    delete[] t;
}



#endif //LAB6_STACK_H
