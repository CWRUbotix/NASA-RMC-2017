#ifndef MYDIALOG3_H
#define MYDIALOG3_H

#include <QDialog>

namespace Ui {
class MyDialog3;
}

class MyDialog3 : public QDialog
{
    Q_OBJECT

public:
    explicit MyDialog3(QWidget *parent = 0);
    ~MyDialog3();

private:
    Ui::MyDialog3 *ui;
};

#endif // MYDIALOG3_H
