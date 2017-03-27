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
import com.cwrubotix.glennifer.Messages.DisplacementUpdate;
import com.cwrubotix.glennifer.Messages.Fault;
import com.cwrubotix.glennifer.Messages.UnixTime;


import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Michael
 */
public class ExcavationStateModule {
    
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

            if (sensorString.equals("conveyor_rpm")) {
                handleConveyorRpmUpdate(body);
            } else if (sensorString.equals("conveyor_translation_displacement")) {
                handleConveyorTranslationDisplacementUpdate(body);
			}  else if (sensorString.equals("arm_pos")) {
                handleArmPosUpdate(body);
            } else if (sensorString.equals("arm_limit_extended")) {
                handleArmLimitExtendedUpdate(body);
            } else if (sensorString.equals("arm_limit_retracted")) {
                handleArmLimitRetractedUpdate(body);
            } else if (sensorString.equals("conveyor_translation_limit_extended")) {
                handleConveyorTranslationLimitExtendedUpdate(body);
            } else if (sensorString.equals("conveyor_translation_limit_retracted")) {
                handleConveyorTranslationLimitRetractedUpdate(body);
            }
        }
    }
    
    private void handleConveyorRpmUpdate(byte[] body) throws IOException {
        RpmUpdate message = RpmUpdate.parseFrom(body);
        float rpm = message.getRpm();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateConveyorRpm(rpm, time);
        } catch (RobotFaultException e) {
            ExcavationStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
	
	private void handleConveyorTranslationDisplacementUpdate(byte[] body) throws IOException {
        DisplacementUpdate message = DisplacementUpdate.parseFrom(body);
        float displacement = message.getDisplacement();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateTranslationDisplacement(displacement, time);
        } catch (RobotFaultException e) {
            ExcavationStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
    
    private void handleArmPosUpdate(byte[] body) throws IOException {
        PositionUpdate message = PositionUpdate.parseFrom(body);
        float pos = message.getPosition();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateArmPos(pos, time);
        } catch (RobotFaultException e) {
            ExcavationStateModule.this.sendFault(e.getFaultCode(), time);
        }
    }
        
    private void handleArmLimitExtendedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateArmLimitExtended(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
            
    private void handleArmLimitRetractedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateArmLimitRetracted(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
	
	private void handleConveyorTranslationLimitExtendedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateTranslationLimitExtended(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
            
    private void handleConveyorTranslationLimitRetractedUpdate(byte[] body) throws IOException {
        LimitUpdate message = LimitUpdate.parseFrom(body);
        boolean pressed = message.getPressed();
        Instant time = Instant.ofEpochSecond(message.getTimestamp().getTimeInt(), (long)(message.getTimestamp().getTimeFrac() * 1000000000L));
        try {
            state.updateTranslationLimitRetracted(pressed, time);
        } catch (RobotFaultException e) {
            sendFault(e.getFaultCode(), time);
        }
    }
    
    /* Data Members */
    private ExcavationState state;
    private String exchangeName;
    private Connection connection;
    public Channel channel;
    
    public ExcavationStateModule(ExcavationState state) {
        this(state, "amq.topic");
    }

    public ExcavationStateModule(ExcavationState state, String exchangeName) {
        this.state = state;
        this.exchangeName = exchangeName;
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
        channel.queueBind(queueName, exchangeName, "sensor.excavation.#");
        this.channel.basicConsume(queueName, true, new UpdateConsumer(channel));
    }

    public void start() {
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

    public void stop() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
    
    public static void main(String[] args) {
        ExcavationState state = new ExcavationState();
        ExcavationStateModule module = new ExcavationStateModule(state);
        module.start();
    }
}
