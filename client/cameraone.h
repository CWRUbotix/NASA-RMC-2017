#ifndef CAMERAONE_H
#define CAMERAONE_H
#include <opencv2/core/core.hpp>
#include <opencv2/video.hpp>
#include <opencv/cv.h>
#include <opencv/cv.hpp>
#include <AMQPcpp.h>
#include <QDialog>

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

private slots:
    void on_label_linkActivated();
    void camOneSubscription();

private:
    Ui::CameraOne *ui;
    cv::Mat frame;
    QImage img;
    QString str_login;
    AMQP *m_amqp;
};

#endif // CAMERAONE_H
