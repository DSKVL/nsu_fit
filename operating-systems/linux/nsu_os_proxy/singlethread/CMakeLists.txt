cmake_minimum_required(VERSION 3.24)
project(nsu_os_proxy)

add_compile_options(-fsanitize=address)
add_link_options(-fsanitize=address)

set(CMAKE_CXX_STANDARD 20)

add_executable(proxy main.cpp headers/ClientConnection.h sources/ClientConnection.cpp sources/HTTPMessage.cpp sources/picohttpparser.cpp sources/Proxy.cpp sources/Request.cpp sources/Response.cpp sources/ServerConnection.cpp sources/URLResource.cpp)
