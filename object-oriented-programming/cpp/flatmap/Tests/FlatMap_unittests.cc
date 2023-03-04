#include "FlatMap.hpp"
#include "gtest/gtest.h"

TEST(FlatMap, contructor) {
    FlatMap<int, int> a;
    EXPECT_EQ(0, a.size());
    EXPECT_TRUE(a.empty());

    FlatMap<int, int>* b = new FlatMap<int,int>();
    delete b;
}

TEST(FlatMap, operatorbraces) {
    FlatMap<int, int> a;

    for (int i = 0; i < 512; i++) {
        a[i] == 0;
    }
    EXPECT_EQ(a.size(), 512);
}

TEST(FlatMap, containment1) {
    FlatMap<int, int> a;
    int i1 = 8;
    int i2 = 4;
    EXPECT_TRUE(a.insert(1, i1));
    EXPECT_FALSE(a.insert(1, i2));
    FlatMap<int, int> b = a;
    EXPECT_EQ(8, b.at(1));
    int i3 = 5;
    EXPECT_TRUE(a.insert(4, i3));
    a.swap(b);
    EXPECT_EQ(5, b.at(4));
}

TEST(FlatMap, containment2) {
    FlatMap<int, int> a;
    int* b;
    for(int i = -10; i <= 10; i++)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(i,*b));
    }
    for(int i = -10; i <= 10; i++)
    {
        EXPECT_EQ(-i, a.at(i));
        EXPECT_EQ(-i, a[i]);
    }
}

TEST(FlatMap, containment3) {
    FlatMap<int, int> a;
    EXPECT_FALSE(a.contains(0));
    int *b;
    for(int i = -10; i <= 10; i++)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(2*i, *b));
    }
    
    for(int i = -10; i <= 10; i++)
    {
        EXPECT_EQ(-i, a.at(2*i));
        EXPECT_EQ(-i, a[2*i]);
    }
    EXPECT_FALSE(a.contains(-11));
    EXPECT_TRUE(a.contains(-10));
    EXPECT_ANY_THROW(a.at(-11));
    EXPECT_FALSE(a.empty());
    a.clear();
    EXPECT_TRUE(a.empty());
}

TEST(FlatMap, containment4) {
    FlatMap<int, int> a;
    int *b;
    for(int i = 10; i >= -10; i--)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(2*i, *b));
    }
    for(int i = 10; i >= 10; i--)
    {
        EXPECT_EQ(-i, a.at(2*i));
        EXPECT_EQ(-i, a[2*i]);
    }
}

TEST(FlatMap, containment5) {
    FlatMap<int, int> a;
    int *b;
    for(int i = -65536; i <= 65535; i++)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(i, *b));
    }
    for(int i = -65536; i <= 65535; i++)
    {
        EXPECT_EQ(-i, a.at(i));
        EXPECT_EQ(-i, a[i]);
    }

    for(int i = 0; i <= 65535; i++)
    {
        EXPECT_TRUE(a.erase(i));
    }

    for(int i = 0; i <= 65535; i++)
    {
        EXPECT_FALSE(a.erase(i));
    }
}    

TEST(FlatMap, containment6) {
    FlatMap<int, int> a;
    int *b;
    for(int i = 65535; i >= -65536; i--)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(i, *b));
    }
    for(int i = 65535; i >= -65536; i--)
    {
        EXPECT_EQ(-i, a.at(i));
        EXPECT_EQ(-i, a[i]);
    }
    for (int i = 65535; i >= -65536; i--)
    {
        EXPECT_TRUE(a.erase(i));
    }
    EXPECT_TRUE(a.empty());
}   

TEST(FlatMap, erase) {
    FlatMap<int, int> a;
    int *b;
    for(int i = 10; i >= -10; i--)
    {
        b = new int(-i);
        a.insert(2*i, *b);
    }
    for(int i = 10; i >= 0; i--)
    {
        EXPECT_TRUE(a.erase(2*i));
    }
    for(int i = 10; i >= 0; i--)
    {
        EXPECT_FALSE(a.erase(2*i));        
    }
    for(int i = 10; i >= 0; i--)
    {
        EXPECT_ANY_THROW(a.at(2*i));        
    }
    for(int i = 10; i >= 0; i--)
    {
        b = new int(-i);
        a.insert(2*i, *b);
    }
    for(int i = 0; i <= 10; i++)
    {
        EXPECT_TRUE(a.erase(2*i));
    }
    for(int i = 0; i <= 10; i++)
    {
        EXPECT_FALSE(a.erase(2*i));        
    }
    for (int i = 0; i <= 10; i++)
    {
        EXPECT_FALSE(a.contains(2*i));
    }
}

TEST(FlatMap, assign) {
    FlatMap<int, int> a;
    int *b;
    for(int i = 10; i >= -10; i--)
    {
        b = new int(-i);
        a.insert(2*i, *b);
    }
    FlatMap<int, int> c;
    c = a;
    for(int i = 10; i >= 10; i--)
    {
        EXPECT_EQ(-i, c.at(2*i));
    }
}

TEST(FlatMap, move) {
    FlatMap<int, int> a;
    int *b;
    for (int i = -10; i <= 10; i++)
    {
        b = new int(-i);
        EXPECT_TRUE(a.insert(i, *b));
    }
    FlatMap<int, int> c(std::move(a));
    EXPECT_TRUE(a.empty());

    for (int i = -10; i <= 10; i++)
    {
        EXPECT_EQ(-i, c.at(i));
    }
}
