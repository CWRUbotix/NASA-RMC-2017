/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwrubotix.glennifer.robot_state;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

import com.cwrubotix.glennifer.Messages.RpmUpdate;
import com.cwrubotix.glennifer.Messages.LimitUpdate;
import com.cwrubotix.glennifer.Messages.PositionUpdate;
import com.cwrubotix.glennifer.Messages.Fault;
import com.cwrubotix.glennifer.Messages.UnixTime;


import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Michael
 */
public class LocomotionStateModule implements Runnable {
    
    /* Consumer callback class and methods */
    
    private class UpdateConsumer extends DefaultConsumer {
        
        public UpdateConsumer(Channel channel) {
            super(channel);
        }
        
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String routingKey = envelope.getRoutingKey();
            String[] keys = routingKey.split("\\.");
            if (keys.length < 4) {
                return;
            }
            String wheelString = keys[2];
            String sensorString = keys[3];
            LocomotionState.Wheel wheel;
            if (wheelString.equals("front_left")) {
                wheel = LocomotionState.Wheel.FRONT_LEFT;
            } else if (wheelString.equals("front_right")) {
                wheel = LocomotionState.Wheel.FRONT_RIGHT;
            } else if (wheelString.equals("back_left")) {
                wheel = LocomotionState.Wheel.BACK_LEFT;
            } else if (wheelString.equals("back_right")) {
                wheel = LocomotionState.Wheel.BACK_RIGHT;
            } else {
                return;
            }
            if (sensorString.equals("wheel_rpm")) {
                handleWheelRpmUpdate(wheel, body);
            } else if (sensorString.equals("wheel_pod_pos")) {
                handleWheelPodPositionUpdate(wheel, body);
            } else if (sensorString.equals("wheel_pod_limit_extended")) {
                handleWheelPodLimitExtendedUpdate(wheel, body);
            } else if (sensorString.equals("wheel_pod_limit_retracted")) {
                handleWheelPodLimitRetractedUpdate(wheel, body);
            }
        }
    };
    
    private void handleWheelRpmUpdate(LocomotionState.Wheel wheel, byte[] body) throws IOException {
        RpmUpdate message = RpmUpdate.parseFrom(body);
        float rpm = message.getRpm();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateWheelRpm(wheel, rpm, time);
        } catch (RobotFaultException e) {
            LocomotionStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
    
    private void handleWheelPodPositionUpdate(LocomotionState.Wheel wheel, byte[] body) throws IOException {
        PositionUpdate message = PositionUpdate.parseFrom(body);
        float pos = message.getPosition();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateWheelPodPos(wheel, pos, time);
        } catch (RobotFaultException e) {
            LocomotionStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
        
    private void handleWheelPodLimitExtendedUpdate(LocomotionState.Wheel wheel, byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateWheelPodLimitExtended(wheel, pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
            
    private void handleWheelPodLimitRetractedUpdate(LocomotionState.Wheel wheel, byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateWheelPodLimitRetracted(wheel, pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
    
    /* Data Members */
    private LocomotionState state;
    private Channel channel;
    
    public LocomotionStateModule(LocomotionState state) {
        this.state = state;
    }
    
    private UnixTime instantToUnixTime(Instant time) {
        UnixTime.Builder unixTimeBuilder = UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }
    
    private void sendFault(int faultCode, Instant time) throws IOException {
        Fault.Builder faultBuilder = Fault.newBuilder();
        faultBuilder.setFaultCode(faultCode);
        faultBuilder.setTimestamp(instantToUnixTime(time));
        Fault message = faultBuilder.build();
        channel.basicPublish("amq.topic", "fault", null, message.toByteArray());
    }
    
    public void runWithExceptions() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "amq.topic", "sensor.locomotion.#");
        this.channel.basicConsume(queueName, true, new UpdateConsumer(channel));
    }
    
    @Override
    public void run() {
        try {
            runWithExceptions();
        } catch (Exception e) {
            try {
                sendFault(999, Instant.now());
            } catch (Exception e2) { }
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        LocomotionState state = new LocomotionState();
        LocomotionStateModule module = new LocomotionStateModule(state);
        try {
            module.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
