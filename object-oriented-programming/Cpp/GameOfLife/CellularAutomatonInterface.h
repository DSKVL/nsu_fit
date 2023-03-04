#ifndef CELLULARAUTOMATONINTERFACE_H
#define CELLULARAUTOMATONINTERFACE_H

class CellularAutomatonInterface {
public:
    virtual void newGeneration() = 0;
    virtual void clear() = 0;
    virtual void reviveCell(unsigned x, unsigned y) = 0;
    virtual void killCell(unsigned x, unsigned y) = 0;
    virtual void setHeight(unsigned) = 0;
    virtual void setWidth(unsigned) = 0;
    virtual void setRule(int) = 0;
    virtual unsigned getHeight() = 0;
    virtual unsigned getWidth() = 0;
    virtual bool isAlive(unsigned i, unsigned j) = 0;
    virtual ~CellularAutomatonInterface() = default;
};

#endif // CELLULARAUTOMATONINTERFACE_H
