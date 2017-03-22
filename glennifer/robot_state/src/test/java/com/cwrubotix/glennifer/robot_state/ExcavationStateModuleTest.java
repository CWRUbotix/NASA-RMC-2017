package com.cwrubotix.glennifer.robot_state;

import com.cwrubotix.glennifer.Messages;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExcavationStateModuleTest {

    private ExcavationState state; // TODO: mock this
    private ExcavationStateModule module;
    private Thread thread;

    public ExcavationStateModuleTest() { }

    private Messages.UnixTime instantToUnixTime(Instant time) {
        Messages.UnixTime.Builder unixTimeBuilder = Messages.UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }

    @Before
    public void setUp() throws InterruptedException {
        state = new ExcavationState();
        module = new ExcavationStateModule(state, "amq.topic");
        module.start();
        module.awaitReady();
    }

    @After
    public void tearDown() throws IOException, TimeoutException {
        module.stop();
    }

    /**
     * Test of run method, of class ExcavationStateModule.
     */
    @Test
    public void testRun() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Messages.RpmUpdate.Builder rpmUpdateFactory = Messages.RpmUpdate.newBuilder();
        rpmUpdateFactory.setRpm(42F);
        rpmUpdateFactory.setTimestamp(instantToUnixTime(Instant.now()));
        Messages.RpmUpdate message = rpmUpdateFactory.build();
        channel.basicPublish("amq.topic", "sensor.excavation.conveyor_rpm", null, message.toByteArray());

        channel.close();
        connection.close();

        Thread.sleep(1000);

        float result = state.getConveyorRpm();
        assertEquals(42F, result, 0);
    }

}

