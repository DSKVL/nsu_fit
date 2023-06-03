#ifndef UNTITLED1_NFARUNNER_H
#define UNTITLED1_NFARUNNER_H

#include <vector>
#include <string>
#include "NFA.h"
#include <optional>

class NFARunner {
  typedef std::string::iterator it;

  struct match {
    it begin;
    it end;
    bool matched;
  };
public:
  typedef std::vector<match> matches;
public:
  NFARunner(it begin, it end, const NFA&  dfa)
          : begin(begin), end(end), dfa(dfa), repetitionsCount(dfa.size()) { }

  std::optional<matches> match() {
    current = begin;
    curResults.resize(3 + dfa.size(), {});
    dfs(dfa.startId);

    if (hasSol) return {results};
    else        return {};
  }


private:
  void repOnceMore                    (long);
  void handleRepeat                   (long);
  void handleSubexprBegin             (long);
  void handleSubexprEnd               (long);
  void handleLineBeginAssertion       (long);
  void handleLineEndAssertion         (long);
  void handleWordBoundary             (long);
  void handleMatch                    (long);
  void handleAccept                   (long);
  void handleAlternative              (long);

  void dfs(long i);

  bool isWord(char c) const {
    return isalpha(c);
  }

  bool atBegin() const {
    return current == begin;
  }

  bool atEnd() const {
    if (current == end)
      return isLineTerminator(*current);
    else
      return false;
  }
  bool wordBoundary() const;
  bool isLineTerminator(char c) const {
    if (c == '\n')
      return true;
    return false;
  }

public:
  matches curResults;
  it current, begin;
  const it end;
  const NFA& dfa;
  matches results;
  std::vector<std::pair<it, int>> repetitionsCount;
  bool hasSol = false;
};


#endif //UNTITLED1_NFARUNNER_H
