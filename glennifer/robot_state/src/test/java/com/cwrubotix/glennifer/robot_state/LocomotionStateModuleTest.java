package com.cwrubotix.glennifer.robot_state;

import com.cwrubotix.glennifer.Messages;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.cwrubotix.glennifer.Messages.RpmUpdate;
import com.cwrubotix.glennifer.Messages.LimitUpdate;
import com.cwrubotix.glennifer.Messages.PositionUpdate;
import com.cwrubotix.glennifer.Messages.Fault;
import com.cwrubotix.glennifer.Messages.UnixTime;
import com.rabbitmq.client.GetResponse;
import java.time.Instant;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocomotionStateModuleTest {
    
    private LocomotionState state; // TODO: mock this
    private LocomotionStateModule module;
    private Thread thread;
    
    public LocomotionStateModuleTest() { }
    
    private UnixTime instantToUnixTime(Instant time) {
        UnixTime.Builder unixTimeBuilder = UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }
    
    @Before
    public void setUp() {
        state = new LocomotionState();
        module = new LocomotionStateModule(state, "amq.topic");
        thread = new Thread(module);
        thread.start();
    }
    
    @After
    public void tearDown() {
        thread.stop();
    }

    /**
     * Test of run method, of class LocomotionStateModule.
     */
    @Test
    public void testRun() throws Exception {
        Thread.sleep(1000);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        RpmUpdate.Builder rpmUpdateFactory = RpmUpdate.newBuilder();
        rpmUpdateFactory.setRpm(42F);
        rpmUpdateFactory.setTimestamp(instantToUnixTime(Instant.now()));
        RpmUpdate message = rpmUpdateFactory.build();
        channel.basicPublish("amq.topic", "sensor.locomotion.back_left.wheel_rpm", null, message.toByteArray());
        
        Thread.sleep(1000);
        
        float result = state.getWheelRpm(LocomotionState.Wheel.BACK_LEFT);
        
        assertEquals(42F, result, 0);
    }

    /**
     * Test subscription
     */
    @Test
    public void testSubscribe() throws Exception {
        Thread.sleep(1000);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Create queue
        channel.exchangeDeclare("amq.topic", "topic", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "amq.topic", queueName);

        // Queue is known to be empty

        // Send message
        Messages.LocomotionStateSubscribe subMsg = Messages.LocomotionStateSubscribe.newBuilder()
                .setStartTime(instantToUnixTime(Instant.now()))
                .setInterval(0.1f)
                .setReplyKey(queueName)
                .build();

        channel.basicPublish("amq.topic", "state.locomotion.subscribe", null, subMsg.toByteArray());

        // Wait
        Thread.sleep(1000);

        GetResponse response = channel.basicGet(queueName, true);
        assertNotNull("Failed to get message from state subscription", response);
        byte[] body = response.getBody();
        Messages.LocomotionState s = Messages.LocomotionState.parseFrom(body);
        System.out.println(s);
    }
}
