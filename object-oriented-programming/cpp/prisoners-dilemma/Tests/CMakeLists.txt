# 'Google_test' is the subproject name
project(Google_tests)

# 'lib' is the folder with Google Test sources
add_subdirectory(lib/googletest-main)
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

# 'Google_Tests_run' is the target name
# 'test1.cpp tests2.cpp' are source files with tests
add_executable(Google_Tests_run Test.cpp ../Implementations/ConsoleHandler.cpp
        ../Headers/ConsoleHandler.h
        ../Implementations/PlayersManager.cpp
        ../Headers/PlayersManager.h
        ../Implementations/Game.cpp
        ../Headers/Game.h
        ../Implementations/GameOutcome.cpp
        ../Headers/GameOutcome.h
        ../Implementations/GameExceptions.cpp
        ../Headers/GameExceptions.h
        ../Headers/Strategy.h
        ../Implementations/RoundOutcome.cpp
        ../Headers/RoundOutcome.h
        ../Implementations/FileManager.cpp
        ../Headers/FileManager.h
        ../Implementations/PayoffsRule.cpp
        ../Headers/PayoffsRule.h
        ../Headers/Factory.h
        ../Headers/FlatMap.h
        ../Implementations/Metastrategy.cpp
        ../Headers/Metastrategy.h
        ../Headers/Response.h
        ../Strategies/AlwaysCooperate.cpp
        ../Strategies/AlwaysCooperate.h
        ../Strategies/AlwaysDefect.cpp
        ../Strategies/AlwaysDefect.h
        ../Strategies/Random.cpp
        ../Strategies/Random.h
        ../Strategies/ToughTitForTat.cpp
        ../Strategies/ToughTitForTat.h
        ../Strategies/SoftTitForTat.cpp
        ../Strategies/SoftTitForTat.h
        ../Implementations/Strategy.cpp
        ../Strategies/Deduction.cpp
        ../Strategies/Deduction.h
        ../Implementations/IterativePrisonersDilemma.cpp
        ../Headers/IterativePrisonersDilemma.h)
target_link_libraries(Google_Tests_run gtest gtest_main)