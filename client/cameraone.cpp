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

/* Deleted All other Camera Windows */

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

//Camera One

void CameraOne::handleFrameOne(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), data.length(), "JPEG");
    ui->cam1lbl->setPixmap(pix);
}

void CameraOne::handleFrameTwo(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), data.length(), "JPEG");
    ui->cam2lbl->setPixmap(pix);
}

void CameraOne::handleFrameThree(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), data.length(), "JPEG");
    ui->cam3lbl->setPixmap(pix);
}

void CameraOne::handleFrameFour(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), data.length(), "JPEG");
    ui->cam4lbl->setPixmap(pix);
}

void CameraOne::handleFrameFive(QString key, QByteArray data) {
    QPixmap pix;
    pix.loadFromData((uchar*)data.data(), data.length(), "JPEG");
    ui->cam5lbl->setPixmap(pix);
}

void CameraOne::camOneSubscription() {
    QString login = str_login;
    ConsumerThread *thread1 = new ConsumerThread(str_login, "camera.two");
    connect(thread1, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrameOne);
    connect(thread1, SIGNAL(finished()), thread1, SLOT(deleteLater()));
    thread1->start();
}

void CameraOne::camOneStream() {
    CameraOne::camOneSubscription();
}

void CameraOne::camOneEnd() {
    delete thread1;
}

//Camera Two
void CameraOne::camTwoSubscription() {
    QString login = str_login;
    ConsumerThread *thread2 = new ConsumerThread(str_login, "camera.three");
    connect(thread2, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrameTwo);
    connect(thread2, SIGNAL(finished()), thread2, SLOT(deleteLater()));
    thread2->start();
}

void CameraOne::camTwoStream() {
    CameraOne::camTwoSubscription();
}

void CameraOne::camTwoEnd() {
    delete thread2;
}

//Camera Three
void CameraOne::camThreeSubscription() {
    QString login = str_login;
    ConsumerThread *thread3 = new ConsumerThread(str_login, "camera.four");
    connect(thread3, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrameThree);
    connect(thread3, SIGNAL(finished()), thread3, SLOT(deleteLater()));
    thread3->start();
}

void CameraOne::camThreeStream() {
    CameraOne::camThreeSubscription();
}

void CameraOne::camThreeEnd() {
    delete thread3;
}

//Camera Four
void CameraOne::camFourSubscription() {
    QString login = str_login;
    ConsumerThread *thread4 = new ConsumerThread(str_login, "camera.five");
    connect(thread4, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrameFour);
    connect(thread4, SIGNAL(finished()), thread4, SLOT(deleteLater()));
    thread4->start();
}

void CameraOne::camFourStream() {
    CameraOne::camFourSubscription();
}

void CameraOne::camFourEnd() {
    delete thread4;
}

//Camera Five
void CameraOne::camFiveSubscription() {
    QString login = str_login;
    ConsumerThread *thread5 = new ConsumerThread(str_login, "camera.one");
    connect(thread5, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrameFive);
    connect(thread5, SIGNAL(finished()), thread5, SLOT(deleteLater()));
    thread5->start();
}

void CameraOne::camFiveStream() {
    CameraOne::camFiveSubscription();
}

void CameraOne::camFiveEnd() {
    delete thread5;
}
