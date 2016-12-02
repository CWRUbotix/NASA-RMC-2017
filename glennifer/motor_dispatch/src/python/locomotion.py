import pika
import messages_pb2

# Create global wheel speed and pod
front_left_speed = message_pb2.SpeedControlCommand()
front_right_speed = message_pb2.SpeedControlCommand()
back_left_speed = message_pb2.SpeedControlCommand()
back_right_speed = message_pb2.SpeedControlCommand()

front_left_pod = message_pb2.PositionControlCommand()
front_right_pod = message_pb2.PositionControlCommand()
back_left_pod = message_pb2.PositionControlCommand()
back_right_pod = message_pb2.PositionControlCommand()

configure = message_pb2.LocomotionControlCommandConfigure()

# Declare 4 queue names and corresponding 4 binding keys
straight_queue = 'straight'
turn_queue = 'turn'
strafe_queue = 'strafe'
configure_queue = 'configure'

straight_binding_key = 'subsystem.locomotion.straight'
straight_binding_key = 'subsystem.locomotion.turn'
straight_binding_key = 'subsystem.locomotion.strafe'
straight_binding_key = 'subsystem.locomotion.configure'

exchange_name = ''


channel = None

def on_connected(connection):
	# Called when we establish a connection to RabbitMQ server
	connection.channel(on_channel_open)

def on_channel_open(new_channel):
	# Called when establish a channel
	global channel
	channel = new_channel

  # declare and bind straight queue
	channel.queue_declare(queue=straight_queue, durable=True,
			exclusive=False, auto_delete=False)
	channel.queue_bind(exchange=exchange_name, queue=straight_queue, 
			routing_key=straight_binding_key)
	# declare and bind turn queue
	channel.queue_declare(queue=turn_queue, durable=True,
			exclusive=False, auto_delete=False)
	channel.queue_bind(exchange=exchange_name, queue=turn_queue,
			routing_key=turn_binding_key)
  # declare and bind strafe queue
	channel.queue_declare(queue=strafe_queue, durable=True,
			exclusive=False, auto_delete=False)
	channel.queue_bind(exchange=exchange_name, queue=strafe_queue,
			routing_key=strafe_binding_key)
	# declare and bind strafe queue
	channel.queue_declare(queue=configure_queue, durable=True,
			exclusive=False, auto_delete=False)
	channel.queue_bind(exchange=exchange_name, queue=configure_queue,
			routing_key=configure_binding_key)


	on_queue_bind()

def on_queue_bind(frame):
	# Called when our queue is declared and binded
	channel.basic_consume(handle_straight, queue=straight_queue, no_ack=True)
	channel.basic_consume(handle_turn, queue=turn_queue, no_ack=True)
	channel.basic_consume(handle_strafe, queue=strafe_queue, no_ack=True)
	channel.basic_consume(handle_configure, queue=configure_queue, no_ack=True)

def handle_straight(channel, method, header, body):
	# Called when we receive messages from straight queue
	msg_in = message_pb2.LocomotionControlCommandStraight()
	msg_in.ParseFromString(body)

	# call out error if the configuration is not straight yet
	if (configure.target is not 
			message_pb2.LocomotionCommandConfigure.Configure.STRAIGHT_CONFIG)
		# call out error

	setup_rpm_timeout(front_left_speed, msg_in.rpm, msg_in.timeout)
	setup_rpm_timeout(front_right_speed, msg_in.rpm, msg_in.timeout)
	setup_rpm_timeout(back_left_speed, msg_in.rpm, msg_in.timeout)
	setup_rpm_timeout(back_right_speed, msg_in.rpm, msg_in.timeout)
	

def handle_turn(channel, method, header, body):
	# Called when we receive messages from turn queue
	msg_in = message_pb2.LocomotionControlCommandTurn()
	msg_in.ParseFromString(body)




def handle_strafe(channel, method, header, body):
  # Called when we receive message from strafe queue
	msg_in = message_pb2.LocomotionControlCommandStrafe()
	msg_in.ParseFromString(body)

def handle_configure(channel, method, header, body):
	# Caleed when we receive message from configure queue



def setup_rpm_timeout(wheel, rpm, timeout):
	wheel.rpm = rpm
	wheel.timeout = timeout

def setup_turn_timeout(wheel, rpm, timeout):
	wheel.pod = pod
	wheel.timeout = timeout
