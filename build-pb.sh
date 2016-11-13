#!/usr/bin/env bash

# This script builds all the protobuf classes for java, cpp, and python

mkdir -p pb
protoc --cpp_out=pb/. --java_out=pb/. --python_out=pb/. --python_out=glennifer/. messages.proto
