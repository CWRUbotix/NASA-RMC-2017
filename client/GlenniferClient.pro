#-------------------------------------------------
#
# Project created by QtCreator 2016-10-17T21:21:14
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = GlenniferClient
TEMPLATE = app

INCLUDEPATH += $$PWD/../pb

SOURCES += main.cpp\
        mainwindow.cpp \
    connectiondialog.cpp \
    amqp_utils.cpp \
    ../pb/messages.pb.cc

HEADERS  += mainwindow.h \
    connectiondialog.h \
    amqp_utils.h \
    ../pb/messages.pb.h

FORMS    += mainwindow.ui \
    connectiondialog.ui

win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../third-party/rabbitmq-c/build/librabbitmq/ -lrabbitmq.4
else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../third-party/rabbitmq-c/build/librabbitmq/ -lrabbitmq.4
else:unix: LIBS += -L$$PWD/../third-party/rabbitmq-c/build/librabbitmq/ -lrabbitmq

INCLUDEPATH += $$PWD/../third-party/rabbitmq-c/librabbitmq
DEPENDPATH += $$PWD/../third-party/rabbitmq-c/librabbitmq

win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../protobuf-3.1.0/cmake/build/release/ -llibprotobuf
else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../protobuf-3.1.0/cmake/build/debug/ -llibprotobuf
else:unix: LIBS += -L/usr/local/lib/ -lprotobuf

INCLUDEPATH += $$PWD/../protobuf-3.1.0/src
DEPENDPATH += $$PWD/../protobuf-3.1.0/src
