#include <gtest/gtest.h>
#include "Compiler.h"
#include "NFARunner.h"
#include "NFA.h"

TEST(Operations, Groups) {
  Compiler compiler("(ab)+");
  auto dfa = compiler.getDFA();
  std::string str = "abab";
  NFARunner runner(str.begin(), str.end(), dfa);
  ASSERT_TRUE(runner.match().has_value());
  std::string str1 = "csb";
  NFARunner runner1(str1.begin(), str1.end(), dfa);
  ASSERT_FALSE(runner1.match().has_value());
}

TEST(Operations, Quantifiers) {
  Compiler compiler("ab{1,3}c");
  auto dfa = compiler.getDFA();
  std::string str = "abc";
  std::string str1 = "abbc";
  std::string str2 = "abbbc";
  std::string str3 = "abbbbc";

  NFARunner runner(str.begin(), str.end(), dfa);
  NFARunner runner1(str1.begin(), str1.end(), dfa);
  NFARunner runner2(str2.begin(), str2.end(), dfa);
  NFARunner runner3(str3.begin(), str3.end(), dfa);

  ASSERT_TRUE(runner.match().has_value());
  ASSERT_TRUE(runner1.match().has_value());
  ASSERT_TRUE(runner2.match().has_value());
  ASSERT_FALSE(runner3.match().has_value());
}

