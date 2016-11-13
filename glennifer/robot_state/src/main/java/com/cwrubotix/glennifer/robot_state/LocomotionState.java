/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwrubotix.glennifer.robot_state;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

import Messages.RpmUpdate;
import Messages.LimitUpdate;
import Messages.PositionUpdate;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Michael
 */
public class LocomotionState {
    
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            System.out.println("Failed to connect");
            return;
        } catch (TimeoutException e) {
            System.out.println("Connection timed out");
            return;
        }
        Channel channel;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            System.out.println("Failed to create channel");
            return;
        }
        try {
            channel.queueDeclare("input_queue", false, false, false, null);
        } catch (IOException e) {
            System.out.println("Failed to declare queue");
            return;
        }
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume("input_queue", true, consumer);
    }
    
}
