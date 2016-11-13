#!/usr/bin/env bash

# This script builds the client. The resulting binaries are stored in ./client/build
# To start the client, execute ./client/build/GlenniferClient

cd client
mkdir -p build
cd build
qmake -r ../GlenniferClient.pro CONFIG+=debug
make
cd ..
cd ..