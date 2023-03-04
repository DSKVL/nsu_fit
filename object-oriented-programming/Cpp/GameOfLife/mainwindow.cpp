#include "mainwindow.h"

#include <QCheckBox>
#include <QFileDialog>
#include <QSaveFile>
#include <QMessageBox>
#include <QScrollBar>

#include "./ui_mainwindow.h"
#include "gamefield.h"
#include "CellularAutomatonInterface.h"
#include "scrollfilter.h"

MainWindow::MainWindow(QWidget *parent, CellularAutomatonInterface* game)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
    , game_field(new GameField(this, game, ui))
{
    ui->setupUi(this);
    game_field->setMinimumSize(3200,3200);
    ui->scrollArea->setWidget(game_field);

    connect(ui->width_spin, SIGNAL(valueChanged(int)), game_field, SLOT(setFieldWidth(int)));
    connect(ui->height_spin, SIGNAL(valueChanged(int)), game_field, SLOT(setFieldHeight(int)));

    connect(ui->startB, SIGNAL(clicked()), game_field, SLOT(start()));
    connect(ui->stopB, SIGNAL(clicked()), game_field, SLOT(stop()));
    connect(ui->clearB, SIGNAL(clicked()), game_field, SLOT(clear()));

    connect(ui->cell_size_slider, SIGNAL(valueChanged(int)), game_field, SLOT(setCellSize(int)));
    connect(ui->speed_slider, SIGNAL(valueChanged(int)), game_field, SLOT(setSimulationSpeed(int)));

    connect(ui->alive_0_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive0()));
    connect(ui->alive_1_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive1()));
    connect(ui->alive_2_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive2()));
    connect(ui->alive_3_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive3()));
    connect(ui->alive_4_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive4()));
    connect(ui->alive_5_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive5()));
    connect(ui->alive_6_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive6()));
    connect(ui->alive_7_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive7()));
    connect(ui->alive_8_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleAlive8()));
    connect(ui->enliven_0_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven0()));
    connect(ui->enliven_1_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven1()));
    connect(ui->enliven_2_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven2()));
    connect(ui->enliven_3_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven3()));
    connect(ui->enliven_4_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven4()));
    connect(ui->enliven_5_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven5()));
    connect(ui->enliven_6_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven6()));
    connect(ui->enliven_7_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven7()));
    connect(ui->enliven_8_cb, SIGNAL(stateChanged(int)), game_field, SLOT(changeRuleEnliven8()));

    connect(game_field, SIGNAL(CellSizeChanged(int)), ui->cell_size_slider, SLOT(setValue(int)));

    connect(ui->saveB, SIGNAL(clicked()), this, SLOT(saveGame()));
    connect(ui->loadB, SIGNAL(clicked()), this, SLOT(loadGame()));
}

MainWindow::~MainWindow()
{
    delete ui;
    delete game_field;
}

void MainWindow::saveGame()
{
    game_field->stop();
    auto fileName = QFileDialog::getSaveFileName(this,
        tr("Save Game"), "/home", tr(""));
    if (fileName.isEmpty()) {
        return;
    } else {
        QFile file(fileName);
        if (!file.open(QIODevice::WriteOnly)) {
            QMessageBox::information(this, tr("Unable to open file"),file.errorString());
            return;
        }
        int w = game_field->getFieldWidth();
        int h = game_field->getFieldHeight();

        QDataStream out(&file);
        out.setVersion(QDataStream::Qt_4_5);

        out << w << h;

        for (int i = 0; i < w; i++){
            for (int j = 0; j < h; j++){
                if (game_field->isAlive(i,j)) {
                    out << '1';
                } else {
                    out << '0';
                }
            }
        }
    }
}

bool is_correct(char* buf, int loaded) {
    if (loaded >= 8) {
        int w = *(buf+3), h = *(buf+7);
        if(loaded == 8 + w*h) {
            for ( int i = 8; i < loaded; i++){
                if (buf[i] != '1' && buf[i] != '0') {
                    return false;
                }
            }
            return true;
        }
    }
    return false;
}

void MainWindow::loadGame()
{
    constexpr int MAX_BUF_SIZE = 101*101;
    game_field->stop();
    auto fileName = QFileDialog::getOpenFileName(this,
        tr("Load Game"), "/home", tr(""));

    if (fileName.isEmpty()) {
        return;
    } else {
        QFile file(fileName);
        if (!file.open(QIODevice::ReadOnly)) {
            QMessageBox::information(this, tr("Unable to open file"),file.errorString());
            return;
        }

        QDataStream in(&file);
        in.setVersion(QDataStream::Qt_4_5);

        char buf[MAX_BUF_SIZE];
        auto loaded = in.readRawData(buf, MAX_BUF_SIZE);
        int w = *(buf+3), h = *(buf+7);

        if(is_correct(buf,loaded)) {
            game_field->setFieldWidth(w);
            game_field->setFieldHeight(h);
            game_field->clear();

            for (int i = 0; i < w; i++){
                for (int j = 0; j < h; j++){
                    if (buf[8+i*w + j] == '1') {
                        game_field->reviveCell(i, j);
                    } else {
                        game_field->killCell(i,j);
                    }
                }
            }
        } else {
            QMessageBox::warning(this, "Error", "Bad file format");
        }
    }
}


