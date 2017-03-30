import cv2
import pika
import numpy as np
import yaml

amqp_server_addr = input('Robot address: ')

with open('config/connection.yml') as connection_config_file:
    connection_config = yaml.safe_load(connection_config_file)
    if not amqp_server_addr:
        amqp_server_addr = connection_config['server-addr']
    amqp_server_user = connection_config['server-user']
    amqp_server_pass = connection_config['server-pass']
    amqp_exchange_name = connection_config['exchange-name']

credentials = pika.PlainCredentials(amqp_server_user, amqp_server_pass)
parameters = pika.ConnectionParameters(amqp_server_addr, 5672,  '/', credentials=credentials)
connection = pika.BlockingConnection(parameters)
channel = connection.channel()
channel.queue_declare(queue = 'cam1')
channel.queue_declare(queue = 'cam2')
channel.queue_declare(queue = 'cam3')
channel.queue_declare(queue = 'cam4')
channel.queue_declare(queue = 'cam5')

#Camera Windowa
cv2.namedWindow('Cam1')
cv2.namedWindow('Cam2')
cv2.namedWindow('Cam3')
cv2.namedWindow('Cam4')
cv2.namedWindow('Cam5')
#cv2.startWindowThread()
def callback1(ch,  method,  properties, body):
    if (body is None):
        return None
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        cv2.imshow('Cam1', img)
        cv2.waitKey(1)
        
def callback2(ch,  method,  properties, body):
    if (body is None):
        return None
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        cv2.imshow('Cam2', img)
        cv2.waitKey(1)
        
def callback3(ch,  method,  properties, body):
    if (body is None):
        return None
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        cv2.imshow('Cam3', img)
        cv2.waitKey(1)      
      
def callback4(ch,  method,  properties, body):
    if (body is None):
        return None
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        cv2.imshow('Cam4', img)
        cv2.waitKey(1)
        
def callback5(ch,  method,  properties, body):
    if (body is None):
        return None
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        cv2.imshow('Cam5', img)
        cv2.waitKey(1)
    
#Consume
channel.basic_consume(callback1,  queue = 'cam1',  no_ack = True)
channel.basic_consume(callback2,  queue = 'cam2',  no_ack = True)
channel.basic_consume(callback3,  queue = 'cam3',  no_ack = True)
channel.basic_consume(callback4,  queue = 'cam4',  no_ack = True)
channel.basic_consume(callback5,  queue = 'cam5',  no_ack = True)
channel.start_consuming()
