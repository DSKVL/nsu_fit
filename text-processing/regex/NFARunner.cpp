#include <stdexcept>
#include "NFARunner.h"
#include "NFA.h"

bool NFARunner::wordBoundary() const {
  bool leftIsWord = false;
  if (current != begin) {
    auto prev = current;
    if (isWord(*std::prev(prev)))
      leftIsWord = true;
  }
  bool rightIsWord = current != end && isWord(*current);

  return leftIsWord != rightIsWord;
}

void NFARunner::repOnceMore(long i) {
  const auto& state = dfa[i];
  auto& repCount = repetitionsCount[i];
  if (repCount.second == 0 || repCount.first != current) {
    auto back = repCount;
    repCount.first = current;
    repCount.second = 1;
    dfs(state.alt);
    repCount = back;
  } else if (repCount.second < 2) {
    repCount.second++;
    dfs(state.alt);
    repCount.second--;
  }
}

void NFARunner::handleRepeat(long i) {
  const auto& state = dfa[i];

  if (!state.neg) {
    repOnceMore(i);
    if (!hasSol)
      dfs(state.nextId);
  } else {
    dfs(state.nextId);
    if (!hasSol)
      repOnceMore(i);
  }
}

void NFARunner::handleSubexprBegin(long i) {
  const auto& state = dfa[i];

  auto& res = curResults[state.subexpr];
  auto back = res.begin;
  res.begin = current;
  dfs(state.nextId);
  res.begin = back;
}

void NFARunner::handleSubexprEnd(long i) {
  const auto& state = dfa[i];

  auto& res = curResults[state.subexpr];
  struct match back = res;
  res.end = current;
  res.matched = true;
  dfs(state.nextId);
  curResults[state.subexpr] = back;
}

inline void NFARunner::handleLineBeginAssertion(long i) {
  const auto& state = dfa[i];
  if (atBegin())
    dfs(state.nextId);
}

inline void NFARunner::handleLineEndAssertion(long i) {
  const auto& state = dfa[i];
  if (atEnd())
    dfs(state.nextId);
}

inline void NFARunner::handleWordBoundary(long i) {
  const auto& state = dfa[i];

  if (wordBoundary() == !state.neg)
    dfs(state.nextId);
}
//
//void NFARunner::handleSubexprLookahead(long i) {
//  const auto& state = dfa[i];
//  if (Lookahead(state.alt) == !state.neg)
//    dfs(state.nextId);
//}

void NFARunner::handleMatch(long i) {
  const auto& state = dfa[i];

  if (current == end)
    return;

  if (state.matcher(*current)) {
    ++current;
    dfs(state.nextId);
    --current;
  }

}

void NFARunner::handleAccept(long) {
    if (hasSol)
      throw std::exception();
    hasSol = true;
    results = curResults;
}

void NFARunner::handleAlternative(long i) {
  const auto& state = dfa[i];
  dfs(state.alt);
  if (!hasSol)
    dfs(state.nextId);
}

void NFARunner::dfs(long i) {
  switch (dfa[i].op) {
    case opcode::REPEAT:               handleRepeat(i); break;
    case opcode::SUBEXPR_BEGIN:        handleSubexprBegin(i); break;
    case opcode::SUBEXPR_END:          handleSubexprEnd(i); break;
    case opcode::LINE_BEGIN_ASSERTION: handleLineBeginAssertion(i); break;
    case opcode::LINE_END_ASSERTION:   handleLineEndAssertion(i); break;
    case opcode::WORD_BOUNDARY:        handleWordBoundary(i); break;
    case opcode::MATCH:                handleMatch(i); break;
    case opcode::ACCEPT:               handleAccept(i); break;
    case opcode::ALTERNATIVE:          handleAlternative(i); break;
    default:
      throw std::domain_error("incorrect opcode");
    }
}