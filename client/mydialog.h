#ifndef MYDIALOG_H
#define MYDIALOG_H
#include <opencv/cv.h>
#include <opencv/cv.hpp>
#include <opencv/highgui.h>
#include <opencv/ml.h>
#include <opencv/cvaux.h>
#include <opencv/cvaux.hpp>
#include <opencv2/cvconfig.h>
#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/video.hpp>
#include <opencv2/videoio.hpp>
#include <QMutex>
#include <QThread>
#include <QImage>
#include <QWaitCondition>
#include <QDialog>
#include <AMQPcpp.h>
#include "messages.pb.h"

namespace Ui {
class MyDialog;
}

class MyDialog : public QDialog
{
    Q_OBJECT

public:
    explicit MyDialog(QWidget *parent = 0);
    ~MyDialog();

public slots:

private:
    Ui::MyDialog *ui;
    //cv::VideoCapture capture;
    cv::Mat frame;
    QImage img;

};

#endif // MYDIALOG_H
