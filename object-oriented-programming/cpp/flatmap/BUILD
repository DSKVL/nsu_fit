cc_library(
    name = "FlatMap",
    hdrs = ["FlatMap.hpp"],
    includes = ["include"],
    visibility = [
        "//visibility:public",
    ]
)

load("@rules_cc//cc:defs.bzl", "cc_test")

cc_test(
    name = "FlatMapTest",
    size = "small",
    srcs = ["FlatMap_unittests.cc"],
    deps = [":FlatMap",
            "@com_google_googletest//:gtest_main"],
)

cc_binary(
    name = "main",
    srcs = ["main.cpp"],
    deps = [":FlatMap",
            "@com_google_googletest//:gtest_main"],
)