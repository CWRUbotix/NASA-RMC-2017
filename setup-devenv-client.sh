#!/usr/bin/env bash

# This script sets up everything you need to build and run the client on Ubuntu

# Install C++ compiler, tools, and debugger
sudo apt-get -q -y install make cmake g++ gdb

# Install Qt5
sudo apt-get -q -y remove qt4-qmake
sudo apt-get -q -y install qt5-default qtdeclarative5-dev

# Install protobuf library for C/C++
sudo apt-get -q -y install libprotobuf-dev

# Build rabbitmq-c locally
git submodule init third-party/rabbitmq-c
git submodule update third-party/rabbitmq-c
cd ./third-party/rabbitmq-c/
mkdir build
cd build
cmake .. -DBUILD_SHARED_LIBS=OFF -DBUILD_STATIC_LIBS=ON -DENABLE_SSL_SUPPORT=OFF
cmake --build . --config Release
cd ..
cd ../..