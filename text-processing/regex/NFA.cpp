#include <stdexcept>
#include <map>
#include "NFA.h"

long NFA::insertDummy() { return insertState(State(opcode::DUMMY)); }


long NFA::insertState(State state) {
  push_back(state);
  return size() - 1;
}

long NFA::insertSubexprBegin() {
  auto id = subexpressionsCount++;
  parenStack.push_back(id);
  State tmp(opcode::SUBEXPR_BEGIN);
  tmp.subexpr = id;
  return insertState(std::move(tmp));
}

long NFA::insertSubexprEnd() {
  State tmp(opcode::SUBEXPR_END);
  tmp.subexpr = parenStack.back();
  parenStack.pop_back();
  return insertState(std::move(tmp));
}

long NFA::insertRepeat(long nextId, long altId, bool neg) {
  State tmp(opcode::REPEAT);
  tmp.nextId = nextId;
  tmp.alt = altId;
  tmp.neg = neg;
  return insertState(std::move(tmp));

}

long NFA::insertAlt(long nextId, long altId, bool) {
  State tmp(opcode::ALTERNATIVE);

  tmp.nextId = nextId;
  tmp.alt = altId;
  return insertState(std::move(tmp));
}

long NFA::insertAccept() {
    return insertState(State(opcode::ACCEPT));
}

void NFA::eliminateDummy() {
  for (auto& it : *this) {
    while (it.nextId >= 0 && at(it.nextId).op == opcode::DUMMY)
      it.nextId = at(it.nextId).nextId;
    if (it.hasAlt())
      while (it.alt >= 0 && at(it.alt).op == opcode::DUMMY)
        it.alt = at(it.alt).nextId;
  }
}

