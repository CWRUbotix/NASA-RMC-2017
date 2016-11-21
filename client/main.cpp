#include "mainwindow.h"
#include "connectiondialog.h"
#include <QApplication>
#include <QDebug>
#include <AMQPcpp.h>
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
	
    AMQP *amqp = new AMQP(address.toStdString());

    MainWindow w(amqp);
    w.show();

    int output = a.exec();

    return output;
}
