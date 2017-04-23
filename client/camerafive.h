#ifndef CAMERAFIVE_H
#define CAMERAFIVE_H
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
class CameraFive;
}

class CameraFive : public QDialog
{
    Q_OBJECT

public:
    explicit CameraFive(QWidget *parent = 0, QString login = "login");
    ~CameraFive();

public slots:
    void handleFrame(QString key, QByteArray data);
    void camFiveStream();
    void camFiveSubscription();

private slots:


private:
    Ui::CameraFive *ui;
    QString str_login;
    AMQP *m_amqp;
};
#endif // CAMERAFIVE_H
