#ifndef GAMEFIELD_H
#define GAMEFIELD_H

#include "./ui_mainwindow.h"

#include <QWidget>
#include <QTimer>
#include "CellularAutomatonInterface.h"


class GameField : public QWidget
{
    Q_OBJECT
public:
    explicit GameField(QWidget *parent = nullptr, CellularAutomatonInterface* game = nullptr,  Ui::MainWindow *ui = nullptr);
    ~GameField();

    int getFieldHeight();
    int getFieldWidth();
    int isAlive(unsigned i, unsigned j);
    void reviveCell(unsigned i, unsigned j);
    void killCell(unsigned i, unsigned j);

public slots:
    void start();
    void stop();

    void clear();
    void setFieldHeight(int height);
    void setFieldWidth(int width);

    void setAliveColour(const QColor colour);
    void setDeadColour(const QColor colour);
    void setCellSize(int size);
    void setSimulationSpeed(int speed);

    void changeRuleAlive0();
    void changeRuleAlive1();
    void changeRuleAlive2();
    void changeRuleAlive3();
    void changeRuleAlive4();
    void changeRuleAlive5();
    void changeRuleAlive6();
    void changeRuleAlive7();
    void changeRuleAlive8();
    void changeRuleEnliven0();
    void changeRuleEnliven1();
    void changeRuleEnliven2();
    void changeRuleEnliven3();
    void changeRuleEnliven4();
    void changeRuleEnliven5();
    void changeRuleEnliven6();
    void changeRuleEnliven7();
    void changeRuleEnliven8();

    void next_step();

signals:
    void CellSizeChanged(int);

protected:
    void paintEvent(QPaintEvent *event) override;
    void mousePressEvent(QMouseEvent* event) override;
    void mouseReleaseEvent(QMouseEvent *event) override;
    void mouseMoveEvent(QMouseEvent* event) override;
    void wheelEvent(QWheelEvent *event) override;
private:
    qreal cellSize;
    QTimer* timer;
    QColor dead_colour;
    QColor alive_colour;
    CellularAutomatonInterface* game;
    size_t interval_;
    Ui::MainWindow *ui;
    bool is_running = false;
    QPoint middle_button_press_pos = {0,0};
};

#endif // GAMEFIELD_H
