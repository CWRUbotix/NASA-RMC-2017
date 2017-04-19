#ifndef MYDIALOG2_H
#define MYDIALOG2_H

#include <QDialog>

namespace Ui {
class MyDialog2;
}

class MyDialog2 : public QDialog
{
    Q_OBJECT

public:
    explicit MyDialog2(QWidget *parent = 0);
    ~MyDialog2();

private:
    Ui::MyDialog2 *ui;
};

#endif // MYDIALOG2_H
