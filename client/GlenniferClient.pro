#-------------------------------------------------
#
# Project created by QtCreator 2016-10-17T21:21:14
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = GlenniferClient
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    connectiondialog.cpp \
    amqp_utils.cpp

HEADERS  += mainwindow.h \
    connectiondialog.h \
    amqp_utils.h

FORMS    += mainwindow.ui \
    connectiondialog.ui

win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/release/ -lrabbitmq.4
else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/debug/ -lrabbitmq.4
else:unix: LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/ -lrabbitmq.4

INCLUDEPATH += $$PWD/../../rabbitmq-c/librabbitmq
DEPENDPATH += $$PWD/../../rabbitmq-c/librabbitmq
