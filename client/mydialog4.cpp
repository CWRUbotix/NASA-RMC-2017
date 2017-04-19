#include "mydialog4.h"
#include "ui_mydialog4.h"

MyDialog4::MyDialog4(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::MyDialog4)
{
    ui->setupUi(this);
}

MyDialog4::~MyDialog4()
{
    delete ui;
}
