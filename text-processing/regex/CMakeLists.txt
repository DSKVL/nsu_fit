cmake_minimum_required(VERSION 3.25)
project(untitled1)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
FetchContent_MakeAvailable(googletest)

add_executable(untitled1
        main.cpp
        Compiler.h
        Compiler.cpp
        Tokenizer.h
        Tokenizer.cpp
        NFA.h
        NFA.cpp

        #NFARunner.cpp
        Tokenizer.cpp NFARunner.cpp)

enable_testing()

add_executable(
        hello_test
        tests.cpp
        Compiler.h
        Compiler.cpp
        Tokenizer.h
        Tokenizer.cpp
        NFA.h
        NFA.cpp
        NFARunner.cpp
)
target_link_libraries(
        hello_test
        GTest::gtest_main
)

include(GoogleTest)
gtest_discover_tests(hello_test)