#include <gtest/gtest.h>
#include "../Headers/ConsoleHandler.h"
#include "../Headers/GameExceptions.h"
#include "../Headers/IterativePrisonersDilemma.h"
#include "../Headers/FileManager.h"
#include "../Headers/Response.h"

TEST(ConsoleHandler, test1) {
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg3};

    ConsoleHandler console(argc, argv);
    EXPECT_EQ(Mode::FAST, console.parseMode());
    auto players = console.parsePlayers();
    EXPECT_EQ(players[0], "AlwaysCooperate");
    EXPECT_EQ(console.parseSteps(), 5);

}

TEST(ConsoleHandler, test2) {
    constexpr int argc=10;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--steps=5";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "--mode=fast";
    const char arg5[] = "--mode=detailed";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg3, arg4, arg5, arg6, arg7};

    ConsoleHandler console(argc, argv);
    auto players = console.parsePlayers();
    EXPECT_EQ(players[0], "AlwaysCooperate");
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseMode();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}

TEST(ConsoleHandler, test3) {
    constexpr int argc=10;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--steps=5";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "--mode=tournament";
    const char arg5[] = "--mode=tournament";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg3, arg4, arg5, arg6, arg7};

    ConsoleHandler console(argc, argv);
    auto players = console.parsePlayers();
    EXPECT_EQ(players[0], "AlwaysCooperate");
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseMode();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}


TEST(ConsoleHandler, test4) {
    constexpr int argc=8;
    const char arg0[] = "test";
    const char arg1[] ="--steps=a";
    const char arg2[] = "--steps=a";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "--mode=tournament";
    const char arg5[] = "--mode=tournament";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7};

    ConsoleHandler console(argc, argv);
    EXPECT_THROW({console.parsePlayers();}, bad_players_exception);
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseMode();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}

TEST(ConsoleHandler, test5) {
    constexpr int argc=8;
    const char arg0[] = "test";
    const char arg1[] ="--steps=a";
    const char arg2[] = "--steps=a";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "--mode=detailed";
    const char arg5[] = "--mode=detailed";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7};

    ConsoleHandler console(argc, argv);
    EXPECT_THROW({console.parsePlayers();}, bad_players_exception);
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseMode();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}

TEST(ConsoleHandler, test6) {
    constexpr int argc=8;
    const char arg0[] = "test";
    const char arg1[] ="--steps=a";
    const char arg2[] = "--steps=a";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "--mode=fast";
    const char arg5[] = "--mode=fast";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7};

    ConsoleHandler console(argc, argv);
    EXPECT_THROW({console.parsePlayers();}, bad_players_exception);
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseMode();}, bad_command_line_argument_exception);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}


TEST(ConsoleHandler, test7) {
    constexpr int argc=9;
    const char arg0[] = "test";
    const char arg1[] ="--steps=a";
    const char arg2[] = "--steps=a";
    const char arg3[] = "AlwaysCooperate";
    const char arg6[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char arg7[] = "--configs=adsfasfd";

    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg3, arg3,  arg6, arg7};

    ConsoleHandler console(argc, argv);
    EXPECT_THROW({console.parseSteps();}, bad_command_line_argument_exception);
    EXPECT_EQ(console.parseMode(), Mode::TOURNAMENT);
    EXPECT_THROW({console.parseConfigs();};, bad_command_line_argument_exception);
}


TEST(IPD, test1) {
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg3};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_TRUE(ipd.isValid());
    auto res = ipd.start();
    EXPECT_EQ(res.score_by_id[0], 35);
    EXPECT_EQ(res.score_by_id[1], 35);
    EXPECT_EQ(res.score_by_id[2], 35);
}

TEST(IPD, test2) {
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysDefect";
    const char arg4[] = "Deduction";
    const char arg5[] = "SoftTitForTat";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_TRUE(ipd.isValid());
    auto res = ipd.start();
    EXPECT_EQ(res.score_by_id[0], 17);
    EXPECT_EQ(res.score_by_id[1], 11);
    EXPECT_EQ(res.score_by_id[2], 6);
}

TEST(IPD, test3) {
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=5";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "ToughTitForTat";
    const char arg4[] = "Deduction";
    const char arg5[] = "SoftTitForTat";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_TRUE(ipd.isValid());
    auto res = ipd.start();
    EXPECT_EQ(res.score_by_id[0], 17);
    EXPECT_EQ(res.score_by_id[1], 23);
    EXPECT_EQ(res.score_by_id[2], 12);
}

TEST(IPD, test4){
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--mode=xdf";
    const char arg3[] = "Tsadf";
    const char arg4[] = "asdf";
    const char arg5[] = "dff";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(IPD, test5){
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--matrix=asdasda";
    const char arg3[] = "Tsadf";
    const char arg4[] = "asdf";
    const char arg5[] = "dff";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(IPD, test6){
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--matrix=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx_bad";
    const char arg3[] = "Tsadf";
    const char arg4[] = "asdf";
    const char arg5[] = "dff";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(IPD, test7){
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--matrix=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx_bad";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "AlwaysCooperate";
    const char arg5[] = "AlwaysCooperate";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}


TEST(IPD, test8){
    constexpr int argc=7;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--matrix=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "AlwaysCooperate";
    const char arg5[] = "AlwaysCooperate";
    const char* argv[argc] = {arg0, arg1, arg2, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(IPD, test9){
    constexpr int argc=6;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx_bad";
    const char arg3[] = "Tsadf";
    const char arg4[] = "asdf";
    const char arg5[] = "dff";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}



TEST(Metastrategies, test1) {
    constexpr int argc=7;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "SoftTitForTatDeduction";
    const char arg5[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_TRUE(ipd.isValid());
    auto res = ipd.start();
    EXPECT_EQ(res.score_by_id[0], 54);
    EXPECT_EQ(res.score_by_id[1], 54);
    EXPECT_EQ(res.score_by_id[2], 78);
}

TEST(Metastrategies, test2) {
    constexpr int argc=7;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "RandomToughTitForTatDeduction";
    const char arg5[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_TRUE(ipd.isValid());
    auto res = ipd.start();
}

TEST(Metastrategies, test3) {
    constexpr int argc=7;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "RandomToughTitForTatDeduction";
    const char arg5[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs_bad1";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(Metastrategies, test4) {
    constexpr int argc=7;
    const char arg0[] = "test";
    const char arg1[] ="--steps=10";
    const char arg2[] = "--mode=fast";
    const char arg3[] = "AlwaysCooperate";
    const char arg4[] = "RandomToughTitForTatDeduction";
    const char arg5[] = "--configs=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/test_cfgs_bad2";
    const char* argv[argc] = {arg0, arg1, arg2, arg3, arg3, arg4, arg5};
    IterativePrisonersDilemma ipd(argc, argv);
    EXPECT_FALSE(ipd.isValid());
}

TEST(PayoffsRule, test1) {
    constexpr int argc = 2;
    const char arg0[] = "test";
    const char arg1[] ="--matrix=/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx";
    const char* argv[argc] = {arg0, arg1};

    ConsoleHandler cons(argc, argv);
    EXPECT_EQ("/home/dskvl/NSU/OOP/20201_koval/PrisonersDilemma/Tests/mtx", cons.parsePayoffsFile());
    auto rule= FileManager::parsePayoffsFile(cons.parsePayoffsFile());
    Response CCC[3] = {Response::COOPERATE, Response::COOPERATE, Response::COOPERATE};
    Response CCD[3] = {Response::COOPERATE, Response::COOPERATE, Response::DEFECT};
    Response CDC[3] = {Response::COOPERATE, Response::DEFECT, Response::COOPERATE};
    Response CDD[3] = {Response::COOPERATE, Response::DEFECT, Response::DEFECT};
    Response DCC[3] = {Response::DEFECT, Response::COOPERATE, Response::COOPERATE};
    Response DCD[3] = {Response::DEFECT, Response::COOPERATE, Response::DEFECT};
    Response DDC[3] = {Response::DEFECT, Response::DEFECT, Response::COOPERATE};
    Response DDD[3] = {Response::DEFECT, Response::DEFECT, Response::DEFECT};

    std::array<int, 3> expected = {10, 10, 10};
    EXPECT_EQ(expected, rule.getPayoffs(CCC));
    expected = {6, 6, 12};
    EXPECT_EQ(expected, rule.getPayoffs(CCD));
    expected = {6, 12, 6};
    EXPECT_EQ(expected, rule.getPayoffs(CDC));
    expected = {3, 8, 8};
    EXPECT_EQ(expected, rule.getPayoffs(CDD));
    expected = {12, 6, 6};
    EXPECT_EQ(expected, rule.getPayoffs(DCC));
    expected = {8, 3, 8};
    EXPECT_EQ(expected, rule.getPayoffs(DCD));
    expected = {8, 8, 3};
    EXPECT_EQ(expected, rule.getPayoffs(DDC));
    expected = {4, 4, 4};
    EXPECT_EQ(expected, rule.getPayoffs(DDD));
}
