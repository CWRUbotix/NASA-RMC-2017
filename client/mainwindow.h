#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <amqp_tcp_socket.h>
#include <amqp.h>
#include <amqp_framing.h>
#include "messages.pb.h"
#include <QGraphicsScene>
#include <QGraphicsView>
#include <QGraphicsItem>

using namespace com::cwrubotix::glennifer;

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    explicit MainWindow(amqp_connection_state_t conn, QWidget *parent = 0);
    ~MainWindow();

public slots:
    void handleLocomotion(LocomotionControl_LocomotionType direction);
    void handleLocomotionUp();
    void handleLocomotionDown();
    void handleLocomotionLeft();
    void handleLocomotionRight();
    void on_spinBox_setWheelAngle_valueChanged(int value);

private:
    Ui::MainWindow *ui;
    amqp_connection_state_t m_conn;
    QGraphicsScene *scene;
    QGraphicsRectItem *rectangle1;
    QGraphicsRectItem *rectangle2;
    QGraphicsRectItem *rectangle3;
    QGraphicsRectItem *rectangle4;
    void updateAngle(int x);
};

#endif // MAINWINDOW_H
