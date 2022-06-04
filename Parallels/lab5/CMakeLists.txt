cmake_minimum_required(VERSION 3.22)
project(lab5 C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_COMPILER "/usr/lib64/openmpi/bin/mpicc")
set(CMAKE_CXX_COMPILER "/usr/lib64/openmpi/bin/mpic++")
include_directories(/usr/include/openmpi-x86_64)
add_executable(lab5 main.c main.c)
