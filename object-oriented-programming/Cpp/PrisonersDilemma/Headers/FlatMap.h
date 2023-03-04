#ifndef PRISONERSDILEMMA_FLATMAP_H
#define PRISONERSDILEMMA_FLATMAP_H

#include <cstddef>
#include <algorithm>
#include <utility>
#include <stdexcept>


template<class Key, class Value>
class FlatMap
{
public:
    FlatMap();
    ~FlatMap();

    FlatMap(const FlatMap& b);
    FlatMap(FlatMap&& b);

    void swap(FlatMap& b);

    FlatMap& operator=(const FlatMap& b);
    FlatMap& operator=(FlatMap&& b);


    void clear();
    bool erase(const Key& k);
    bool insert(const Key& k, Value& v);
    bool contains(const Key& k) const;

    Value& operator[](const Key& k);
    Value& at(const Key& k);
    const Value& at(const Key& k) const;

    size_t size() const;
    bool empty() const;

    template<class K, class V>
    friend bool operator==(const FlatMap<K, V>& a, const FlatMap<K,V>& b);
    template<class K, class V>
    friend bool operator!=(const FlatMap<K, V>& a, const FlatMap<K,V>& b);

private:
    size_t size_ = 0;
    size_t capacity_ = 1;
    Key* Keys_ = nullptr;
    Value* Values_ = nullptr;

    bool binary_search(const Key& k, size_t* index) const;
    bool insert(const Key& k, const Value& v, Value** contained_elem);
    void resize();
};

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap()
        : size_(0), capacity_(1)
{
    Keys_ = new Key[1];
    Values_ = new Value[1];
}

template<class Key, class Value>
FlatMap<Key, Value>::~FlatMap()
{
    delete[] Keys_;
    delete[] Values_;
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(const FlatMap<Key, Value>& b)
        : size_(b.size_), capacity_(b.capacity_)
{
    Keys_ = new Key[capacity_];
    Values_ = new Value[capacity_];
    std::copy(b.Keys_, b.Keys_+b.size_, Keys_);
    std::copy(b.Values_, b.Values_+b.size_, Values_);
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(FlatMap<Key, Value>&& b)
        : size_(b.size_), capacity_(b.capacity_)
{
    Keys_ = b.Keys_;
    Values_ = b.Values_;

    b.Keys_ = new Key[1];
    b.Values_ = new Value[1];
    b.size_ = 0;
    b.capacity_ = 1;
}

template<class Key, class Value>
void FlatMap<Key, Value>::swap(FlatMap& b)
{
    FlatMap<Key, Value> t(std::move(b));
    b = std::move(*this);
    *this = std::move(t);
}

template<class Key, class Value>
FlatMap<Key, Value>& FlatMap<Key, Value>::operator=(const FlatMap<Key, Value>& b)
{
    if (this != &b)
    {
        size_ = b.size_;
        capacity_ = b.capacity_;

        delete[] Keys_;
        Keys_ = new Key[capacity_];
        std::copy(b.Keys_, b.Keys_ + b.capacity_, Keys_);

        delete[] Values_;
        Values_ = new Value[capacity_];
        std::copy(b.Values_, b.Values_ + b.capacity_, Values_);
    }
    return *this;
}

template<class Key, class Value>
FlatMap<Key, Value>& FlatMap<Key, Value>::operator=(FlatMap<Key, Value>&& b)
{
    delete[] Keys_;
    delete[] Values_;

    size_ = b.size_;
    capacity_ = b.capacity_;
    Keys_= b.Keys_;
    Values_ = b.Values_;

    b.Keys_ = new Key[1];
    b.Values_ = new Value[1];
    b.size_ = 0;
    b.capacity_ = 1;

    return *this;
}

template<class Key, class Value>
void FlatMap<Key, Value>::clear()
{
    size_ = 0;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::erase(const Key& k)
{
    size_t idx;
    if (binary_search(k, &idx))
    {
        while (idx !=  size_ - 1)
        {
            Values_[idx] = Values_[idx + 1];
            Keys_[idx] = Keys_[idx + 1];
            idx++;
        }
        size_--;
        return true;
    }
    return false;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::insert(const Key& k, Value& v)
{
    size_t idx;
    if(!binary_search(k, &idx))
    {
        if(size_>= capacity_)
        {
            resize();
        }

        for(size_t i = size_; i > idx; i--)
        {
            Keys_[i] = Keys_[i-1];
            Values_[i] = Values_[i-1];
        }


        Keys_[idx] = k;
        Values_[idx] = v;
        size_++;
        return true;
    }

    return false;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::contains(const Key& k) const
{
    if(size_ == 0)
    {
        return false;
    }

    size_t l = 0;
    size_t r = size_ - 1;
    while (r >= l)
    {
        size_t mid = l + (r - l)/2;
        if (Keys_[mid] == k)
        {
            return true;
        }

        if (k > Keys_[mid])
        {
            l = mid+1;
        }
        else
        {
            r = mid - 1;
        }
    }

    return false;
}

template<class Key, class Value>
Value& FlatMap<Key, Value>::operator[](const Key& k)
{
    size_t idx;
    if(binary_search(k, &idx))
    {
        return Values_[idx];
    }
    else
    {
        Value* v = nullptr;
        auto t = new Value();
        insert(k, *t, &v);
        return *v;
    }
}

template<class Key, class Value>
Value& FlatMap<Key, Value>::at(const Key& k)
{
    size_t idx;
    if(binary_search(k, &idx))
    {
        return Values_[idx];
    }
    else
    {
        throw std::out_of_range("aaaaa");
    }
}

template<class Key, class Value>
const Value& FlatMap<Key, Value>::at(const Key& k) const
{
    size_t idx;
    if(binary_search(k, &idx))
    {
        return Values_[idx];
    }

    throw std::out_of_range("aaaaa");
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::size() const
{
    return size_;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::empty() const
{
    return size_ == 0;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::binary_search(const Key& k, size_t* index) const
{
    if(size_ == 0)
    {
        *index = 0;
        return false;
    }

    long long l = 0;
    long long r = size_ - 1;
    while (r >= l)
    {
        size_t mid = l + (r - l)/2;
        if (Keys_[mid] == k)
        {
            *index = mid;
            return true;
        }

        if (k > Keys_[mid])
        {
            l = mid+1;
        }
        else
        {
            r = mid - 1;
        }
    }

    *index = l;
    return false;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::insert(const Key& k, const Value& v, Value** contained_elem)
{
    size_t idx;
    if(!binary_search(k, &idx))
    {
        if(size_>= capacity_)
        {
            resize();
        }

        for(size_t i = size_; i > idx; i--)
        {
            Keys_[i] = Keys_[i-1];
            Values_[i] = Values_[i-1];
        }


        Keys_[idx] = k;
        Values_[idx] = v;
        *contained_elem = Values_+idx;
        size_++;
        return true;
    }

    return false;
}

template<class Key, class Value>
void FlatMap<Key,Value>::resize()
{
    Key* tmp_k = new Key[2*capacity_];
    Value* tmp_v = new Value[2*capacity_];
    std::copy(Keys_, Keys_ + capacity_, tmp_k);
    std::copy(Values_, Values_ + capacity_, tmp_v);
    capacity_ *= 2;
    delete[] Keys_;
    delete[] Values_;
    Keys_ = tmp_k;
    Values_= tmp_v;
}

#endif //PRISONERSDILEMMA_FLATMAP_H
