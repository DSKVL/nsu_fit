#ifndef UNTITLED1_TOKENIZER_H
#define UNTITLED1_TOKENIZER_H

#include <string>

enum class Token {
  ANYCHAR,
  ORD_CHAR,
  SUBEXPR_BEGIN,
  SUBEXPR_NO_GROUP_BEGIN,
  SUBEXPR_END,
  INTERVAL_BEGIN,
  INTERVAL_END,
  QUOTED_CLASS,
  OPT,
  OR,
  CLOSURE0,
  CLOSURE1,
  LINE_BEGIN,
  LINE_END,
  WORD_BOUND,
  COMMA,
  DUP_COUNT,
  EOF_TOK,
};

enum TokenizerState {
  NORMAL, IN_BRACKET
};

class Tokenizer {
public:
  Tokenizer(const std::string&);
  Token getToken() { return currentToken; };
  std::string getValue() { return value; };
  void advance();
private:
  void eatEscape();
  void scanNormal();
  void scanBracket();

  std::string::const_iterator current;
  std::string::const_iterator end;
  std::string value;
  Token currentToken;
  TokenizerState state = NORMAL;

  const std::pair<char, Token> token_tbl[8] =
          {
                  {'^',  Token::LINE_BEGIN},
                  {'$',  Token::LINE_END},
                  {'.',  Token::ANYCHAR},
                  {'*',  Token::CLOSURE0},
                  {'+',  Token::CLOSURE1},
                  {'?',  Token::OPT},
                  {'|',  Token::OR},
                  {'\0', Token::OR},
          };
};


#endif //UNTITLED1_TOKENIZER_H
