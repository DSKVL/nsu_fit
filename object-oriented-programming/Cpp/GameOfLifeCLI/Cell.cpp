#include "Cell.h"

Cell::Cell(const bool status) : status_(status) {}

bool Cell::getStatus() const {
    return status_;
}
