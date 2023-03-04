#include "gamefield.h"

#include <QTimer>
#include <QColor>
#include <QPainter>
#include <QMouseEvent>
#include <QScrollBar>

#include "CellularAutomatonInterface.h"

GameField::GameField(QWidget *parent,  CellularAutomatonInterface* game,  Ui::MainWindow *ui) :
    QWidget(parent)
  , cellSize(100)
  , timer(new QTimer(this))
  , alive_colour(QColor(0x2a, 0xa6, 0x3d))
  , dead_colour(QColor(0x36, 0x2c, 0x58))
  , game(game)
  , interval_(250)
  , ui(ui)
{
    timer->setInterval(interval_);
    connect(timer, SIGNAL(timeout()), this, SLOT(next_step()));
    this->setSizePolicy(QSizePolicy::Minimum, QSizePolicy::Minimum);
}

GameField::~GameField()
{
    delete timer;
}

void GameField::start()
{
    timer->start();
    is_running = true;
}

void GameField::stop()
{
    timer->stop();
    is_running = false;
}

void GameField::clear()
{
    timer->stop();
    game->clear();
    update();
    if (is_running) {
        timer->start();
    }
}

void GameField::setFieldHeight(int h)
{
    timer->stop();
    game->setHeight(h);
    this->setMinimumHeight(cellSize*h);
    ui->height_spin->setValue(h);
    update();
    if (is_running) {
        timer->start();
    }
}

void GameField::setFieldWidth(int w)
{
    timer->stop();
    game->setWidth(w);
    this->setMinimumWidth(cellSize*w);
    ui->width_spin->setValue(w);
    update();
    if (is_running) {
        timer->start();
    }
}

int GameField::getFieldHeight()
{
    return game->getHeight();
}

int GameField::getFieldWidth(){
    return game->getWidth();
}

int GameField::isAlive(unsigned i, unsigned j)
{
    return game->isAlive(i, j);
}

void GameField::reviveCell(unsigned i, unsigned j)
{
    game->reviveCell(i, j);
}

void GameField::killCell(unsigned i, unsigned j)
{
    game->killCell(i, j);
}


void GameField::setAliveColour(const QColor colour)
{
    timer->stop();
    alive_colour = colour;
    update();
    if (is_running) {
        timer->start();
    }
}

void GameField::setDeadColour(const QColor colour)
{
    timer->stop();
    dead_colour = colour;
    update();
    if (is_running) {
        timer->start();
    }
}

void GameField::setCellSize(int size)
{
    timer->stop();
    cellSize = static_cast<qreal>(size);
    this->setMinimumSize(cellSize*game->getWidth(), cellSize*game->getHeight());
    update();
    if (is_running) {
        timer->start();
    }
}

void GameField::setSimulationSpeed(int speed)
{
    timer->stop();
    interval_ = static_cast<size_t>(speed);
    timer->setInterval(interval_);
    if (is_running) {
        timer->start();
    }
}

void GameField::changeRuleAlive0(){game->setRule(0);}
void GameField::changeRuleAlive1(){game->setRule(1);}
void GameField::changeRuleAlive2(){game->setRule(2);}
void GameField::changeRuleAlive3(){game->setRule(3);}
void GameField::changeRuleAlive4(){game->setRule(4);}
void GameField::changeRuleAlive5(){game->setRule(5);}
void GameField::changeRuleAlive6(){game->setRule(6);}
void GameField::changeRuleAlive7(){game->setRule(7);}
void GameField::changeRuleAlive8(){game->setRule(8);}
void GameField::changeRuleEnliven0(){game->setRule(9);}
void GameField::changeRuleEnliven1(){game->setRule(10);}
void GameField::changeRuleEnliven2(){game->setRule(11);}
void GameField::changeRuleEnliven3(){game->setRule(12);}
void GameField::changeRuleEnliven4(){game->setRule(13);}
void GameField::changeRuleEnliven5(){game->setRule(14);}
void GameField::changeRuleEnliven6(){game->setRule(15);}
void GameField::changeRuleEnliven7(){game->setRule(16);}
void GameField::changeRuleEnliven8(){game->setRule(17);}

void GameField::next_step()
{
    timer->stop();
    game->newGeneration();
    update();
    timer->start();
}

void GameField::paintEvent(QPaintEvent *event)
{
    QPainter painter(this);
    for(size_t i=0; i < game->getHeight(); i++)
    {
        for(size_t j=0; j < game->getWidth(); j++)
        {
            qreal left = cellSize*j;
            qreal top  = cellSize*i;
            QRectF rect(left, top, cellSize, cellSize);
            if(game->isAlive(i,j))
            {
                painter.fillRect(rect, QBrush(alive_colour));
            }
            else
            {
                painter.fillRect(rect, QBrush(dead_colour));
            }
        }
    }
}

void GameField::mousePressEvent(QMouseEvent* event)
{
    if (event->position().y() > 0 && event->position().x() > 0)
    {
        size_t i = std::floor(event->position().y()/cellSize);
        size_t j = std::floor(event->position().x()/cellSize);
        if (event->buttons() == Qt::LeftButton) {
                    game->reviveCell(i, j);
        } else if( event->buttons() == Qt::RightButton ) {
            game->killCell(i, j);
        } else if (event->buttons() == Qt::MiddleButton) {
            middle_button_press_pos = event->pos();
        }

        update();
    }
}

void GameField::mouseReleaseEvent(QMouseEvent *event) {
    if (event->button() == Qt::MiddleButton){
        auto horizontal_old = ui->scrollArea->horizontalScrollBar()->value();
        ui->scrollArea->horizontalScrollBar()->setValue(horizontal_old - event->pos().x() + middle_button_press_pos.x());
        auto vertical_old = ui->scrollArea->verticalScrollBar()->value();
        ui->scrollArea->verticalScrollBar()->setValue(vertical_old - event->pos().y() + middle_button_press_pos.y());
    }
}

void GameField::mouseMoveEvent(QMouseEvent* event)
{
    if (event->position().y() > 0 && event->position().x() > 0)
    {
        size_t i = std::floor(event->position().y()/cellSize);
        size_t j = std::floor(event->position().x()/cellSize);
        if (i <= 99 && j <= 99){
            if (event->buttons() == Qt::LeftButton) {
                game->reviveCell(i, j);
            } else if( event->buttons() == Qt::RightButton ) {
                game->killCell(i, j);
            }
            update();
        }
    }
}

void GameField::wheelEvent(QWheelEvent *event)
{
    auto cellSizeOld = cellSize;
    auto mouse_pos = event->position();
    auto y = event->angleDelta().y();
    if (y >= 0 && cellSize < 300) {
        cellSize += y/10;
        if(cellSize > 300){
            cellSize = 300;
        }
    } else if (y <= 0 && cellSize > 15) {
        cellSize += y/10;
        if (cellSize < 15) {
            cellSize = 15;
        }
    }
    auto scale_factor = cellSize/cellSizeOld;
    auto horizontal_old = ui->scrollArea->horizontalScrollBar()->value();
    ui->scrollArea->horizontalScrollBar()->setValue(horizontal_old - mouse_pos.x()*(1-scale_factor));
    auto vertical_old = ui->scrollArea->verticalScrollBar()->value();
    ui->scrollArea->verticalScrollBar()->setValue(vertical_old-mouse_pos.y()*(1-scale_factor));
    emit CellSizeChanged(cellSize);
    update();
}
