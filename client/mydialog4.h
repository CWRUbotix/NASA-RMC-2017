#ifndef MYDIALOG4_H
#define MYDIALOG4_H

#include <QDialog>

namespace Ui {
class MyDialog4;
}

class MyDialog4 : public QDialog
{
    Q_OBJECT

public:
    explicit MyDialog4(QWidget *parent = 0);
    ~MyDialog4();

private:
    Ui::MyDialog4 *ui;
};

#endif // MYDIALOG4_H
