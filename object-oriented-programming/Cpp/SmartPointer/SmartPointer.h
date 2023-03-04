#include <iostream>

template<class T>
class SmartPointer {
public:
    explicit SmartPointer(T * p) : p_(p) {}
    ~SmartPointer() {
        delete p_;
    }
    template<class U>
    SmartPointer(U &&);
    T& release();
    T* operator->();
    T& operator*();
    T* get();
    void reset(T * p);
    explicit  operator bool();                           // ДЗ
    void swap(SmartPointer& other);        // ДЗ
    template<class U>
    SmartPointer& operator=(SmartPointer<U>&& other);

private:
    T * p_ = nullptr;
    T& operator=(SmartPointer&);
};

template<class T>
template<class P>
SmartPointer<T>::SmartPointer(P && other)
{
    p_ = other.p_;
    other.p_ = nullptr;
}

template<class T>
template<class U>
SmartPointer<T>& SmartPointer<T>::operator=(SmartPointer<U>&& other)
{
    p_ = other.p_;
    other.p_ = nullptr;
}

template<class T>
T& SmartPointer<T>::release()
{
    T& v = *p_;
    p_ = nullptr;
    return v;
}
template<class T>
void SmartPointer<T>::swap(SmartPointer& other)
{
    T * t = other.p_;
    other.p_ = p_;
    p_ = t;
}

template<class T>
SmartPointer<T>::operator bool() { return (p_ != nullptr); }
template<class T>
T* SmartPointer<T>::operator->() { return p_; }
template<class T>
T& SmartPointer<T>::operator*() { return *p_; }
template<class T>
T* SmartPointer<T>::get() { return p_; }
template<class T>
void SmartPointer<T>::reset(T *p)
{
    delete p_;
    p_ = p;
}

struct Str {
    int a;
    int b;
};

int main()
{
    SmartPointer<Str> A(new Str);
    (*A).a = 1;
    (*A).b = 2;
    std::cout << A->a << " " << A->b;
    return 0;
}
