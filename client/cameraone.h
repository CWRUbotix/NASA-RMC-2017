#ifndef CAMERAONE_H
#define CAMERAONE_H
#include <opencv2/core/core.hpp>
#include <opencv2/video.hpp>
#include <opencv/cv.h>
#include <opencv/cv.hpp>
#include <AMQPcpp.h>
#include <QDialog>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgcodecs.hpp>
#include <stdio.h>
#include <iostream>
#include <opencv2/imgproc.hpp>
#include "consumerthread.h"

namespace Ui {
class CameraOne;
}

class CameraOne : public QDialog
{
    Q_OBJECT

public:
    explicit CameraOne(QWidget *parent = 0, QString login = "login");
    ~CameraOne();

public slots:
    void handleFrameOne(QString key, QByteArray data);
    void camOneStream();
    void camOneSubscription();
    void camOneEnd();
    void handleCamOne_Box(bool checked);

    void handleFrameTwo(QString key, QByteArray data);
    void camTwoStream();
    void camTwoSubscription();
    void camTwoEnd();
    void handleCamTwo_Box(bool checked);

    void handleFrameThree(QString key, QByteArray data);
    void camThreeStream();
    void camThreeSubscription();
    void camThreeEnd();
    void handleCamThree_Box(bool checked);

    void handleFrameFour(QString key, QByteArray data);
    void camFourStream();
    void camFourSubscription();
    void camFourEnd();
    void handleCamFour_Box(bool checked);

    void handleFrameFive(QString key, QByteArray data);
    void camFiveStream();
    void camFiveSubscription();
    void camFiveEnd();
    void handleCamFive_Box(bool checked);

private slots:


private:
    Ui::CameraOne *ui;
    QString str_login;
    ConsumerThread *thread1;
    ConsumerThread *thread2;
    ConsumerThread *thread3;
    ConsumerThread *thread4;
    ConsumerThread *thread5;
};

#endif // CAMERAONE_H
