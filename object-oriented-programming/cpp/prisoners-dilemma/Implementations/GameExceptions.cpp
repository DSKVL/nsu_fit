#include "../Headers/GameExceptions.h"

bad_players_exception::bad_players_exception(const char *err) {
    this->err = err;
}

const char* bad_players_exception::what() const noexcept {
    return err.c_str();
}

bad_command_line_argument_exception::bad_command_line_argument_exception(const char* err) {
    this->err = err;
}

const char* bad_command_line_argument_exception::what() const noexcept {
    return err.c_str();
}

bad_payoffs_file_format::bad_payoffs_file_format(const char* err) {
    this->err = err;
}

const char *bad_payoffs_file_format::what() const noexcept {
    return err.c_str();
}

bad_configs_exception::bad_configs_exception(const char *) {
    this->err = err;
}

const char *bad_configs_exception::what() const noexcept {
    return err.c_str();
}
