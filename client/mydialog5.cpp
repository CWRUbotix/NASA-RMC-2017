#include "mydialog5.h"
#include "ui_mydialog5.h"

MyDialog5::MyDialog5(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::MyDialog5)
{
    ui->setupUi(this);
}

MyDialog5::~MyDialog5()
{
    delete ui;
}
