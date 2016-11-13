#!/usr/bin/env bash

# This script sets up everything you need to build and run Glennifer on Ubuntu

# Install C++ compiler and tools
sudo apt-get -q -y install autoconf automake make g++ 

# Install python 3 and tools
sudo apt-get -q -y install python3 python3-pip

# Install the protobuf compiler, protoc
sudo apt-get -q -y install protobuf-compiler

# Install the protobuf library for python
sudo pip3 install protobuf

# Install pika, the AMQP library for python
sudo pip3 install pika

# Install the Java 8 Development Kit and Maven
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get -q -y install openjdk-8-jdk maven

# Install and set up the RabbitMQ server
echo 'deb http://www.rabbitmq.com/debian/ testing main' |
        sudo tee /etc/apt/sources.list.d/rabbitmq.list
wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc |
        sudo apt-key add -
sudo apt-get update
sudo apt-get install rabbitmq-server
sudo invoke-rc.d rabbitmq-server start