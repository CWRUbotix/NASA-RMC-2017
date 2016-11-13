#!/usr/bin/env bash

# This script builds all the protobuf classes for java, cpp, and python

protoc --cpp_out=. --java_out=. --python_out=. messages.proto
