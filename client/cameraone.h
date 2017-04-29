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
    void handleFrame(QString key, QByteArray data);
    void camOneStream();
    void camOneSubscription();

private slots:


private:
    Ui::CameraOne *ui;
    QString str_login;
    AMQP *m_amqp;
};

#endif // CAMERAONE_H
