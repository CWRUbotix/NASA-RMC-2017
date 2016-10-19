import pika
import serial
import messages_pb2

# Create a global channel variable to hold our channel object in
channel = None

ser_connected = True

try:
    ser = serial.Serial('COM5', 9600, timeout=0)
except:
    print("Warning: serial not connected")
    ser_connected = False

# Step #2
def on_connected(connection):
    """Called when we are fully connected to RabbitMQ"""
    # Open a channel
    connection.channel(on_channel_open)

# Step #3
def on_channel_open(new_channel):
    """Called when our channel has opened"""
    global channel
    channel = new_channel
    channel.queue_declare(queue="test", durable=True, exclusive=False, auto_delete=False, callback=on_queue_declared)

# Step #4
def on_queue_declared(frame):
    """Called when RabbitMQ has told us our Queue has been declared, frame is the response from RabbitMQ"""
    channel.basic_consume(handle_delivery, queue='test', no_ack = True)

# Step #5
def handle_delivery(channel, method, header, body):
    """Called when we receive a message from RabbitMQ"""
    print('recieved')
    print(channel)
    print(method)
    print(body)
    msg = messages_pb2.LocomotionControl()
    msg.ParseFromString(body)
    print(msg)
    if (ser_connected):
        ser.write(msg.locomotionType)
    

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
