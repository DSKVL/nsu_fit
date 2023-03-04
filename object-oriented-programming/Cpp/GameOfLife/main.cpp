#include "mainwindow.h"
#include "game.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainWindow w(nullptr, new Game());
    w.show();

    return a.exec();
}
