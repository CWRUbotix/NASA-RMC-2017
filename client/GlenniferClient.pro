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
    amqp_utils.cpp \
    messages.pb.cc

HEADERS  += mainwindow.h \
    connectiondialog.h \
    amqp_utils.h \
    messages.pb.h

FORMS    += mainwindow.ui \
    connectiondialog.ui

CONFIG += conan_basic_setup
include(conanbuildinfo.pri)

win32:CONFIG(release, debug|release): LIBS += -L$$PWD/../protobuf-3.1.0/cmake/build/release/ -llibprotobuf
else:win32:CONFIG(debug, debug|release): LIBS += -L$$PWD/../protobuf-3.1.0/cmake/build/debug/ -llibprotobuf
else:unix: LIBS += -L/usr/local/lib/ -lprotobuf

win32:INCLUDEPATH += $$PWD/../protobuf-3.1.0/src
win32:DEPENDPATH += $$PWD/../protobuf-3.1.0/src
