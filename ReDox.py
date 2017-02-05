import cv2
import pika
import numpy as np
credentials = pika.PlainCredentials('guest', 'guest')
parameters = pika.ConnectionParameters('localhost', 5672,  '/', credentials=credentials)
connection = pika.BlockingConnection(parameters)
channel = connection.channel()
channel.queue_declare(queue = 'q03')

#cv2.startWindowThread()
cv2.namedWindow('Picture')
#Main Loop
while(True):
    method_frame,  header_frame,  body = channel.basic_get(queue = 'q03')
    if (body is None):
        print("No Frame")
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.IMREAD_COLOR)
        im = cv2.cvtColor(img,  cv2.COLOR_BGR2GRAY)
        ims = cv2.cvtColor(img,  cv2.COLOR_BGR2RGB)
        cv2.imshow('Picture', img)
    cv2.waitKey(10)
