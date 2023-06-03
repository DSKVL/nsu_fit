#include <stdexcept>
#include <map>
#include "Compiler.h"

Compiler::Compiler(const std::string &pattern) : tokenizer(pattern) {
  StateSeq tmp(dfa, dfa.startId);
  tmp.append(dfa.insertSubexprBegin());
  disjunctionMatch();
  if (!matchToken( Token::EOF_TOK))
    throw std::domain_error("Garbage at the end");
  tmp.append(pop());
  tmp.append(dfa.insertSubexprEnd());
  tmp.append(dfa.insertAccept());
  dfa.eliminateDummy();
}

void Compiler::disjunctionMatch() {
  alternativeMatch();

  while(matchToken( Token::OR)) {
    StateSeq alt1 = pop();
    alternativeMatch();
    StateSeq alt2 = pop();
    auto end = dfa.insertDummy();
    alt1.append(end);
    alt2.append(end);

    stack.emplace(dfa, dfa.insertAlt( alt2.startId, alt1.startId, false), end);
  }
}

void Compiler::alternativeMatch() {
  if (termMatch()) {
    StateSeq states = pop();
    alternativeMatch();
    states.append(pop());
    stack.push(states);
  }
  else
    stack.emplace(dfa, dfa.insertDummy());
}

bool Compiler::matchToken(Token token) {
    if (token == tokenizer.getToken()){
      value = tokenizer.getValue();
      tokenizer.advance();
      return true;
    }
    return false;

}

StateSeq Compiler::pop() {
  auto ret = stack.top();
  stack.pop();
  return ret;
}

bool Compiler::termMatch() {
  if (assertionMatch())
    return true;
  if (atomMatch()) {
    while (quantifierMatch());
    return true;
  }

  return false;
}

bool Compiler::assertionMatch() {
  if (matchToken( Token::LINE_BEGIN))      stack.emplace(dfa, dfa.insertLineBegin());
  else if (matchToken( Token::LINE_END))   stack.emplace(dfa, dfa.insertLineEnd());
  else if (matchToken( Token::WORD_BOUND)) stack.emplace(dfa, dfa.insertWordBound(value[0] == 'n'));
  else return false;
  return true;
}

bool Compiler::atomMatch() {
  if (matchToken(Token::ANYCHAR)) {
    insertAnyMatcher();
    return true;
  } else if (matchToken(Token::ORD_CHAR)) {
    insertCharMatcher();
    return true;
  } else if (matchToken(Token::QUOTED_CLASS)) {
    insertQuotedClassMatcher(value.at(0));
  } else if (matchToken( Token::SUBEXPR_NO_GROUP_BEGIN)) {
    StateSeq tmp(dfa, dfa.insertDummy());
    disjunctionMatch();
    if (!matchToken( Token::SUBEXPR_END))
      throw std::domain_error("No subexpr");
    tmp.append(pop());
    stack.push(tmp);
    return true;
  } else if (matchToken( Token::SUBEXPR_BEGIN)) {
    StateSeq tmp(dfa, dfa.insertSubexprBegin());
    disjunctionMatch();
    if (!matchToken( Token::SUBEXPR_END))
      throw std::domain_error("No subexpr");
    tmp.append(pop());
    tmp.append(dfa.insertSubexprEnd());
    stack.push(tmp);
    return true;
  }

  return false;
}

void Compiler::insertCharMatcher() {
  char tmp = value.at(0);
  stack.emplace(dfa, dfa.insertState(State(opcode::MATCH, [tmp](char c){ return c == tmp; })));
}

void Compiler::insertAnyMatcher() {
  stack.emplace(dfa, dfa.insertState(State(opcode::MATCH, [](char c){ return true; })));
}

bool Compiler::quantifierMatch() {
    bool neg = true;
    auto init = [this, &neg]() {
        if (stack.empty())
          throw std::domain_error("Bad quantifier");
        neg = neg && matchToken( Token::OPT);
    };

    if (matchToken( Token::CLOSURE0)) {
      init();
      auto prev = pop();
      StateSeq tmp(dfa, dfa.insertRepeat(-1, prev.startId, neg));
      prev.append(tmp);
      stack.push(tmp);
    } else if (matchToken( Token::CLOSURE1)) {
      init();
      auto prev = pop();
      prev.append(dfa.insertRepeat(-1, prev.startId, neg));
      stack.push(prev);
    } else if (matchToken( Token::OPT)) {
      init();
      auto prev = pop();
      auto end = dfa.insertDummy();
      StateSeq tmp(dfa, dfa.insertRepeat(-1, prev.startId, neg));
      prev.append(end);
      tmp.append(end);
      stack.push(tmp);
    } else if (matchToken( Token::INTERVAL_BEGIN)) {
      if (stack.empty())
        throw std::domain_error("Bad quantifier interval");
      if (!matchToken( Token::DUP_COUNT))
        throw std::domain_error("Bad dup count");
      StateSeq r(pop());
      StateSeq e(dfa, dfa.insertDummy());
      long minRep = std::stoi(value);
      bool inf = false;
      long n = 0;

      if (matchToken( Token::COMMA)) {
        if (matchToken( Token::DUP_COUNT)) n = std::stoi(value) - minRep;
        else inf = true;
      }
      if (!matchToken( Token::INTERVAL_END))
        throw std::domain_error("Bad interval end");

      neg = neg && matchToken( Token::OPT);

      for (long i = 0; i < minRep; ++i)
        e.append(r.clone());

      if (inf) {
        auto tmp = r.clone();
        StateSeq _s(dfa, dfa.insertRepeat(-1, tmp.startId, neg));
        tmp.append(_s);
        e.append(_s);
      } else {
        if (n < 0)
          throw std::domain_error("Bad interval inf");
        auto _end = dfa.insertDummy();

        std::stack<long> localStack;
        for (long i = 0; i < n; ++i) {
          auto _tmp = r.clone();
          auto _alt = dfa.insertRepeat(_tmp.startId, _end, neg);
          localStack.push(_alt);
          e.append(StateSeq(dfa, _alt, _tmp.endId));
        }
        e.append(_end);
        while (!localStack.empty()){
          auto& _tmp = (dfa)[localStack.top()];
          localStack.pop();
          std::swap(_tmp.nextId, _tmp.alt);
        }
      }
      stack.push(e);
    }
    else
      return false;
    return true;

}

NFA Compiler::getDFA() {
  return std::move(dfa);
}

void Compiler::insertQuotedClassMatcher(char c) {
  std::function<bool(char)> matcher;
  switch (c) {
    case 'd': matcher = [](char c){return isdigit(c); };  break;
    case 'D': matcher = [](char c){return !isdigit(c); }; break;
    case 's': matcher = [](char c){return isspace(c); };  break;
    case 'S': matcher = [](char c){return !isspace(c);};  break;
    case 'w': matcher = [](char c){return isalpha(c); };  break;
    case 'W': matcher = [](char c){return !isalpha(c); }; break;
    default: throw std::domain_error("unexpected class");
  }
  stack.emplace(dfa, dfa.insertState(State(opcode::MATCH, matcher)));
}

void StateSeq::append(long stateId) {
  dfa[endId].nextId = stateId;
  endId = stateId;
}

void StateSeq::append(const StateSeq &seq) {
  dfa[endId].nextId = seq.startId;
  endId = seq.endId;
}

StateSeq StateSeq::clone() {
    std::map<long, long> m;
    std::stack<long, std::deque<long>> stack;
    stack.push(startId);
    while (!stack.empty()) {
      auto u = stack.top();
      stack.pop();
      auto dup = dfa[u];

      auto id = dfa.insertState(std::move(dup));
      m[u] = id;
      if (dup.hasAlt())
        if (dup.alt != -1 && m.count(dup.alt) == 0)
          stack.push(dup.alt);
      if (u == endId)
        continue;
      if (dup.nextId != -1
          && m.count(dup.nextId) == 0)
        stack.push(dup.nextId);
    }
    for (auto it : m) {
      auto val = it.second;
      auto& ref = dfa[val];
      if (ref.nextId != -1)
        ref.nextId = m.find(ref.nextId)->second;
      if (ref.hasAlt() && ref.alt != -1)
        ref.alt = m.find(ref.alt)->second;
    }
    return {dfa, m[startId], m[endId]};
}
