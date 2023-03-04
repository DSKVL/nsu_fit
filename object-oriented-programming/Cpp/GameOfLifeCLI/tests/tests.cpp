#include "gtest/gtest.h"
#include "../AutomatonCalculator.h"
#include "../GameDumper.h"
#include "../GameField.h"
#include "../GameImporter.h"

TEST(CalculatorTest, test1) {
    AutomatonCalculator calculator;
    GameField field;

    // .O.
    // ..O
    // OOO
    field.set(Cell(true), 1, 0);
    field.set(Cell(true), 2, 1);
    field.set(Cell(true), 0, 2);
    field.set(Cell(true), 1, 2);
    field.set(Cell(true), 2, 2);

    ASSERT_TRUE(field.at(1, 0).getStatus());
    ASSERT_TRUE(field.at(2, 1).getStatus());
    ASSERT_TRUE(field.at(0, 2).getStatus());
    ASSERT_TRUE(field.at(1, 2).getStatus());
    ASSERT_TRUE(field.at(2, 2).getStatus());

    auto next = calculator.next(field);

    ASSERT_TRUE(next.at(0, 1).getStatus());
    ASSERT_TRUE(next.at(2, 1).getStatus());
    ASSERT_TRUE(next.at(1, 2).getStatus());
    ASSERT_TRUE(next.at(2, 2).getStatus());
    ASSERT_TRUE(next.at(1, 3).getStatus());
}

TEST(GameImporterTest, test1) {
    std::ofstream out("testcfg");
    out << "#Life 1.06\n#N aaaa\n#R B123\\S123\n1 2\n3 1\n-1 2\n";
    out.close();
    std::string supposedName("aaaa");
    Rule supposeRule({false, true, true, true, false, false, false, false, false},
                     {false, true, true, true, false, false, false, false, false});
    GameImporter imp(std::filesystem::path("testcfg"));
    imp.import();

    ASSERT_TRUE(std::equal(supposedName.begin(), supposedName.end(), imp.getName().value().begin()));
    for (int i = 0; i < 9; i++) {
        ASSERT_TRUE(supposeRule.survives(true, i) == imp.getRule().survives(true, i));
        ASSERT_TRUE(supposeRule.survives(false, i) == imp.getRule().survives(false, i));
    }

    auto field = imp.getGameField();
    ASSERT_TRUE(field.at(1, 2).getStatus());
    ASSERT_TRUE(field.at(3, 1).getStatus());
}

TEST(GameDumperTest, test1) {
    AutomatonCalculator calculator;
    GameField field;

    //O..O...
    //....O..
    //O...O..
    //.OOOO..
    field.set(Cell(true), 0, 0);
    field.set(Cell(true), 3, 0);
    field.set(Cell(true), 4, 1);
    field.set(Cell(true), 0, 2);
    field.set(Cell(true), 4, 2);
    field.set(Cell(true), 1, 3);
    field.set(Cell(true), 2, 3);
    field.set(Cell(true), 3, 3);
    field.set(Cell(true), 4, 3);

    auto next = calculator.next(field);

    //........
    //...OO...
    //.OO.OO..
    //.OOOO...
    //..OO....
    ASSERT_TRUE(next.at(3, 1).getStatus());
    ASSERT_TRUE(next.at(4, 1).getStatus());
    ASSERT_TRUE(next.at(1, 2).getStatus());
    ASSERT_TRUE(next.at(2, 2).getStatus());
    ASSERT_TRUE(next.at(4, 2).getStatus());
    ASSERT_TRUE(next.at(5, 2).getStatus());
    ASSERT_TRUE(next.at(1, 3).getStatus());
    ASSERT_TRUE(next.at(2, 3).getStatus());
    ASSERT_TRUE(next.at(3, 3).getStatus());
    ASSERT_TRUE(next.at(4, 3).getStatus());
    ASSERT_TRUE(next.at(2, 4).getStatus());
    ASSERT_TRUE(next.at(3, 4).getStatus());

    std::filesystem::path path("dump");
    GameDumper dumper(path);

    Rule defaultRule;
    dumper.dump(defaultRule, next);

    GameImporter importer(path);
    importer.import();

    auto nextImported = importer.getGameField();

    ASSERT_TRUE(nextImported.at(3, 1).getStatus());
    ASSERT_TRUE(nextImported.at(4, 1).getStatus());
    ASSERT_TRUE(nextImported.at(1, 2).getStatus());
    ASSERT_TRUE(nextImported.at(2, 2).getStatus());
    ASSERT_TRUE(nextImported.at(4, 2).getStatus());
    ASSERT_TRUE(nextImported.at(5, 2).getStatus());
    ASSERT_TRUE(nextImported.at(1, 3).getStatus());
    ASSERT_TRUE(nextImported.at(2, 3).getStatus());
    ASSERT_TRUE(nextImported.at(3, 3).getStatus());
    ASSERT_TRUE(nextImported.at(4, 3).getStatus());
    ASSERT_TRUE(nextImported.at(2, 4).getStatus());
    ASSERT_TRUE(nextImported.at(3, 4).getStatus());
}



