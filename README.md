This is the entire codebase for Case Western Reserve University's 2017 NASA Robotic Mining Challenge robot. It is currently made up a minimal skeleton of test scripts written in python.

Running this code requires Python 3 with the the Pika and PySerial libraries. The robot must also be running a standalone AMQP server such as RabbitMQ.

Each python script represents a node in a network of message consumers and producers, communicating over AMQP using the pika library.

The client program takes input from the standard input. A line of input consists of a letter (wasd for forward, left, backwards, and right) and a number. The robot should move in the given direction for an amount of time equal to the given number times 50 milliseconds.

The comms program consumes messages from the client program, and uses them to control Sabretooth motor controllers over serial through an Arduino connected by USB.
