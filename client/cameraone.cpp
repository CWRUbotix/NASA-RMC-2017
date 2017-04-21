#include "cameraone.h"
#include "ui_cameraone.h"
//#include "consumerthread.cpp"

CameraOne::CameraOne(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::CameraOne)
{
    ui->setupUi(this);
}

CameraOne::~CameraOne()
{
    delete ui;
}

void CameraOne::on_label_linkActivated(cv::Mat mat) {
    cv::cvtColor(mat, mat, CV_BGR2RGB);
    ui->cam1lbl->setPixmap(QPixmap::fromImage(QImage(mat.data, mat.cols, mat.rows, mat.step, QImage::Format_RGB888)));
}

/*void CameraOne::startStream(cv::Mat mat) {
  runs for a loop displaying video
  since messaging is invloved, maybe its better to do this in another cpp like main window
}*/

/*void CameraOne::handleMessage() {

}*/
