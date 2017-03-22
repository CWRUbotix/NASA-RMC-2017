package com.cwrubotix.glennifer.robot_state;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

import com.cwrubotix.glennifer.Messages.LimitUpdate;
import com.cwrubotix.glennifer.Messages.PositionUpdate;
import com.cwrubotix.glennifer.Messages.LoadUpdate;
import com.cwrubotix.glennifer.Messages.Fault;
import com.cwrubotix.glennifer.Messages.UnixTime;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Michael
 */
public class DepositionStateModule implements Runnable {
    
    /* Consumer callback class and methods */
    
    private class UpdateConsumer extends DefaultConsumer {
        
        public UpdateConsumer(Channel channel) {
            super(channel);
        }
        
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String routingKey = envelope.getRoutingKey();
            String[] keys = routingKey.split("\\.");
            if (keys.length < 3) {
                return;
            }
            String sensorString = keys[2];
            String loadCellString = keys[3];
            if (sensorString.equals("dump_load")) {
                DepositionState.LoadCell loadcell;
                if(loadCellString.equals("front_left")){
                    loadcell = DepositionState.LoadCell.FRONT_LEFT;
                } else if (loadCellString.equals("front_right")){
                    loadcell = DepositionState.LoadCell.FRONT_RIGHT;
                } else if (loadCellString.equals("back_left")){
                    loadcell = DepositionState.LoadCell.BACK_LEFT;
                } else if (loadCellString.equals("back_right")) {
                    loadcell = DepositionState.LoadCell.BACK_RIGHT;
                } else {
                    return;
                }
                handleDumpLoadUpdate(loadcell, body);
			}  else if (sensorString.equals("arm_pos")) {
                handleDumpPosUpdate(body);
            } else if (sensorString.equals("dump_limit_extended")) {
                handleDumpLimitExtendedUpdate(body);
            } else if (sensorString.equals("dump_limit_retracted")) {
                handleDumpLimitRetractedUpdate(body);
            }
        }
    };
    
    private void handleDumpLoadUpdate(DepositionState.LoadCell cell, byte[] body) throws IOException {
        LoadUpdate message = LoadUpdate.parseFrom(body);
        float load = message.getLoad();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateDumpLoad(cell, load, time);
        } catch (RobotFaultException e) {
            DepositionStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
    
    private void handleDumpPosUpdate(byte[] body) throws IOException {
        PositionUpdate message = PositionUpdate.parseFrom(body);
        float pos = message.getPosition();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateDumpPos(pos, time);
        } catch (RobotFaultException e) {
            DepositionStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
        
    private void handleDumpLimitExtendedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateDumpLimitExtended(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
            
    private void handleDumpLimitRetractedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateDumpLimitRetracted(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
	
    /* Data Members */
    private DepositionState state;
    private String exchangeName;
    private Connection connection;
    private Channel channel;
    private CountDownLatch ready;
    
    public DepositionStateModule(DepositionState state) {
        this(state, "amq.topic");
    }

    public DepositionStateModule(DepositionState state, String exchangeName) {
        this.state = state;
        this.exchangeName = exchangeName;
        this.ready = new CountDownLatch(1);
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
        channel.basicPublish(exchangeName, "fault", null, message.toByteArray());
    }
    
    public void runWithExceptions() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        this.channel = connection.createChannel();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "sensor.deposition.#");
        this.channel.basicConsume(queueName, true, new UpdateConsumer(channel));
        ready.countDown();
    }

    public void awaitReady() throws InterruptedException {
        ready.await();
    }

    public void awaitReady(long val, TimeUnit timeUnit) throws InterruptedException {
        ready.await(val, timeUnit);
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

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public static void main(String[] args) {
        DepositionState state = new DepositionState();
        DepositionStateModule module = new DepositionStateModule(state);
        try {
            module.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
