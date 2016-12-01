# this code control the robot locomotion
# it receives messages from middle level commands
# and send out ouput to the motor layer

import pika
import messages_pb2


# create a connection 
connection = pika.BlockingConnection(pika.ConnectionParameters(
			host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='
