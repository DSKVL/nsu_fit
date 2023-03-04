#ifndef PRISONERSDILEMMA_GAMEEXCEPTIONS_H
#define PRISONERSDILEMMA_GAMEEXCEPTIONS_H

#include <exception>
#include <string>

class bad_command_line_argument_exception : public std::exception {
public:
    explicit bad_command_line_argument_exception(const char*);
    const char* what() const noexcept override;
private:
    std::string err;
};

class bad_players_exception : public std::exception {
public:
    explicit bad_players_exception(const char*);
    const char* what() const noexcept override;
private:
    std::string err;
};

class bad_payoffs_file_format : public std::exception {
public:
    explicit bad_payoffs_file_format(const char*);
    const char* what() const noexcept override;
private:
    std::string err;
};

class bad_configs_exception : public std::exception {
public:
    explicit bad_configs_exception(const char*);
    const char* what() const noexcept override;
private:
    std::string err;
};

#endif //PRISONERSDILEMMA_GAMEEXCEPTIONS_H
