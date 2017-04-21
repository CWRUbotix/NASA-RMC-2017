#ifndef CAMERAONE_H
#define CAMERAONE_H
#include <opencv2/core/core.hpp>
#include <opencv2/video.hpp>
#include <opencv/cv.h>
#include <opencv/cv.hpp>

#include <QDialog>

namespace Ui {
class CameraOne;
}

class CameraOne : public QDialog
{
    Q_OBJECT

public:
    explicit CameraOne(QWidget *parent = 0);
    ~CameraOne();

private slots:
    void on_label_linkActivated(cv::Mat mat);

private:
    Ui::CameraOne *ui;
    QImage img;
};

#endif // CAMERAONE_H
