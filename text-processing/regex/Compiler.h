#ifndef UNTITLED1_COMPILER_H
#define UNTITLED1_COMPILER_H


#include <stack>
#include "NFA.h"
#include "Tokenizer.h"

struct StateSeq {
  StateSeq(NFA& dfa, long stateId) : dfa(dfa), startId(stateId), endId(stateId) { };
  StateSeq(NFA& dfa, long stateId, long endId) : dfa(dfa), startId(stateId), endId(endId) { };


  StateSeq clone();

  void append(long);
  void append(const StateSeq&);
  NFA& dfa;
  std::vector<State> states;
  long startId = 0;
  long endId = 0;
};

class Compiler {
public:
  explicit Compiler(const std::string&);

  NFA getDFA();
private:
  void disjunctionMatch();
  void alternativeMatch();
  bool termMatch();
  bool assertionMatch();
  bool atomMatch();
  bool quantifierMatch();

  bool matchToken(Token);

  void insertCharMatcher();
  void insertAnyMatcher();
  void insertQuotedClassMatcher(char);

  StateSeq pop();

  std::stack<StateSeq> stack;
  std::string value;
  Tokenizer tokenizer;
  NFA dfa;
};


#endif //UNTITLED1_COMPILER_H
