cmake_minimum_required(VERSION 3.21)
project(PrisonersDilemma)

set(CMAKE_CXX_STANDARD 20)
add_executable(PrisonersDilemma
        main.cpp
        Implementations/ConsoleHandler.cpp
        Headers/ConsoleHandler.h
        Implementations/PlayersManager.cpp
        Headers/PlayersManager.h
        Implementations/Game.cpp
        Headers/Game.h
        Implementations/GameOutcome.cpp
        Headers/GameOutcome.h
        Implementations/GameExceptions.cpp
        Headers/GameExceptions.h
        Headers/Strategy.h
        Implementations/RoundOutcome.cpp
        Headers/RoundOutcome.h
        Implementations/FileManager.cpp
        Headers/FileManager.h
        Implementations/PayoffsRule.cpp
        Headers/PayoffsRule.h
        Headers/Factory.h
        Headers/FlatMap.h
        Implementations/Metastrategy.cpp
        Headers/Metastrategy.h
        Headers/Response.h
        Strategies/AlwaysCooperate.cpp
        Strategies/AlwaysCooperate.h
        Strategies/AlwaysDefect.cpp
        Strategies/AlwaysDefect.h
        Strategies/Random.cpp
        Strategies/Random.h
        Strategies/ToughTitForTat.cpp
        Strategies/ToughTitForTat.h
        Strategies/SoftTitForTat.cpp
        Strategies/SoftTitForTat.h
        Implementations/Strategy.cpp
        Strategies/Deduction.cpp
        Strategies/Deduction.h
        Implementations/IterativePrisonersDilemma.cpp
        Headers/IterativePrisonersDilemma.h)

add_subdirectory(Tests)

