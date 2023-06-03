#include "Tokenizer.h"
#include <stdexcept>

Tokenizer::Tokenizer(const std::string &pattern) : current(pattern.cbegin()), end(pattern.cend()) {
  advance();
}

void Tokenizer::advance() {
  if (current == end) {
    currentToken = Token::EOF_TOK;
    return;
  }

  switch (state) {
    case NORMAL:
      scanNormal();
      break;
    case IN_BRACKET:
      scanBracket();
      break;
  }
}

void Tokenizer::scanNormal() {
  auto c = *current++;

  if (!std::string("^$\\.*+?()[]{}|").contains(c)) {
    currentToken =  Token::ORD_CHAR;
    value.assign(1, c);
    return;
  }
  if (c == '\\') {
    if (current == end) throw std::domain_error("Escape at end");
    eatEscape();
  } else if (c == '.') {
    currentToken =  Token::ANYCHAR;
    return;
  } else if (c == '(') {
    if (*current == '?') {
      if (++current == end) throw std::domain_error("Group start at end");

      if (*current == ':') {
        ++current;
        currentToken =  Token::SUBEXPR_NO_GROUP_BEGIN;
      } else throw std::domain_error("Group start incorrect");
    } else
      currentToken =  Token::SUBEXPR_BEGIN;
  } else if (c == ')')
    currentToken =  Token::SUBEXPR_END;
  else
    //there could be bracket matching
  if (c == '{') {
    state = IN_BRACKET;
    currentToken =  Token::INTERVAL_BEGIN;
  } else if (c != ']' && c != '}') {
    auto it = token_tbl;
    for (; it->first != '\0'; ++it)
      if (it->first == c) {
        currentToken = it->second;
        return;
      }
    throw std::domain_error("Unexpected");
  } else {
    currentToken =  Token::ORD_CHAR;
    value.assign(1, c);
  }
}

const char *findEscape(char c) {
  static const std::pair<char, char> escape_tbl[8] =
          {
                  {'0',  '\0'},
                  {'b',  '\b'},
                  {'f',  '\f'},
                  {'n',  '\n'},
                  {'r',  '\r'},
                  {'t',  '\t'},
                  {'v',  '\v'},
                  {'\0', '\0'},
          };
  auto it = escape_tbl;
  for (; it->first != '\0'; ++it)
    if (it->first == c)
      return &it->second;
  return nullptr;
}

void Tokenizer::eatEscape() {
  auto c = *current++;
  auto pos = findEscape(c);

  if (pos != nullptr && (c != 'b' || state == IN_BRACKET)) {
    currentToken =  Token::ORD_CHAR;
    value.assign(1, *pos);
  } else if (c == 'b') {
    currentToken =  Token::WORD_BOUND;
    value.assign(1, 'p');
  } else if (c == 'B') {
    currentToken =  Token::WORD_BOUND;
    value.assign(1, 'n');
  } else if (   c == 'd'
             || c == 'D'
             || c == 's'
             || c == 'S'
             || c == 'w'
             || c == 'W') {
    currentToken =  Token::QUOTED_CLASS;
    value.assign(1, c);
  } else if (c == 'c') {
    if (current == end) throw std::domain_error("Bad esc");
    currentToken =  Token::ORD_CHAR;
    value.assign(1, *current++);
  } else {
    currentToken =  Token::ORD_CHAR;
    value.assign(1, c);
  }
}

bool isDigit(char c) {
  return c >= '0' && c <= '9';
}

void Tokenizer::scanBracket() {
  if (current == end) throw std::domain_error("Bad bracket");

  auto c = *current++;

  if (isDigit(c)) {
    currentToken =  Token::DUP_COUNT;
    value.assign(1, c);
    while (current != end && isDigit(*current))
      value += *current++;
  } else if (c == ',')
    currentToken =  Token::COMMA;
  else if (c == '}') {
    state = NORMAL;
    currentToken =  Token::INTERVAL_END;
  } else throw std::domain_error("Bad bracket");
}
