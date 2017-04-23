#include "camerathree.h"
#include "ui_camerathree.h"
#include <opencv/cv.h>
#include <opencv2/cvconfig.h>
#include <opencv2/imgcodecs/imgcodecs.hpp>
#include <opencv2/imgcodecs/imgcodecs_c.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <io.h>
#include <iostream>
#include "consumerthread.h"

typedef unsigned char byte;

CameraThree::CameraThree(QWidget *parent, QString login) :
    QDialog(parent),
    ui(new Ui::CameraThree)
{
    ui->setupUi(this);
    str_login = login;
}

CameraThree::~CameraThree()
{
    delete ui;
}

void CameraThree::handleFrame(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), 64300, "JPEG");
    ui->cam3lbl->setPixmap(pix);
}

void CameraThree::camThreeSubscription() {
    QString login = str_login;
    ConsumerThread *thread = new ConsumerThread(str_login, "camera.three");
    connect(thread, &ConsumerThread::receivedMessage, this, &CameraThree::handleFrame);
    connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
    thread->start();
}

void CameraThree::camThreeStream() {
    CameraThree::camThreeSubscription();
}
