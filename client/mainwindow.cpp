#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <amqp_tcp_socket.h>
#include <amqp.h>
#include <amqp_framing.h>
#include "amqp_utils.h"
#include "messages.pb.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

//    QObject::connect(ui->locomotion_UpButton, &QPushButton::clicked,
//                     this, &MainWindow::handleLocomotionUp);
//    QObject::connect(ui->locomotion_DownButton, &QPushButton::clicked,
//                     this, &MainWindow::handleLocomotionDown);
//    QObject::connect(ui->locomotion_LeftButton, &QPushButton::clicked,
//                     this, &MainWindow::handleLocomotionLeft);
//    QObject::connect(ui->locomotion_RightButton, &QPushButton::clicked,
//                     this, &MainWindow::handleLocomotionRight);
}

//MainWindow::MainWindow(amqp_connection_state_t conn, QWidget *parent) :
//    MainWindow::MainWindow(parent)
//{
//    m_conn = conn;
//}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::handleLocomotion(LocomotionControl_LocomotionType direction) {
    LocomotionControl msg;
    msg.set_locomotiontype(direction);
 //   msg.set_speed_percent(ui->locomotion_SpeedSlider->value());
 //   msg.set_timeout_ms(ui->locomotion_DurationSlider->value());
    int msg_size = msg.ByteSize();
    void *msg_buff = malloc(msg_size);
    if (!msg_buff) {
        ui->consoleOutputTextBrowser->append("Failed to allocate message buffer.\nDetails: malloc(msg_size) returned: NULL\n");
        return;
    }
    msg.SerializeToArray(msg_buff, msg_size);
    amqp_bytes_t message_bytes;
    message_bytes.len = msg_size;
    message_bytes.bytes = msg_buff;
    const char* queue_name = "locomotion";
    int status = amqp_basic_publish(m_conn,
                                    1,
                                    amqp_cstring_bytes(""),
                                    amqp_cstring_bytes(queue_name),
                                    0,
                                    0,
                                    NULL,
                                    message_bytes);
    if (AMQP_STATUS_OK != status) {
        ui->consoleOutputTextBrowser->append("Failed to publish message.\nDetails: amqp_basic_publish() returned: " + QString::number(status) + "\n");
    }
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
