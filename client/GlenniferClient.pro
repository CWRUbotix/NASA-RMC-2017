#-------------------------------------------------
#
# Project created by QtCreator 2016-10-17T21:21:14
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = GlenniferClient
TEMPLATE = app

INCLUDEPATH += $$PWD/..

SOURCES += main.cpp\
        mainwindow.cpp \
    connectiondialog.cpp \
    amqp_utils.cpp \
    ../messages.pb.cc

HEADERS  += mainwindow.h \
    connectiondialog.h \
    amqp_utils.h \
    ../messages.pb.h

FORMS    += mainwindow.ui \
    connectiondialog.ui

#win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/release/ -lrabbitmq.4
#else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/debug/ -lrabbitmq.4
#else:unix: LIBS += -L$$PWD/../../rabbitmq-c/build/librabbitmq/ -lrabbitmq.4

INCLUDEPATH += $$PWD/../../rabbitmq-c/librabbitmq $$PWD/../third-party/rabbitmq-c/librabbitmq
DEPENDPATH += $$PWD/../../rabbitmq-c/librabbitmq

win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../third-party/protobuf/cmake/build/release/ -llibprotobuf
else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../third-party/protobuf/cmake/build/debug/ -llibprotobuf
#else:unix: LIBS += -L$$PWD/../third-party/protobuf/cmake/build/ -llibprotobuf -L/usr/local/lib/ -llibprotobuf
else:unix: LIBS += -L/usr/local/lib/ -lprotobuf

INCLUDEPATH += $$PWD/../third-party/protobuf/src
DEPENDPATH += $$PWD/../third-party/protobuf/src
