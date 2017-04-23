#ifndef CAMERAFOUR_H
#define CAMERAFOUR_H
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
class CameraFour;
}

class CameraFour : public QDialog
{
    Q_OBJECT

public:
    explicit CameraFour(QWidget *parent = 0, QString login = "login");
    ~CameraFour();

public slots:
    void handleFrame(QString key, QByteArray data);
    void camFourStream();
    void camFourSubscription();

private slots:


private:
    Ui::CameraFour *ui;
    QString str_login;
    AMQP *m_amqp;
};

#endif // CAMERAFOUR_H
