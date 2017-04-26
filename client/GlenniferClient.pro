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
    messages.pb.cc \
    consumerthread.cpp \
    cameraone.cpp \
    cameratwo.cpp \
    camerathree.cpp \
    camerafour.cpp \
    camerafive.cpp

HEADERS  += mainwindow.h \
    connectiondialog.h \
    messages.pb.h \
    consumerthread.h \
    cameraone.h \
    cameratwo.h \
    camerathree.h \
    camerafour.h \
    camerafive.h

FORMS    += mainwindow.ui \
    connectiondialog.ui \
    cameraone.ui \
    cameratwo.ui \
    camerathree.ui \
    camerafour.ui \
    camerafive.ui

CONFIG += conan_basic_setup
include(conanbuildinfo.pri)

unix: LIBS += -L/usr/local/lib/ -lprotobuf
