#include "mydialog3.h"
#include "ui_mydialog3.h"

MyDialog3::MyDialog3(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::MyDialog3)
{
    ui->setupUi(this);
}

MyDialog3::~MyDialog3()
{
    delete ui;
}
