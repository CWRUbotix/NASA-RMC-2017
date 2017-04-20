#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <AMQPcpp.h>
#include "messages.pb.h"
#include <QGraphicsScene>
#include <QGraphicsView>
#include <QGraphicsItem>
#include <QKeyEvent>
#include <QWheelEvent>
<<<<<<< HEAD
#include <QFileDialog>
#include <QMessageBox>
#include "mydialog.h"
#include "mydialog2.h"
#include "mydialog3.h"
#include "mydialog4.h"
#include "mydialog5.h"
=======
#include <QCloseEvent>
>>>>>>> master

using namespace com::cwrubotix::glennifer;

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    explicit MainWindow(QString loginStr, QWidget *parent = 0);
    ~MainWindow();
    void initSubscription();

    static MainWindow instance;

public slots:
    void handleLocomotionUp();
    void handleLocomotionDown();
    void handleLocomotionLeft();
    void handleLocomotionRight();
    void handleLocomotionRelease();
    void handleLocomotionStop();
    void handleLocomotionStraight();
    void handleLocomotionTurn();
    void handleLocomotionStrafe();
    void handleFrontLeftWheelStop();
    void handleFrontLeftWheelSet(int value);
    void handleFrontRightWheelStop();
    void handleFrontRightWheelSet(int value);
    void handleBackLeftWheelStop();
    void handleBackLeftWheelSet(int value);
    void handleBackRightWheelStop();
    void handleBackRightWheelSet(int value);
    void handleFrontLeftWheelPodStraight();
    void handleFrontLeftWheelPodTurn();
    void handleFrontLeftWheelPodStrafe();
    void handleFrontLeftWheelPodSet(int value);
    void handleFrontRightWheelPodStraight();
    void handleFrontRightWheelPodTurn();
    void handleFrontRightWheelPodStrafe();
    void handleFrontRightWheelPodSet(int value);
    void handleBackLeftWheelPodStraight();
    void handleBackLeftWheelPodTurn();
    void handleBackLeftWheelPodStrafe();
    void handleBackLeftWheelPodSet(int value);
    void handleBackRightWheelPodStraight();
    void handleBackRightWheelPodTurn();
    void handleBackRightWheelPodStrafe();
    void handleBackRightWheelPodSet(int value);
    void handleExcavationArmSet(int value);
    void handleExcavationArmDig();
    void handleExcavationArmJog();
    void handleExcavationArmStore();
    void handleExcavationTranslationSet(int value);
    void handleExcavationTranslationExtend();
    void handleExcavationTranslationStop();
    void handleExcavationTranslationRetract();
    void handleExcavationConveyor(bool checked);
    void handleDepositionDumpSet(int value);
    void handleDepositionDumpDump();
    void handleDepositionDumpStop();
    void handleDepositionDumpStore();
    void handleDepositionConveyor(bool checked);
    void handleSubscribe();

    void handleState(QString key, QByteArray data);

    void keyPressEvent(QKeyEvent *ev);
    void keyReleaseEvent(QKeyEvent *ev);
    void wheelEvent(QWheelEvent* event);
    void closeEvent(QCloseEvent *event);


private slots:
    void on_pushButton_3_clicked();

private:
    Ui::MainWindow *ui;
    AMQP *m_amqp;
    QString m_loginStr;
    QGraphicsScene *locomotionScene;
    QGraphicsScene *excavationScene;
    QGraphicsScene *depositionScene;
    QGraphicsRectItem *rectangle1;
    QGraphicsRectItem *rectangle2;
    QGraphicsRectItem *rectangle3;
    QGraphicsRectItem *rectangle4;
    int m_desiredConfig = 0; // 0 is straight, 1 is turn, 2 is strafe
    int m_configSpeeds[3] = {100, 60, 50};
    void updateAngle(int x);

    MyDialog *mDialog1;
    MyDialog2 *mDialog2;
    MyDialog3 *mDialog3;
    MyDialog4 *mDialog4;
    MyDialog5 *mDialog5;
};

#endif // MAINWINDOW_H
