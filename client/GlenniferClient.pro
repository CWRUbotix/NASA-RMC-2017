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
    mydialog.cpp \
    mydialog2.cpp \
    mydialog3.cpp \
    mydialog4.cpp \
    mydialog5.cpp \
    consumerthread.cpp

HEADERS  += mainwindow.h \
    connectiondialog.h \
    messages.pb.h \
    mydialog.h \
    mydialog2.h \
    mydialog3.h \
    mydialog4.h \
    mydialog5.h \
    consumerthread.h

FORMS    += mainwindow.ui \
    connectiondialog.ui \
    mydialog.ui \
    mydialog2.ui \
    mydialog3.ui \
    mydialog4.ui \
    mydialog5.ui

CONFIG += conan_basic_setup
include(conanbuildinfo.pri)

unix: LIBS += -L/usr/local/lib/ -lprotobuf
