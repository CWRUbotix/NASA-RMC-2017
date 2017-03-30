#include "mainwindow.h"
#include "connectiondialog.h"
#include <QApplication>
#include <AMQPcpp.h>
#include <QMessageBox>
#include <yaml-cpp/yaml.h>
#include <QDebug>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    amqp_connection_state_t conn = amqp_new_connection();
    amqp_socket_t *socket = amqp_tcp_socket_new(conn);

    if (!socket) {
      QMessageBox::critical(0,"Error","Failed to allocate socket.\nDetails: amqp_tcp_socket_new() returned NULL");
      return 1;
    }

    ConnectionDialog d;
    d.exec();
    if (d.result() != QDialog::Accepted) {
        return 0;
    }

    QString address = d.URL();

    YAML::Node connectionConfig;
    try {
        connectionConfig = YAML::LoadFile("config/connection.yml");
    } catch (YAML::BadFile) {
        QMessageBox::critical(0,"Error","No connection config file found");
        return 1;
    }

    if (address.isEmpty()) {
        if (connectionConfig["server-addr"]) {
            address = QString::fromStdString(connectionConfig["server-addr"].as<std::string>());
        } else {
            QMessageBox::critical(0,"Error","No server address found in connection config");
            return 1;
        }
    }

    QString user;
    QString pass;

    if (connectionConfig["server-user"]) {
        user = QString::fromStdString(connectionConfig["server-user"].as<std::string>());
    } else {
        QMessageBox::critical(0,"Error","No server username found in connection config");
        return 1;
    }

    if (connectionConfig["server-pass"]) {
        pass = QString::fromStdString(connectionConfig["server-pass"].as<std::string>());
    } else {
        QMessageBox::critical(0,"Error","No server password found in connection config");
        return 1;
    }

    QString loginStr = user + ":" + pass + "@" + address;

    AMQP *amqp;
    try {
        amqp = new AMQP(loginStr.toStdString());
    } catch (AMQPException) {
        QMessageBox::critical(0,"Error",QString::fromStdString("AMQP connection error"));
        return 1;
    }

    MainWindow w(amqp);
    w.show();

    int output = a.exec();
    return output;
}
