#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <amqp_tcp_socket.h>
#include <amqp.h>
#include <amqp_framing.h>
#include "amqp_utils.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    QObject::connect(ui->locomotion_UpButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionUp);
    QObject::connect(ui->locomotion_DownButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionDown);
    QObject::connect(ui->locomotion_LeftButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionLeft);
    QObject::connect(ui->locomotion_RightButton, &QPushButton::clicked,
                     this, &MainWindow::handleLocomotionRight);
}

MainWindow::MainWindow(amqp_connection_state_t conn, QWidget *parent) :
    MainWindow::MainWindow(parent)
{
    m_conn = conn;
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::handleLocomotionUp() {
    const char* queue_name = "test";
    char message[2] = "w";
    amqp_bytes_t message_bytes;
    message_bytes.len = 1;
    message_bytes.bytes = message;
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
}

void MainWindow::handleLocomotionDown() {
    const char* queue_name = "test";
    char message[2] = "s";
    amqp_bytes_t message_bytes;
    message_bytes.len = 1;
    message_bytes.bytes = message;
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
}

void MainWindow::handleLocomotionLeft() {
    const char* queue_name = "test";
    char message[2] = "a";
    amqp_bytes_t message_bytes;
    message_bytes.len = 1;
    message_bytes.bytes = message;
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
}

void MainWindow::handleLocomotionRight() {
    const char* queue_name = "test";
    char message[2] = "d";
    amqp_bytes_t message_bytes;
    message_bytes.len = 1;
    message_bytes.bytes = message;
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
}
