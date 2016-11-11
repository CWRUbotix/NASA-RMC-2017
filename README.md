[![Stories in Ready](https://badge.waffle.io/cwruRobotics/NASA-RMC-2017.png?label=ready&title=Ready)](https://waffle.io/cwruRobotics/NASA-RMC-2017)

This is the entire codebase for Case Western Reserve University's 2017 NASA Robotic Mining Challenge robot. It is currently made up a minimal skeleton of test scripts written in python.

## Dependencies ##

### For building and running the robot ###

* A modern linux operating system, such as Ubuntu: https://www.ubuntu.com/download
* Python 3: https://www.python.org/downloads/
* pika, an AMQP library for python: https://pika.readthedocs.io/en/0.10.0/
  * `pip install pika`
* pyserial, a serial communication library for python: https://pythonhosted.org/pyserial/
  * `pip install pyserial`
* An arbritrary AMQP server, such as RabbitMQ: https://www.rabbitmq.com/download.html
* The Java Runtime: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
* The RabbitMQ client for Java: https://www.rabbitmq.com/java-client.html
* A C/C++ compiler, such as gcc
* rabbitmq-c, the RabbitMQ client for c: https://github.com/alanxz/rabbitmq-c
  * Included as a git submodule
* Google protocol buffers: https://developers.google.com/protocol-buffers/
  * Included as a git submodule

### For building and running the client ###

* A C/C++ compiler, such as gcc or msvc
* The Qt GUI framework: https://www.qt.io/download-open-source/
* rabbitmq-c, the RabbitMQ client for c: https://github.com/alanxz/rabbitmq-c
  * Included as a git submodule
* Google protocol buffers: https://developers.google.com/protocol-buffers/
  * Included as a git submodule

## Installation and running ##

(to be continued)

## The example program's description ##

Each python script represents a node in a network of message consumers and producers, communicating over AMQP using the pika library.

The client program takes input from the standard input. A line of input consists of a letter (wasd for forward, left, backwards, and right) and a number. The robot should move in the given direction for an amount of time equal to the given number times 50 milliseconds.

The comms program consumes messages from the client program, and uses them to control Sabretooth motor controllers over serial through an Arduino connected by USB.
