[![Stories in Ready](https://badge.waffle.io/cwruRobotics/NASA-RMC-2017.png?label=ready&title=Ready)](https://waffle.io/cwruRobotics/NASA-RMC-2017)

This is the entire codebase for Case Western Reserve University's 2017 NASA Robotic Mining Challenge robot, named Glennifer. It includes two products: the set of programs that run on Glennifer, and the human control client.

## Compatability ##

The robot must be built and run on Ubuntu. The client may be built and run on either linux or windows.

## Dependencies and environment setup ##

All setup of the development environment for both the robot and the client is done with setup scripts.

### For Glennifer ###

To setup the dev environment for the robot on Ubuntu, run ./setup-devenv-glennifer.sh

If this script fails, inspect the script and try to do what failed manually. If there is a problem with the script that should be fixed, please fix it!

For reference, these are all the dependencies required to build and run Glennifer:

* A modern linux operating system, such as Ubuntu: https://www.ubuntu.com/download
* Python 3: https://www.python.org/downloads/
* pika, an AMQP library for python: https://pika.readthedocs.io/en/0.10.0/
  * `pip install pika`
* pyserial, a serial communication library for python: https://pythonhosted.org/pyserial/
  * `pip install pyserial`
* An arbritrary AMQP server, such as RabbitMQ: https://www.rabbitmq.com/download.html
* The Java Development Kit, version 8 or higher: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* The RabbitMQ client for Java: https://www.rabbitmq.com/java-client.html
* A C/C++ compiler, such as gcc
* rabbitmq-c, the RabbitMQ client for c: https://github.com/alanxz/rabbitmq-c
  * Included as a git submodule
* Google protocol buffers: https://developers.google.com/protocol-buffers/

### For the client on Linux ###

To setup the dev environment for the client on Ubuntu, run ./setup-devenv-client.sh

To setup the dev environment for the client on some other version of linux, inspect the contents of ./setup-devenv-client.sh and change all the apt-get statements to use your distro's package manager with the proper package names. Then, add your changed script to the repo (and this README file) for others to use!

### For the client on Windows ###

To setup the dev environment for the client on Windows, use the following procedure:

1. Ensure you are running a 64-bit windows operating system
2. Install Visual Studio 2015
3. Install CMake, making sure it is in your system PATH
4. Install Qt
5. Install git for windows, making sure it is in your system PATH
6. Install Conan
7. Run ./setup-devenv-client.ps1

For reference, these are all the dependencies required to build and run Glennifer:

* A C/C++ compiler, such as gcc or msvc
* CMake
* The Qt GUI framework: https://www.qt.io/download-open-source/
* rabbitmq-c, the RabbitMQ client for c: https://github.com/alanxz/rabbitmq-c
  * Included as a git submodule
* Google protocol buffers: https://developers.google.com/protocol-buffers/

## Building and running ##

### For Glennifer ###

To build Glennifer on Ubuntu, run ./build-glennifer.sh
To run Glennifer, run ./build-glennifer/start

### For the client ###

To build the client on Ubuntu, either use the Qt Creator GUI or run ./build-client.sh

To build the client on Windows, either use the Qt Creator GUI or run ./build-client.ps1

To start the client, run ./client/build/GlenniferClient

## The example program's description ##

Each python script represents a node in a network of message consumers and producers, communicating over AMQP using the pika library.

The client program takes input from the standard input. A line of input consists of a letter (wasd for forward, left, backwards, and right) and a number. The robot should move in the given direction for an amount of time equal to the given number times 50 milliseconds.

The comms program consumes messages from the client program, and uses them to control Sabretooth motor controllers over serial through an Arduino connected by USB.
