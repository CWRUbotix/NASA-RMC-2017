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

namespace Ui {
class CameraOne;
}

class CameraOne : public QDialog
{
    Q_OBJECT

public:
    explicit CameraOne(QWidget *parent = 0, QString login = "login");
    ~CameraOne();
    void on_label_linkActivated(cv::Mat frame); //took argument off

public slots:
    void handleFrame(QString key, QByteArray data);
    void camOneStream();
    //void on_label_linkActivated(); //took argument off cv::Mat frame
    void camOneSubscription();

private slots:


private:
    Ui::CameraOne *ui;
    //cv::Mat frame;
    //cv::Mat temp;
    //QImage *img;
    QString str_login;
    AMQP *m_amqp;
};

#endif // CAMERAONE_H
