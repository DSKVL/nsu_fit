#ifndef UNTITLED1_NFA_H
#define UNTITLED1_NFA_H

#include <utility>
#include <vector>
#include <functional>

enum class opcode {
  ALTERNATIVE,
  REPEAT,
  LINE_BEGIN_ASSERTION,
  LINE_END_ASSERTION,
  WORD_BOUNDARY,
  SUBEXPR_BEGIN,
  SUBEXPR_END,
  DUMMY,
  MATCH,
  ACCEPT,
};

class State {
public:
  State(opcode op) : op(op) {}
  State(opcode op, std::function<bool(char)> matcher) : op(op), matcher(std::move(matcher)) {}

  bool hasAlt() const noexcept { return op == opcode::ALTERNATIVE || op == opcode::REPEAT; }

  opcode op;
  long nextId = -1;
  long alt;
  size_t subexpr;

  bool neg;

  std::function<bool(char)> matcher;
};

class NFA : public std::vector<State> {
public:
  long insertDummy();
  long insertSubexprBegin();
  long insertSubexprEnd();

  long insertLineBegin() { return insertState(State(opcode::LINE_BEGIN_ASSERTION)); }
  long insertLineEnd() { return insertState(State(opcode::LINE_END_ASSERTION)); }
  long insertWordBound(bool neg){
    State tmp(opcode::WORD_BOUNDARY);
    tmp.neg = neg;
    return insertState(std::move(tmp));
  }

  long insertState(State);
  long insertRepeat(long, long, bool);
  long insertAlt(long, long, bool);
  long insertAccept();

  void eliminateDummy();

  std::vector<size_t> parenStack;
  long                startId = 0;
  size_t              subexpressionsCount = 0;
};


#endif
