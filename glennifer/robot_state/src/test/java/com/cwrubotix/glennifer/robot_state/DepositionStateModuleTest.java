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

public class DepositionStateModuleTest {

    private DepositionState state; // TODO: mock this
    private DepositionStateModule module;

    public DepositionStateModuleTest() { }

    private Messages.UnixTime instantToUnixTime(Instant time) {
        Messages.UnixTime.Builder unixTimeBuilder = Messages.UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }

    @Before
    public void setUp() throws InterruptedException {
        state = new DepositionState();
        module = new DepositionStateModule(state, "amq.topic");
        module.start();
    }

    @After
    public void tearDown() throws IOException, TimeoutException {
        module.stop();
    }

    /**
     * Test of run method, of class DepositionStateModule.
     */
    @Test
    public void testRun() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Messages.LoadUpdate.Builder loadUpdateFactory = Messages.LoadUpdate.newBuilder();
        loadUpdateFactory.setLoad(5F);
        loadUpdateFactory.setTimestamp(instantToUnixTime(Instant.now()));
        Messages.LoadUpdate message = loadUpdateFactory.build();
        channel.basicPublish("amq.topic", "sensor.deposition.dump_load.back_left", null, message.toByteArray());

        channel.close();
        connection.close();

        Thread.sleep(1000);

        float result = state.getDumpLoad(DepositionState.LoadCell.BACK_LEFT);
        assertEquals(5F, result, 0);
    }

}


