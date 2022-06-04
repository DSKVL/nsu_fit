cmake_minimum_required(VERSION 3.22)
project(g1)

set(CMAKE_CXX_STANDARD 23)

find_package(Curses REQUIRED)
include_directories(${CURSES_INCLUDE_DIR})

add_executable(g1 main.cpp)
target_link_libraries(g1 ${CURSES_LIBRARIES})