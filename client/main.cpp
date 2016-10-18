#include "mainwindow.h"
#include "connectiondialog.h"
#include <QApplication>
#include <QDebug>
#include <amqp_tcp_socket.h>
#include <amqp.h>
#include <amqp_framing.h>
#include "amqp_utils.h"
#include <QMessageBox>

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

    int status = amqp_socket_open(socket, address.toStdString().c_str(), 5672);
    if (AMQP_STATUS_OK != status) {
        QMessageBox::critical(0,"Error","Failed to open socket.\nDetails: amqp_socket_open() returned: " + QString::number(status));
        return 1;
    }

    amqp_rpc_reply_t reply = amqp_login(conn, "/", 0, 131072, 0, AMQP_SASL_METHOD_PLAIN, "guest", "guest");
    if (AMQP_RESPONSE_NORMAL != reply.reply_type) {
        QMessageBox::critical(0,"Error","Failed to log in to server.\nDetails: amqp_login() returned: " + amqp_error_message(reply));
        return 1;
    }
    amqp_channel_open(conn, 1);
    reply = amqp_get_rpc_reply(conn);
    if (AMQP_RESPONSE_NORMAL != reply.reply_type) {
        QMessageBox::critical(0,"Error","Failed to open channel.\nDetails: amqp_get_rpc_reply() returned: " + amqp_error_message(reply));
        return 1;
    }

    MainWindow w(conn);
    w.show();

    int output = a.exec();

    reply = amqp_channel_close(conn, 1, AMQP_REPLY_SUCCESS);
    if (AMQP_RESPONSE_NORMAL != reply.reply_type) {
        QMessageBox::critical(0,"Error","Failed to close channel.\nDetails: amqp_channel_close() returned: " + amqp_error_message(reply));
        return 1;
    }

    reply = amqp_connection_close(conn, AMQP_REPLY_SUCCESS);
    if (AMQP_RESPONSE_NORMAL != reply.reply_type) {
        QMessageBox::critical(0,"Error","Failed to close connection.\nDetails: amqp_connection_close() returned: " + amqp_error_message(reply));
        return 1;
    }

    status = amqp_destroy_connection(conn);
    if (AMQP_STATUS_OK != status) {
        QMessageBox::critical(0,"Error","Failed to destruct connection.\nDetails: amqp_destroy_connection() returned: " + QString::number(status));
        return 1;
    }

    return output;
}
