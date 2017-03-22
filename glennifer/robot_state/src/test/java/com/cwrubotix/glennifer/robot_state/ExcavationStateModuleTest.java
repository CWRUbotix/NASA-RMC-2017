package com.cwrubotix.glennifer.robot_state;

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

public class ExcavationStateModuleTest {

    private ExcavationState state; // TODO: mock this
    private ExcavationStateModule module;
    private Thread thread;

    public ExcavationStateModuleTest() { }

    private UnixTime instantToUnixTime(Instant time) {
        UnixTime.Builder unixTimeBuilder = UnixTime.newBuilder();
        unixTimeBuilder.setTimeInt(time.getEpochSecond());
        unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
        return unixTimeBuilder.build();
    }

    @Before
    public void setUp() throws InterruptedException {
        state = new ExcavationState();
        module = new ExcavationStateModule(state, "amq.topic");
        thread = new Thread(module);
        thread.start();
        module.awaitReady();
    }

    @After
    public void tearDown() {
        thread.stop();
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

        RpmUpdate.Builder rpmUpdateFactory = RpmUpdate.newBuilder();
        rpmUpdateFactory.setRpm(42F);
        rpmUpdateFactory.setTimestamp(instantToUnixTime(Instant.now()));
        RpmUpdate message = rpmUpdateFactory.build();
        channel.basicPublish("amq.topic", "sensor.excavation.conveyor_rpm", null, message.toByteArray());

        channel.close();
        connection.close();

        Thread.sleep(1000);

        float result = state.getConveyorRpm();
        assertEquals(42F, result, 0);
    }

}

