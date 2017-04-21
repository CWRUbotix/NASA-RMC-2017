import cv2
import numpy as np
import pika
import sys
import time

#Change demo to guest or local host, or another user, password tuple
#Change ip address to the one for the server running the receive code
#172.20.36.235
#credentials = pika.PlainCredentials('guest', 'guest')
#parameters = pika.ConnectionParameters('localhost',  5672, '/', credentials=credentials)
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.exchange_declare(exchange='amq.topic',  type = 'topic', durable = True)

#Sends a message
def send1(msg):
    channel.basic_publish(exchange = 'amq.topic',  routing_key = 'camera.one',  body = msg)
    
def send2(msg):
    channel.basic_publish(exchange = 'amq.topic',  routing_key = 'camera.two',  body = msg)
    
def send3(msg):
    channel.basic_publish(exchange = 'amq.topic',  routing_key = 'camera.three',  body = msg)
    
def send4(msg):
    channel.basic_publish(exchange = 'amq.topic',  routing_key = 'camera.four',  body = msg)
    
def send5(msg):
    channel.basic_publish(exchange = 'amq.topic',  routing_key = 'camera.five',  body = msg)
    
#Camera Capture
#Change Capture Device Index to correspinding index
#for corresponding cameracam1 = cv2.VideoCapture(0)#
cam1.set(3, 720)
cam1.set(4, 576)
cam2 = cv2.VideoCapture(1)
cam2.set(3, 720)
cam2.set(4, 576)
cam3 = cv2.VideoCapture(2)
cam3.set(3, 720)
cam3.set(4, 576)
cam4 = cv2.VideoCapture(3)
cam4.set(3, 720)
cam4.set(4, 576)
cam5 = cv2.VideoCapture(4)
cam5.set(3, 720)
cam5.set(4, 576)
def getCam1():
    if (cam1.isOpened() is True):
        ret,  frame1 = cam1.read()
        imr1 = cv2.cvtColor(frame1,  cv2.COLOR_BGR2RGB)
	print (str(imr1.shape))
        img1 = str(bytearray(cv2.imencode('.jpg',  imr1)[1].flatten().tolist()))
        send1(img1)
    else:
         c= 1
        
def getCam2():
    if (cam2.isOpened() is True):
        ret,  frame2 = cam2.read()
        imr2 = cv2.cvtColor(frame2,  cv2.COLOR_BGR2RGB)
	print (str(imr2.shape))
        img2 = str(bytearray(cv2.imencode('.jpg',  imr2)[1].flatten().tolist()))
        send2(img2)
    else:
        c= 1
        
def getCam3():
    if (cam3.isOpened() is True):
        ret,  frame3 = cam3.read()
        imr3 = cv2.cvtColor(frame3,  cv2.COLOR_BGR2RGB)
	print (str(imr3.shape))
        img3 = str(bytearray(cv2.imencode('.jpg',  imr3)[1].flatten().tolist()))
        send3(img3)
    else:
         c= 1
        
def getCam4():
    if (cam4.isOpened() is True):
        ret,  frame4 = cam4.read()
        imr4 = cv2.cvtColor(frame4,  cv2.COLOR_BGR2RGB)
	print (str(imr4.shape))
        img4 = str(bytearray(cv2.imencode('.jpg',  imr4)[1].flatten().tolist()))
        send4(img4)
    else:
         c= 1
        
def getCam5():
    if (cam5.isOpened() is True):
        ret,  frame5 = cam5.read()
        imr5 = cv2.cvtColor(frame5,  cv2.COLOR_BGR2RGB)
	print (str(imr5.shape))
        img5 = str(bytearray(cv2.imencode('.jpg',  imr5)[1].flatten().tolist()))
	#print (str(imr5.shape))
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
