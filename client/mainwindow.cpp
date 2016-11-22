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
    scene = new QGraphicsScene(this);
    ui->graphicsView->setScene(scene);

    QBrush greenBrush(Qt::green);
    QPen outlinePen(Qt::black);
    outlinePen.setWidth(2);

    rectangle1 = scene->addRect(-200, 0, 10, 20, outlinePen, greenBrush);
    rectangle2 = scene->addRect(80, 0, 10, 20, outlinePen, greenBrush);
    rectangle3 = scene->addRect(-200, 80, 10, 20, outlinePen, greenBrush);
    rectangle4 = scene->addRect(80, 80, 10, 20, outlinePen, greenBrush);

    QImage image("../clockwiseArrow.png");
    ui->label->setPixmap(QPixmap::fromImage(image));
    ui->label->setScaledContents(true);

    ui->label_4->setPixmap(QPixmap::fromImage(image.mirrored(true,false)));
    ui->label_4->setScaledContents(true);


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
    delete scene;
    delete ui;
}

void MainWindow::handleLocomotion(LocomotionControl_LocomotionType direction) {


    LocomotionControl msg;
    msg.set_locomotiontype(direction);
    msg.set_speed_percent(123.0F);
    msg.set_timeout_ms(456);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "locomotion");

    free(msg_buff);
}

void MainWindow::handleLocomotionUp() {
    handleLocomotion(LocomotionControl_LocomotionType_FORWARD);
}

void MainWindow::handleLocomotionDown() {
    handleLocomotion(LocomotionControl_LocomotionType_BACKWARD);
}

void MainWindow::handleLocomotionLeft() {
    handleLocomotion(LocomotionControl_LocomotionType_LEFT);
}

void MainWindow::handleLocomotionRight() {
    handleLocomotion(LocomotionControl_LocomotionType_RIGHT);
}

void MainWindow::on_spinBox_setWheelAngle_valueChanged(int value){
    updateAngle(value);
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
