import pika

params = pika.ConnectionParameters()
connection = pika.BlockingConnection(params) # Connect to ButtAMQP
channel = connection.channel() # start a channel
channel.queue_declare(queue='pdfprocess') # Declare a queue
# send a message

while True:
        cmd = input().split()
        body = cmd[0] * int(cmd[1])
        print(body)
        channel.basic_publish('',
                            'test',
                            body,
                            pika.BasicProperties(content_type='text/plain',
                                                 delivery_mode=1))

connection.close()
