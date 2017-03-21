import cv2
import numpy as np
import pika
import sys
import time
import yaml

with open('config/connection.yml') as connection_config_file:
    connection_config = yaml.safe_load(connection_config_file)
    amqp_server_addr = connection_config['server-addr']
    amqp_server_user = connection_config['server-user']
    amqp_server_pass = connection_config['server-pass']
    amqp_exchange_name = connection_config['exchange-name']

credentials = pika.PlainCredentials(amqp_server_user, amqp_server_pass)
parameters = pika.ConnectionParameters(amqp_server_addr,  5672, '/', credentials=credentials)

#Sends a message
def send1(msg):
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue = 'cam1')
    channel.basic_publish(exchange = '',  routing_key = 'cam1',  body = msg)
    
def send2(msg):
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue = 'cam2')
    channel.basic_publish(exchange = '',  routing_key = 'cam2',  body = msg)
    
def send3(msg):
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue = 'cam3')
    channel.basic_publish(exchange = '',  routing_key = 'cam3',  body = msg)
    
def send4(msg):
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue = 'cam4')
    channel.basic_publish(exchange = '',  routing_key = 'cam4',  body = msg)
    
def send5(msg):
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue = 'cam5')
    channel.basic_publish(exchange = '',  routing_key = 'cam5',  body = msg)
    
#Camera Capture
#Change Capture Device Index to correspinding index
#for corresponding camera
# cam1.set(3,  480)
#cam1.set(4, 320)
cam1 = cv2.VideoCapture(0)
cam1.set(3, 240)
cam1.set(4, 160)
cam2 = cv2.VideoCapture(1)
cam2.set(3, 240)
cam2.set(4, 160)
cam3 = cv2.VideoCapture(2)
cam3.set(3, 240)
cam3.set(4, 160)
cam4 = cv2.VideoCapture(3)
cam4.set(3, 240)
cam4.set(4, 160)
cam5 = cv2.VideoCapture(4)
cam5.set(3, 240)
cam5.set(4, 160)
def getCam1():
    if (cam1.isOpened() is True):
        ret,  frame1 = cam1.read()
        imr1 = cv2.cvtColor(frame1,  cv2.COLOR_BGR2RGB)
        img1 = cv2.imencode('.ppm',  imr1)[1].tostring()
        send1(img1)
    else:
         c= 1
        
def getCam2():
    if (cam2.isOpened() is True):
        ret,  frame2 = cam2.read()
        imr2 = cv2.cvtColor(frame2,  cv2.COLOR_BGR2RGB)
        img2 = cv2.imencode('.ppm',  imr2)[1].tostring()
        send2(img2)
    else:
        c= 1
        
def getCam3():
    if (cam3.isOpened() is True):
        ret,  frame3 = cam3.read()
        imr3 = cv2.cvtColor(frame3,  cv2.COLOR_BGR2RGB)
        img3 = cv2.imencode('.ppm',  imr3)[1].tostring()
        send3(img3)
    else:
         c= 1
        
def getCam4():
    if (cam4.isOpened() is True):
        ret,  frame4 = cam4.read()
        imr4 = cv2.cvtColor(frame4,  cv2.COLOR_BGR2RGB)
        img4 = cv2.imencode('.ppm',  imr4)[1].tostring()
        send4(img4)
    else:
         c= 1
        
def getCam5():
    if (cam5.isOpened() is True):
        ret,  frame5 = cam5.read()
        imr5 = cv2.cvtColor(frame5,  cv2.COLOR_BGR2RGB)
        img5 = cv2.imencode('.ppm',  imr5)[1].tostring()
        send5(img5)
    else:
         c= 1

#Main Loop
while(True):
    #Camera One Update
    getCam1()
    #Camera Two Update
    getCam2()
    #Camera Three Update
    getCam3()
    #Camera Four Update
    getCam4()
    #Camera Five Update
    getCam5()
