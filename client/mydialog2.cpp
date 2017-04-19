#include "mydialog2.h"
#include "ui_mydialog2.h"

MyDialog2::MyDialog2(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::MyDialog2)
{
    ui->setupUi(this);
}

MyDialog2::~MyDialog2()
{
    delete ui;
}
