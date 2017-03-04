import pika
import messages_pb2

# Declare 3 queue names and corresponding 3 binding keys
arm_queue = 'subsyscommand.excavation.position_arm'
translation_queue = 'subsyscommand.excavation.position_translation'
belt_queue = 'subsyscommand.excavation.move_bucket_conveyor'

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
    
  # declare and bind arm queue
    channel.queue_declare(on_arm_queue_declare, arm_queue, durable=True, exclusive=False, auto_delete=False)
  # declare and bind translation queue
    channel.queue_declare(on_translation_queue_declare, translation_queue, durable=True, exclusive=False, auto_delete=False)
  # declare and bind belt queue
    channel.queue_declare(on_belt_queue_declare, belt_queue, durable=True, exclusive=False, auto_delete=False)
			
def on_arm_queue_declare(frame):
    # Called when our queue is declared
    channel.queue_bind(callback=on_arm_queue_bind, queue=arm_queue, exchange=exchange_name, routing_key=arm_queue)
def on_translation_queue_declare(frame):
    # Called when our queue is declared
    channel.queue_bind(callback=on_translation_queue_bind, queue=translation_queue, exchange=exchange_name, routing_key=translation_queue)
def on_belt_queue_declare(frame):
    # Called when our queue is declared
    channel.queue_bind(callback=on_belt_queue_bind, queue=belt_queue, exchange=exchange_name, routing_key=belt_queue)

def on_arm_queue_bind(frame):
    # Called when our queue is binded
    channel.basic_consume(handle_arm_position, queue=arm_queue, no_ack=True)
def on_translation_queue_bind(frame):
    # Called when our queue is binded
    channel.basic_consume(handle_translation_position, queue=translation_queue, no_ack=True)
def on_belt_queue_bind(frame):
    # Called when our queue is binded
    channel.basic_consume(handle_belt_speed, queue=belt_queue, no_ack=True)

def handle_arm_position(channel, method, header, body):
    # Called when we receive messages from arm queue
    msg_in = messages_pb2.LocomotionControlCommandStraight()
    msg_in.ParseFromString(body)
    
    # set the arm position, power, and timeout
    publish_arm_position(msg_in.position, msg_in.power, msg_in.timeout)

def handle_translation_position(channel, method, header, body):
    # Called when we receive messages from translation queue
    msg_in = messages_pb2.LocomotionControlCommandStraight()
    msg_in.ParseFromString(body)
    
    # set the translation position, power, and timeout
    publish_translation_position(msg_in.position, msg_in.power, msg_in.timeout)

def handle_belt_speed(channel, method, header, body):
    # Called when we receive messages from belt queue
    msg_in = messages_pb2.LocomotionControlCommandStraight()
    msg_in.ParseFromString(body)
    
    # set the arm position, power, and timeout
    publish_belt_speed(msg_in.speed, msg_in.timeout)	
    
def publish_arm_position(position, power, timeout):
	msg = messages_pb2.ExcavationControlCommandPositionArm()
	msg.position = position
	msg.power = power
	msg.timeout = timeout
	topic = 'motorcontrol.excavation.arm_pos'
	channel.basic_publish(exchange=exchange_name, routing_key=topic, body=msg.SerializeToString())
    
def publish_translation_position(position, power, timeout):
	msg = messages_pb2.ExcavationControlCommandPositionTranslation
	msg.position = position
	msg.power = power
	msg.timeout = timeout
	topic = 'motorcontrol.excavation.conveyor_translation_displacement'
	channel.basic_publish(exchange=exchange_name, routing_key=topic, body=msg.SerializeToString())

def publish_belt_speed(speed, timeout):
	msg = messages_pb2.ExcavationControlCommandMoveBucketConveyor
	msg.speed = speed
	msg.timeout = timeout
	topic = 'motorcontrol.excavation.bucket_conveyor_rpm';
	channel.basic_publish(exchange=exchange_name, routing_key=topic, body=msg.SerializeToString())
			
			
# Step #1: Connect to RabbitMQ using the default parameters
parameters = pika.connection.URLParameters('amqp://guest:guest@localhost:5672/%2F')
connection = pika.SelectConnection(parameters=parameters, on_open_callback=on_connected)
try:
    # Loop so we can communicate with RabbitMQ
    connection.ioloop.start()
except KeyboardInterrupt:
    # Gracefully close the connection
    connection.close()
    # Loop until we're fully closed, will stop on its own
    connection.ioloop.start()