import pika
import messages_pb2

# Declare 2 queue names and corresponding 2 binding keys
dump_queue = 'subsyscommand.deposition.position_dump'
conveyor_queue = 'subsyscommand.deposition.move_conveyor'

# Declare exchange from subsys
exchange_name = 'amq.topic'
channel = None

def on_connected(connection):
    # Called when we establish a connection to RabbitMQ server
    connection.channel(on_channel_open)
	
def on_channel_open(new_channel):
    # Called when establish a channel
    global channel
    channel = new_channel
    
  # declare and bind dump queue
    channel.queue_declare(on_dump_queue_declare, dump_queue, durable=True,
            exclusive=False, auto_delete=False)
  # declare and bind conveyor queue
    channel.queue_declare(on_conveyor_queue_declare, conveyor_queue, durable=True,
            exclusive=False, auto_delete=False)
			
			
def on_dump_queue_declare(frame):
    # Called when our queue is declared
    channel.queue_bind(callback=on_dump_queue_bind, queue=dump_queue, exchange=exchange_name, routing_key=dump_queue)
def on_conveyor_queue_declare(frame):
    # Called when our queue is declared
    channel.queue_bind(callback=on_conveyor_queue_bind, queue=conveyor_queue, exchange=exchange_name, routing_key=conveyor_queue)
	
def on_dump_queue_bind(frame):
    # Called when our queue is binded
    channel.basic_consume(handle_dump_position, queue=dump_queue, no_ack=True)
def on_conveyor_queue_bind(frame):
    # Called when our queue is binded
    channel.basic_consume(handle_conveyor_speed, queue=conveyor_queue, no_ack=True)

	
def handle_dump_position(channel, method, header, body):
    # Called when we receive messages from dump queue
    msg_in = messages_pb2.LocomotionControlCommandStraight()
    msg_in.ParseFromString(body)
    
    # set the dump position, power, and timeout
    publish_dump_position(msg_in.position, msg_in.power, msg_in.timeout)

def handle_conveyor_speed(channel, method, header, body):
    # Called when we receive messages from conveyor queue
    msg_in = messages_pb2.LocomotionControlCommandStraight()
    msg_in.ParseFromString(body)
    
    # set the arm position, power, and timeout
    publish_conveyor_speed(msg_in.speed, msg_in.timeout)	
    
    
def publish_dump_position(position, power, timeout):
    msg = messages_pb2.DepositionControlPositionDump
    msg.position = position
	msg.power = power
    msg.timeout = timeout
    topic = 'motorcontrol.deposition.dump_pos'
    channel.basic_publish(exchange=exchange_name,
            routing_key=topic,
            body=msg.SerializeToString())

def publish_conveyor_speed(speed, timeout):
    msg = messages_pb2.DepositionControlMoveConveyor
	msg.speed = speed
    msg.timeout = timeout
    topic = 'motorcontrol.deposition.conveyor_rpm';
    channel.basic_publish(exchange=exchange_name,
            routing_key=topic,
            body=msg.SerializeToString())
			
			
# Step #1: Connect to RabbitMQ using the default parameters
parameters = pika.connection.URLParameters('amqp://guest:guest@localhost:5672/%2F')
connection = pika.SelectConnection(parameters=parameters,
                                   on_open_callback=on_connected)
try:
    # Loop so we can communicate with RabbitMQ
    connection.ioloop.start()
except KeyboardInterrupt:
    # Gracefully close the connection
    connection.close()
    # Loop until we're fully closed, will stop on its own
    connection.ioloop.start()