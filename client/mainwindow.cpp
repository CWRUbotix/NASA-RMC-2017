#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <AMQPcpp.h>
#include "messages.pb.h"
#include <QGraphicsScene>
#include <QGraphicsView>
#include <QGraphicsItem>
#include <QImage>

/*
 * In this file, the state of the robot is queried by RPC.
 * Since those RPC calls do not exist yet, instead we are using fake calls like this:
 *
 * float x = 0; // getLocomotionFrontLeftWheelRpm();
 * float y = 0; // getLocomotionFrontLeftWheelPodPos();
 * LocomotionConfiguration x = STRAIGHT; // getLocomotionConfiguration();
 * float a = 0; // getLocomotionStraightSpeed();
 * float b = 0; // getLocomotionTurnSpeed();
 * float c = 0; // getLocomotionStrafeSpeed();
 */

using namespace com::cwrubotix::glennifer;

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    locomotionScene = new QGraphicsScene(this);
    ui->graphicsView->setScene(locomotionScene);

    excavationScene = new QGraphicsScene(this);
    ui->graphicsView_2->setScene(excavationScene);

    depositionScene = new QGraphicsScene(this);
    ui->graphicsView_3->setScene(depositionScene);

    QBrush greenBrush(Qt::green);
    QBrush grayBrush(Qt::gray);
    QBrush redBrush(Qt::red);
    QBrush blueBrush(Qt::blue);
    QPen outlinePen(Qt::black);
    outlinePen.setWidth(2);

    rectangle1 = locomotionScene->addRect(-50, -80, 10, 20, outlinePen, greenBrush);
    rectangle2 = locomotionScene->addRect(50, -80, 10, 20, outlinePen, greenBrush);
    rectangle3 = locomotionScene->addRect(-50, 80, 10, 20, outlinePen, greenBrush);
    rectangle4 = locomotionScene->addRect(50, 80, 10, 20, outlinePen, greenBrush);

    excavationScene->addRect(-80, -20, 160, 40, outlinePen, grayBrush);
    excavationScene->addRect(-100, -10, 160, 20, outlinePen, blueBrush);

    QPolygonF poly(4);
    poly[0] = QPointF(-120, -60);
    poly[1] = QPointF(20, 80);
    poly[2] = QPointF(50, 80);
    poly[3] = QPointF(80, -60);

    depositionScene->addPolygon(poly, outlinePen, redBrush);

    QObject::connect(ui->locomotion_UpButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionUp);
    QObject::connect(ui->locomotion_DownButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionDown);
    QObject::connect(ui->locomotion_LeftButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionLeft);
    QObject::connect(ui->locomotion_RightButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionRight);
}

MainWindow::MainWindow(AMQP *amqp, QWidget *parent) :
    MainWindow::MainWindow(parent)
{
    m_amqp = amqp;
}

MainWindow::~MainWindow()
{
    delete locomotionScene;
    delete ui;
}

void MainWindow::handleLocomotionUp() {
    LocomotionControlCommandStraight msg;
    msg.set_speed(123.0F);
    msg.set_timeout(456);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.straight");

    free(msg_buff);
}

void MainWindow::handleLocomotionDown() {
    LocomotionControlCommandStraight msg;
    msg.set_speed(-123.0F);
    msg.set_timeout(456);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.straight");

    free(msg_buff);
}

void MainWindow::handleLocomotionLeft() {
    LocomotionControlCommandTurn msg;
    msg.set_speed(-123.0F);
    msg.set_timeout(456);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.turn");

    free(msg_buff);
}

void MainWindow::handleLocomotionRight() {
    LocomotionControlCommandTurn msg;
    msg.set_speed(123.0F);
    msg.set_timeout(456);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.turn");

    free(msg_buff);
}

void MainWindow::updateAngle(int x){

    rectangle1->setTransformOriginPoint(QPoint(-195,10));
    rectangle1->setRotation(x);

    rectangle2->setTransformOriginPoint(QPoint(85,10));
    rectangle2->setRotation(x);

    rectangle3->setTransformOriginPoint(QPoint(-195,90));
    rectangle3->setRotation(x);

    rectangle4->setTransformOriginPoint(QPoint(85,90));
    rectangle4->setRotation(x);
}
