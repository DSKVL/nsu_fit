#include "../stack.h"
#include "gtest/gtest.h"
#include "bits/stdc++.h"

using namespace std::chrono_literals;

TEST(test, first) {
    stack<int> st{};

    int a = 1;
    int b = 2;
    int c = 3;

    st.put(a);
    ASSERT_EQ(st.top(), 1);

    st.put(b);
    ASSERT_EQ(st.pop(), 2);
    ASSERT_EQ(st.top(), 1);

    st.put(c);
    ASSERT_EQ(st.top(), 3);
    ASSERT_EQ(st.pop(), 3);
    ASSERT_EQ(st.pop(), 1);
}

TEST(test, second) {
     stack<int> st{};
     int a = 1;

     st.put(a);

     auto aa = std::chrono::high_resolution_clock::now();
     std::thread thread([](stack<int>& st) {
		     for (int i = 0; i < 1000000; i++) {
			 ASSERT_EQ(st.top(), 1);
		     }
		     }, std::ref(st));

     thread.join();

     auto bb = std::chrono::high_resolution_clock::now();

     std::vector<std::thread> threads{};
     for (int i = 0; i < 4; i++)
     	 threads.push_back(std::thread([](stack<int>& st) {
             for (int i = 0; i < 1000000; i++) {
                 ASSERT_EQ(st.top(), 1);
             }}, std::ref(st)));

    for (int i = 0; i < 4; i++)
        threads[i].join();

    auto cc = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> dur1 = cc - bb;
    std::chrono::duration<double> dur2 = bb - aa;
    std::cout <<dur1.count()/dur2.count() << "\n";
    ASSERT_LE(dur1.count()/dur2.count(), 5);
}

TEST(test, third) {
     stack<int> st{};
     int a = 1;

     auto aa = std::chrono::high_resolution_clock::now();
     std::thread thread([](stack<int>& st) {
         st.put(1);
         st.put(2);
         st.put(3);
         st.put(4);
         std::this_thread::sleep_for(5s);
         st.put(5);

     }, std::ref(st));

     std::thread thread2([](stack<int>& st) {
         auto aa = std::chrono::high_resolution_clock::now();

         st.pop();
         st.pop();
         st.pop();
         st.pop();
         st.pop();

         auto bb = std::chrono::high_resolution_clock::now();
         std::chrono::duration<double> dur = bb-aa;

         ASSERT_GE(dur.count(), 4);
         }, std::ref(st));

     thread.join();
     thread2.join();
}

TEST(test, fourth) {
    stack<int> st{};

    int a = 1;
    int b = 2;
    int c = 3;

    st.put(a);
    st.put(b);
    st.put(c);

    EXPECT_ANY_THROW({st.map([](int& val) {
        throw std::exception();
    });});

    st.put(a);
    ASSERT_EQ(st.pop(), 1);
    ASSERT_EQ(st.pop(), 3);
    ASSERT_EQ(st.pop(), 2);
    ASSERT_EQ(st.pop(), 1);
}