#ifndef GAMEOFLIFE_CELL_H
#define GAMEOFLIFE_CELL_H


class Cell {
public:
    Cell() = default;
    explicit Cell(bool status);
    ~Cell() = default;

    [[nodiscard]] bool getStatus() const;

private:
    bool status_ = false;
};


#endif //GAMEOFLIFE_CELL_H
