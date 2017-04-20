#include "mydialog.h"
#include "ui_mydialog.h"

MyDialog::MyDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::MyDialog)
{
    ui->setupUi(this);
}

MyDialog::~MyDialog()
{
    delete ui;
}
void MyDialog::assignFrame(Mat frame) {
     cv::cvtColor(frame, RGBFrame, CV_BGR2RGB);
     img = QImage((const unsigned char*)(RGBFrame.data),
                  RGBFrame.cols, RGBFrame.rows, QImage::Format_RGB888);
     pmap = QPixmap::fromImage(img);
}

void MyDialog::assignDisplay(QImage img) {

}

void MyDialog::updateFrame(Mat frame) {
    while(true) { //add stop function
        assignFrame(frame);
    }
}
