#include "mydialog.h"
#include "ui_mydialog.h"
#include "consumerthread.cpp"

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
QGraphicsPixmapItem MyDialog::assignFrame(Mat frame) {
     cv::cvtColor(frame, RGBFrame, CV_BGR2RGB);
     img = QImage((const unsigned char*)(RGBFrame.data),
                  RGBFrame.cols, RGBFrame.rows, QImage::Format_RGB888);
     gpmap = QPixmap::fromImage(img);
     return gpmap;
}

void MyDialog::assignDisplay(QGraphicsPixmapItem gpmap) {
    scene->addItem(gpmap);
    view.show();
}

void MyDialog::updateFrame(Mat frame) {
    while(true) { //add stop function
        assignFrame(frame);
    }
}
