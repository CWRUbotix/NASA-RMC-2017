package com.cwrubotix.glennifer.robot_state;

import com.cwrubotix.glennifer.Messages;
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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Michael
 */
public class StateModule {

    class SubscriptionRunnable implements Runnable {

        private String returnKey;
        private int interval_ms;

        public SubscriptionRunnable(String returnKey, int interval_ms) {
            this.returnKey = returnKey;
            this.interval_ms = interval_ms;
        }

        @Override
        public void run() {
            boolean go = true;
            while (go) {
                Instant now = Instant.now();
                Messages.LocomotionState locomotionMsg = Messages.LocomotionState.newBuilder()
                        .setConfig(Messages.LocomotionState.Configuration.valueOf(StateModule.this.locomotionState.getConfiguration().ordinal()))
                        .setSpeed(StateModule.this.locomotionState.getStraightSpeed())
                        .setTimestamp(instantToUnixTime(now))
                        .build();
                Messages.ExcavationState excavationMsg = Messages.ExcavationState.newBuilder()
                        .setConfig(Messages.ExcavationState.Configuration.valueOf(StateModule.this.excavationState.getConfiguration().ordinal()))
                        .setSpeed(StateModule.this.excavationState.getStraightSpeed())
                        .setTimestamp(instantToUnixTime(now))
                        .build();
                Messages.DepositionState depositionMsg = Messages.DepositionState.newBuilder()
                        .setConfig(Messages.DepositionState.Configuration.valueOf(StateModule.this.depositionState.getConfiguration().ordinal()))
                        .setSpeed(StateModule.this.depositionState.getStraightSpeed())
                        .setTimestamp(instantToUnixTime(now))
                        .build();
                byte[] locomotionData = locomotionMsg.toByteArray();
                byte[] excavationData = excavationMsg.toByteArray();
                byte[] depositionData = depositionMsg.toByteArray();
                try {
                    //Not sure how to do this bit properly
                    StateModule.this.channel.basicPublish(exchangeName, returnKey, null, locomotionData);
                    StateModule.this.channel.basicPublish(exchangeName, returnKey, null, excavationData);
                    StateModule.this.channel.basicPublish(exchangeName, returnKey, null, depositionData);
                    Thread.sleep(interval_ms);
                } catch (IOException e) {
                    go = false;
                } catch (InterruptedException e) {
                    go = false;
                }
            }
        }
    }

    /* Consumer callback class and methods */
    private class UpdateConsumer extends DefaultConsumer {
        
        public UpdateConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String routingKey = envelope.getRoutingKey();
            String[] keys = routingKey.split("\\.");
            if (keys.length < 2) {
                return;
            }
            String typeOfSensor = keys[1];

            if(typeOfSensor.equals("locomotion")){ //this is a locomotion message
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
            else if(typeOfSensor.equals("excavation")){ //excavation message
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
            else if(typeOfSensor.equals("deposition")){ //deposition message
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
            else { //oops
                return;
            }

        }
    }

    private class RequestConsumer extends DefaultConsumer {

        public RequestConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            Messages.LocomotionStateSubscribe msg = Messages.LocomotionStateSubscribe.parseFrom(body);
            float interval = msg.getInterval();
            int interval_ms = (int)(interval / 1000);
            String replyKey = msg.getReplyKey();
            Thread t = new Thread(new SubscriptionRunnable(replyKey, interval_ms));
            subscriptionThreads.add(t);
            t.start();
        }
    }
    
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
    private LocomotionState locomotionState;
    private ExcavationState excavationState;
    private DepositionState depositionState;
    private String exchangeName;
    private Connection connection;
    private Channel channel;
    private Queue<Thread> subscriptionThreads = new LinkedList<>();
    
    public StateModule(LocomotionState locState, ExcavationState excState, DepositionState depState) {
        this(locState, excState, depState, "amq.topic");
    }

    public StateModule(LocomotionState locState, ExcavationState excState, DepositionState depState, String exchangeName) {
        this.locomotionState = locState;
        this.excavationState = excState;
        this.depositionState = depState;
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
        // Setup connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        this.channel = connection.createChannel();

        // Subscribe to sensor updates
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "sensor.#");
        this.channel.basicConsume(queueName, true, new UpdateConsumer(channel));

        // Listen for requests to subscribe to state updates
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "state.*.subscribe");
        this.channel.basicConsume(queueName, true, new RequestConsumer(channel));
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

    public void stop() throws IOException, TimeoutException, InterruptedException {
        for (Thread t : subscriptionThreads) {
            t.interrupt();
        }
        for (Thread t : subscriptionThreads) {
            t.join();
        }
        channel.close();
        connection.close();
    }
    
    public static void main(String[] args) {
        LocomotionState locState = new LocomotionState();
        ExcavationState excState = new ExcavationState();
        DepositionState depState = new DepositionState();
        StateModule module = new StateModule(locState, excState, depState);
        module.start();
    }
}