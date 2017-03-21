package com.cwrubotix.glennifer.robot_state;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.cwrubotix.glennifer.Messages.LoadUpdate;
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

public class DepositionStateModuleTest {

    private DepositionState state; // TODO: mock this
    private DepositionStateModule module;
    private Thread thread;

    public DepositionStateModuleTest() { }

    private UnixTime instantToUnixTime(Instant time) {
        UnixTime.Builder unixTimeBuilder = UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }

    @Before
    public void setUp() {
        state = new DepositionState();
        module = new DepositionStateModule(state, "amq.topic");
        thread = new Thread(module);
        thread.start();
    }

    @After
    public void tearDown() {
        thread.stop();
    }

    /**
     * Test of run method, of class DepositionStateModule.
     */
    @Test
    public void testRun() throws Exception {
        Thread.sleep(1000);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        LoadUpdate.Builder loadUpdateFactory = LoadUpdate.newBuilder();
        loadUpdateFactory.setLoad(5F);
        loadUpdateFactory.setTimestamp(instantToUnixTime(Instant.now()));
        LoadUpdate message = loadUpdateFactory.build();
        channel.basicPublish("amq.topic", "sensor.deposition.dump_load.back_left", null, message.toByteArray());

        channel.close();
        connection.close();

        Thread.sleep(1000);

        float result = state.getDumpLoad(DepositionState.LoadCell.BACK_LEFT);
        assertEquals(5F, result, 0);
    }

}


