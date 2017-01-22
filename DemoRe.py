import cv2
import pika
import numpy as np
#host_in = raw_input("Plese Enter the Host: '172.20.93.234'")
#'remote-host', 5672,  '/', 15672 
credentials = pika.PlainCredentials('demo', 'demo')
parameters = pika.ConnectionParameters('172.20.93.234', 5672,  '/', credentials=credentials)
connection = pika.BlockingConnection(parameters)
channel = connection.channel()
channel.queue_declare(queue = 'dim')

def callback(ch,  method,  properties, body):
    #connection.sleep()
    print(type(body)) 
    #final_name = ''.join([name, str(count), ".png"])
    nparr = np.fromstring(body,  np.uint8)
    img = cv2.imdecode(nparr,  cv2.CV_LOAD_IMAGE_COLOR)
    im = cv2.cvtColor(img,  cv2.COLOR_BGR2RGB)
    #cv2.imwrite(final_name,  im)
    cv2.imshow('Its Picture Time', im)
    
#consume
#channel.basic_consume(callback,  queue = 'cam',  no_ack = True)
#channel.start_consuming()

#Count
d = 0

#cv2.startWindowThread()
cv2.namedWindow('Picture')
#Main Loop
while(True):
    method_frame,  header_frame,  body = channel.basic_get(queue = 'dim')
    if (body is None):
        print("No Frame")
    else: 
        nparr = np.fromstring(body,  np.uint8)
        img = cv2.imdecode(nparr,  cv2.CV_LOAD_IMAGE_COLOR)
        im = cv2.cvtColor(img,  cv2.COLOR_BGR2GRAY)
        #cv2.imwrite("Received"+str(d)+".png",  im)
        ims = cv2.cvtColor(img,  cv2.COLOR_BGR2RGB)
        #cv2.imwrite("ReceivedGray"+str(d)+".png",  ims)
        d += 1
        #ret,  im2 = ims
        #cv2.startWindowThread()
        #cv2.namedWindow('Picture')
        cv2.imshow('Picture', img)
        cv2.waitKey(10)
