#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <AMQPcpp.h>
#include "messages.pb.h"
#include <QGraphicsScene>
#include <QGraphicsView>
#include <QGraphicsItem>
#include <QImage>
#include <QMessageBox>
#include <QKeyEvent>
#include <QWheelEvent>
#include <QDebug>
#include <opencv2/core/core.hpp>
#include <opencv2/video.hpp>
#include <opencv/cv.h>
#include <opencv/cv.hpp>
#include "mydialog.h"
#include "mydialog2.h"
#include "mydialog3.h"
#include "mydialog4.h"
#include "mydialog5.h"
#include "consumerthread.h"

//cv::VideoCapture capWebcam(0);
//cv::Mat matOriginal;
//QImage qimgOriginal;
//QLabel lblInputImage;

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
    rectangle1->setTransformOriginPoint(-45, -70);
    rectangle2 = locomotionScene->addRect(50, -80, 10, 20, outlinePen, greenBrush);
    rectangle2->setTransformOriginPoint(55, -70);
    rectangle3 = locomotionScene->addRect(-50, 80, 10, 20, outlinePen, greenBrush);
    rectangle3->setTransformOriginPoint(-45, 90);
    rectangle4 = locomotionScene->addRect(50, 80, 10, 20, outlinePen, greenBrush);
    rectangle4->setTransformOriginPoint(55, 90);

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
    QObject::connect(ui->locomotion_UpButton, &QPushButton::released,
                     this, &MainWindow::handleLocomotionRelease);
    QObject::connect(ui->locomotion_DownButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionDown);
    QObject::connect(ui->locomotion_DownButton, &QPushButton::released,
                     this, &MainWindow::handleLocomotionRelease);
    QObject::connect(ui->locomotion_LeftButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionLeft);
    QObject::connect(ui->locomotion_LeftButton, &QPushButton::released,
                     this, &MainWindow::handleLocomotionRelease);
    QObject::connect(ui->locomotion_RightButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionRight);
    QObject::connect(ui->locomotion_RightButton, &QPushButton::released,
                     this, &MainWindow::handleLocomotionRelease);
    QObject::connect(ui->locomotion_StopButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionStop);
    QObject::connect(ui->locomotion_StraightButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionStraight);
    QObject::connect(ui->locomotion_TurnButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionTurn);
    QObject::connect(ui->locomotion_StrafeButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionStrafe);
    QObject::connect(ui->pushButton_FrontLeftWheelStop, &QPushButton::clicked,
                     this, &MainWindow::handleFrontLeftWheelStop);
    QObject::connect(ui->pushButton_FrontRightWheelStop, &QPushButton::clicked,
                     this, &MainWindow::handleFrontRightWheelStop);
    QObject::connect(ui->pushButton_BackLeftWheelStop, &QPushButton::clicked,
                     this, &MainWindow::handleBackLeftWheelStop);
    QObject::connect(ui->pushButton_BackRightWheelStop, &QPushButton::clicked,
                     this, &MainWindow::handleBackRightWheelStop);
    QObject::connect(ui->pushButton_FrontLeftWheelPodStraight, &QPushButton::clicked,
                     this, &MainWindow::handleFrontLeftWheelPodStraight);
    QObject::connect(ui->pushButton_FrontRightWheelPodStraight, &QPushButton::clicked,
                     this, &MainWindow::handleFrontRightWheelPodStraight);
    QObject::connect(ui->pushButton_BackLeftWheelPodStraight, &QPushButton::clicked,
                     this, &MainWindow::handleBackLeftWheelPodStraight);
    QObject::connect(ui->pushButton_BackRightWheelPodStraight, &QPushButton::clicked,
                     this, &MainWindow::handleBackRightWheelPodStraight);
    QObject::connect(ui->pushButton_FrontLeftWheelPodTurn, &QPushButton::clicked,
                     this, &MainWindow::handleFrontLeftWheelPodTurn);
    QObject::connect(ui->pushButton_FrontRightWheelPodTurn, &QPushButton::clicked,
                     this, &MainWindow::handleFrontRightWheelPodTurn);
    QObject::connect(ui->pushButton_BackLeftWheelPodTurn, &QPushButton::clicked,
                     this, &MainWindow::handleBackLeftWheelPodTurn);
    QObject::connect(ui->pushButton_BackRightWheelPodTurn, &QPushButton::clicked,
                     this, &MainWindow::handleBackRightWheelPodTurn);
    QObject::connect(ui->pushButton_FrontLeftWheelPodStrafe, &QPushButton::clicked,
                     this, &MainWindow::handleFrontLeftWheelPodStrafe);
    QObject::connect(ui->pushButton_FrontRightWheelPodStrafe, &QPushButton::clicked,
                     this, &MainWindow::handleFrontRightWheelPodStrafe);
    QObject::connect(ui->pushButton_BackLeftWheelPodStrafe, &QPushButton::clicked,
                     this, &MainWindow::handleBackLeftWheelPodStrafe);
    QObject::connect(ui->pushButton_BackRightWheelPodStrafe, &QPushButton::clicked,
                     this, &MainWindow::handleBackRightWheelPodStrafe);
    QObject::connect(ui->slider_FrontLeftWheel, &QSlider::valueChanged,
                     this, &MainWindow::handleFrontLeftWheelSet);
    QObject::connect(ui->slider_FrontRightWheel, &QSlider::valueChanged,
                     this, &MainWindow::handleFrontRightWheelSet);
    QObject::connect(ui->slider_BackLeftWheel, &QSlider::valueChanged,
                     this, &MainWindow::handleBackLeftWheelSet);
    QObject::connect(ui->slider_BackRightWheel, &QSlider::valueChanged,
                     this, &MainWindow::handleBackRightWheelSet);
    QObject::connect(ui->slider_FrontLeftWheelPod, &QSlider::valueChanged,
                     this, &MainWindow::handleFrontLeftWheelPodSet);
    QObject::connect(ui->slider_FrontRightWheelPod, &QSlider::valueChanged,
                     this, &MainWindow::handleFrontRightWheelPodSet);
    QObject::connect(ui->slider_BackLeftWheelPod, &QSlider::valueChanged,
                     this, &MainWindow::handleBackLeftWheelPodSet);
    QObject::connect(ui->slider_BackRightWheelPod, &QSlider::valueChanged,
                     this, &MainWindow::handleBackRightWheelPodSet);
    QObject::connect(ui->pushButton_Subscribe, &QPushButton::clicked,
                     this, &MainWindow::handleSubscribe);
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
    if (0 == m_desiredConfig) { // straight
        LocomotionControlCommandStraight msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / 100.0F);
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
    } else {
        ui->consoleOutputTextBrowser->append("Wrong config");
    }
}

void MainWindow::handleLocomotionDown() {
    if (0 == m_desiredConfig) { // straight
        LocomotionControlCommandStraight msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / -100.0F);
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
    } else {
        ui->consoleOutputTextBrowser->append("Wrong config");
    }
}

void MainWindow::handleLocomotionLeft() {
    if (1 == m_desiredConfig) { // turn
        LocomotionControlCommandTurn msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / -100.0F);
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
    } else if (2 == m_desiredConfig) { // strafe
        LocomotionControlCommandStrafe msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / -100.0F);
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
        ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.strafe");

        free(msg_buff);
    } else {
        ui->consoleOutputTextBrowser->append("Wrong config");
    }
}

void MainWindow::handleLocomotionRight() {
    if (1 == m_desiredConfig) { // turn
        LocomotionControlCommandTurn msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / 100.0F);
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
    } else if (2 == m_desiredConfig) { // strafe
        LocomotionControlCommandStrafe msg;
        msg.set_speed(ui->slider_LocomotionSpeed->value() / 100.0F);
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
        ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.strafe");

        free(msg_buff);
    } else {
        ui->consoleOutputTextBrowser->append("Wrong config");
    }
}

void MainWindow::handleLocomotionRelease() {
    /*
    if (0 == m_desiredConfig) { // straight
        LocomotionControlCommandStraight msg;
        msg.set_speed(0.0F);
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
    } else if (1 == m_desiredConfig) { // turn
        LocomotionControlCommandTurn msg;
        msg.set_speed(0.0F);
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
    } else if (2 == m_desiredConfig) { // strafe
        LocomotionControlCommandStrafe msg;
        msg.set_speed(0.0F);
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
        ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.strafe");

        free(msg_buff);
    } else {
        ui->consoleOutputTextBrowser->append("Wrong config");
    }
    */
}

void MainWindow::handleLocomotionStop() {
    LocomotionControlCommandStrafe msg;
    msg.set_speed(0.0F);
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
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.strafe");

    free(msg_buff);
}

void MainWindow::handleLocomotionStraight() {
    m_configSpeeds[m_desiredConfig] = ui->slider_LocomotionSpeed->value();
    m_desiredConfig = 0;
    ui->slider_LocomotionSpeed->setValue(m_configSpeeds[m_desiredConfig]);
    LocomotionControlCommandConfigure msg;
    msg.set_power(100.0F);
    msg.set_target(LocomotionControlCommandConfigure_Configuration_STRAIGHT_CONFIG);
    msg.set_timeout(456.0F);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.configure");

    free(msg_buff);
}

void MainWindow::handleLocomotionTurn() {
    m_configSpeeds[m_desiredConfig] = ui->slider_LocomotionSpeed->value();
    m_desiredConfig = 1;
    ui->slider_LocomotionSpeed->setValue(m_configSpeeds[m_desiredConfig]);
    LocomotionControlCommandConfigure msg;
    msg.set_power(100.0F);
    msg.set_target(LocomotionControlCommandConfigure_Configuration_TURN_CONFIG);
    msg.set_timeout(456.0F);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.configure");

    free(msg_buff);
}

void MainWindow::handleLocomotionStrafe() {
    m_configSpeeds[m_desiredConfig] = ui->slider_LocomotionSpeed->value();
    m_desiredConfig = 2;
    ui->slider_LocomotionSpeed->setValue(m_configSpeeds[m_desiredConfig]);
    LocomotionControlCommandConfigure msg;
    msg.set_power(100.0F);
    msg.set_target(LocomotionControlCommandConfigure_Configuration_STRAFE_CONFIG);
    msg.set_timeout(456.0F);
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "subsyscommand.locomotion.configure");

    free(msg_buff);
}

void MainWindow::handleFrontLeftWheelStop() {
    ui->slider_FrontLeftWheel->setValue(0);
}

void MainWindow::handleFrontLeftWheelSet(int value) {
    SpeedContolCommand msg;
    msg.set_rpm(60 * (value / 100.0F));
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.front_left.wheel_rpm");

    free(msg_buff);
}

void MainWindow::handleFrontRightWheelStop() {
    ui->slider_FrontRightWheel->setValue(0);
}

void MainWindow::handleFrontRightWheelSet(int value) {
    SpeedContolCommand msg;
    msg.set_rpm(60 * (value / 100.0F));
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.front_right.wheel_rpm");

    free(msg_buff);
}

void MainWindow::handleBackLeftWheelStop() {
    ui->slider_BackLeftWheel->setValue(0);
}

void MainWindow::handleBackLeftWheelSet(int value) {
    SpeedContolCommand msg;
    msg.set_rpm(60 * (value / 100.0F));
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.back_left.wheel_rpm");

    free(msg_buff);
}

void MainWindow::handleBackRightWheelStop() {
    ui->slider_BackRightWheel->setValue(0);
}

void MainWindow::handleBackRightWheelSet(int value) {
    SpeedContolCommand msg;
    msg.set_rpm(60 * (value / 100.0F));
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.back_right.wheel_rpm");

    free(msg_buff);
}

void MainWindow::handleFrontLeftWheelPodStrafe() {
    ui->slider_FrontLeftWheelPod->setValue(90);
}

void MainWindow::handleFrontLeftWheelPodTurn() {
    ui->slider_FrontLeftWheelPod->setValue(60);
}

void MainWindow::handleFrontLeftWheelPodStraight() {
    ui->slider_FrontLeftWheelPod->setValue(0);
}

void MainWindow::handleFrontLeftWheelPodSet(int value) {
    PositionContolCommand msg;
    msg.set_position(value);
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.front_left.wheel_pod_pos");

    free(msg_buff);
}

void MainWindow::handleFrontRightWheelPodStrafe() {
    ui->slider_FrontRightWheelPod->setValue(90);
}

void MainWindow::handleFrontRightWheelPodTurn() {
    ui->slider_FrontRightWheelPod->setValue(60);
}

void MainWindow::handleFrontRightWheelPodStraight() {
    ui->slider_FrontRightWheelPod->setValue(0);
}

void MainWindow::handleFrontRightWheelPodSet(int value) {
    PositionContolCommand msg;
    msg.set_position(value);
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.front_right.wheel_pod_pos");

    free(msg_buff);
}

void MainWindow::handleBackLeftWheelPodStrafe() {
    ui->slider_BackLeftWheelPod->setValue(90);
}

void MainWindow::handleBackLeftWheelPodTurn() {
    ui->slider_BackLeftWheelPod->setValue(60);
}

void MainWindow::handleBackLeftWheelPodStraight() {
    ui->slider_BackLeftWheelPod->setValue(0);
}

void MainWindow::handleBackLeftWheelPodSet(int value) {
    PositionContolCommand msg;
    msg.set_position(value);
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.back_left.wheel_pod_pos");

    free(msg_buff);
}

void MainWindow::handleBackRightWheelPodStrafe() {
    ui->slider_BackRightWheelPod->setValue(90);
}

void MainWindow::handleBackRightWheelPodTurn() {
    ui->slider_BackRightWheelPod->setValue(60);
}

void MainWindow::handleBackRightWheelPodStraight() {
    ui->slider_BackRightWheelPod->setValue(0);
}

void MainWindow::handleBackRightWheelPodSet(int value) {
    PositionContolCommand msg;
    msg.set_position(value);
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
    ex->Publish((char*)msg_buff, msg_size, "motorcontrol.locomotion.back_right.wheel_pod_pos");

    free(msg_buff);
}

void MainWindow::handleSubscribe() {
    ConsumerThread *thread = new ConsumerThread(m_amqp);
    connect(thread, &ConsumerThread::stateReady, this, &MainWindow::handleState);
    connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
    thread->start();

    StateSubscribe msg;
    msg.set_replykey("abcde");
    msg.set_interval(0.2F);
    msg.set_locomotion_summary(false);
    msg.set_locomotion_detailed(true);
    msg.set_deposition_summary(false);
    msg.set_deposition_detailed(false);
    msg.set_excavation_summary(false);
    msg.set_excavation_detailed(false);

    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }

    msg.SerializeToArray(msg_buff, msg_size);

    AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    ex->Publish((char*)msg_buff, msg_size, "state.subscribe");
}

void MainWindow::handleState(State *s) {
    rectangle1->setRotation(s->locdetailed().front_left_pos());
    rectangle2->setRotation(-s->locdetailed().front_right_pos());
    rectangle3->setRotation(-s->locdetailed().back_left_pos());
    rectangle4->setRotation(s->locdetailed().back_right_pos());
}

void MainWindow::keyPressEvent(QKeyEvent *ev) {
    if (ev->isAutoRepeat()) {
        QWidget::keyPressEvent(ev);
    } else {
        switch (ev->key()) {
        case (Qt::Key_Space):
            handleLocomotionStop();
            break;
        case (Qt::Key_W):
            handleLocomotionUp();
            break;
        case (Qt::Key_A):
            handleLocomotionLeft();
            break;
        case (Qt::Key_S):
            handleLocomotionDown();
            break;
        case (Qt::Key_D):
            handleLocomotionRight();
            break;
        case (Qt::Key_I):
            handleLocomotionStraight();
            break;
        case (Qt::Key_O):
            handleLocomotionTurn();
            break;
        case (Qt::Key_P):
            handleLocomotionStrafe();
            break;
        case (Qt::Key_J):
            ui->slider_LocomotionSpeed->setValue(ui->slider_LocomotionSpeed->value() - 10);
            break;
        case (Qt::Key_K):
            ui->slider_LocomotionSpeed->setValue(ui->slider_LocomotionSpeed->value() + 10);
            break;
        default:
            QWidget::keyPressEvent(ev);
            break;
        }
    }
}

void MainWindow::keyReleaseEvent(QKeyEvent *ev) {
    if (ev->isAutoRepeat()) {
        QWidget::keyReleaseEvent(ev);
    } else {
        switch (ev->key()) {
        case (Qt::Key_Space):
            break;
        case (Qt::Key_W):
            handleLocomotionStop();
            break;
        case (Qt::Key_A):
            handleLocomotionStop();
            break;
        case (Qt::Key_S):
            handleLocomotionStop();
            break;
        case (Qt::Key_D):
            handleLocomotionStop();
            break;
        case (Qt::Key_I):
            break;
        case (Qt::Key_O):
            break;
        case (Qt::Key_P):
            break;
        case (Qt::Key_J):
            break;
        case (Qt::Key_K):
            break;
        default:
            QWidget::keyReleaseEvent(ev);
            break;
        }
    }
}

void MainWindow::wheelEvent(QWheelEvent* event) {
    int delta = event->angleDelta().y();
    delta = (delta > 0) ? 5 : -5;
    ui->slider_LocomotionSpeed->setValue(ui->slider_LocomotionSpeed->value() + delta);
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


void MainWindow::on_pushButton_3_clicked()
{
    mDialog1 = new MyDialog(this);
    mDialog2 = new MyDialog2(this);
    mDialog3 = new MyDialog3(this);
    mDialog4 = new MyDialog4(this);
    mDialog5 = new MyDialog5(this);
    mDialog1->show();
    mDialog2->show();
    mDialog3->show();
    mDialog4->show();
    mDialog5->show();
}
