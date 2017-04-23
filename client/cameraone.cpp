#include "cameraone.h"
#include "ui_cameraone.h"
#include "consumerthread.h"
#include <opencv/cv.h>
#include <opencv2/cvconfig.h>
#include <opencv2/imgcodecs/imgcodecs.hpp>
#include <opencv2/imgcodecs/imgcodecs_c.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <io.h>
#include <iostream>

typedef unsigned char byte;

CameraOne::CameraOne(QWidget *parent, QString login) :
    QDialog(parent),
    ui(new Ui::CameraOne)
{
    ui->setupUi(this);
    str_login = login;
}

CameraOne::~CameraOne()
{
    delete ui;
}

void CameraOne::handleFrame(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), 64300, "JPEG");
    ui->cam1lbl->setPixmap(pix);
}

void CameraOne::camOneSubscription() {
    QString login = str_login;
    ConsumerThread *thread = new ConsumerThread(str_login, "camera.one");
    connect(thread, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrame);
    connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
    thread->start();
}

void CameraOne::camOneStream() {
    CameraOne::camOneSubscription();
}
