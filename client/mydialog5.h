#ifndef MYDIALOG5_H
#define MYDIALOG5_H

#include <QDialog>

namespace Ui {
class MyDialog5;
}

class MyDialog5 : public QDialog
{
    Q_OBJECT

public:
    explicit MyDialog5(QWidget *parent = 0);
    ~MyDialog5();

private:
    Ui::MyDialog5 *ui;
};

#endif // MYDIALOG5_H
