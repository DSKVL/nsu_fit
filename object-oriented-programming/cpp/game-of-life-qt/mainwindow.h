#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "gamefield.h"

class CellularAutomatonInterface;

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr, CellularAutomatonInterface *game = nullptr);
    ~MainWindow();
public slots:
    void saveGame();
    void loadGame();

private:
    Ui::MainWindow *ui;
    GameField* game_field;
};
#endif // MAINWINDOW_H
