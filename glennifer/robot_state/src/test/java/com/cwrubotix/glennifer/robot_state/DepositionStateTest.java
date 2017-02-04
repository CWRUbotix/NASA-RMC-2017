package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import org.junit.Test;
import static org.junit.Assert.*;

public class DepositionStateTest {

    public DepositionStateTest() { }

    /**
     * Test of updateDumpLoad method, of class DepositionState.
     */
    @Test
    public void testDumpLoad() throws Exception {
        float load = 4.1F;
        Instant time = Instant.now();
        DepositionState instance = new DepositionState();
        instance.updateDumpLoad(load, time);
        float resultLoad = instance.getDumpLoad();
        assertEquals(load, resultLoad, 0);
    }

    /**
     * Test of updateArmPos method, of class DepositionState.
     */
    @Test
    public void testDumpPos() throws Exception {
        float dumpPos = 4.8F;
        Instant time = Instant.now();
        DepositionState instance = new DepositionState();
        instance.updateDumpPos(dumpPos, time);
        float resultDumpPos = instance.getDumpPos();
        assertEquals(dumpPos, resultDumpPos, 0);
    }

    /**
     * Test of updateArmLimitExtended, UpdateArmLimitRetracted methods, of class DepositionState.
     */
    @Test
    public void testLimitSwitchConfigurations() throws Exception {
        Instant time = Instant.now();
        DepositionState instance = new DepositionState();
        DepositionState.Configuration extendedConfiguration = DepositionState.Configuration.EXTENDED;
        DepositionState.Configuration retractedConfiguration = DepositionState.Configuration.RETRACTED;
        instance.updateDumpLimitExtended(true, time); //enter arm EXTENDED configuration
        assertEquals(extendedConfiguration, instance.getConfiguration());
        instance.updateDumpLimitRetracted(true, time); //enter arm RETRACTED configuration
        assertEquals(retractedConfiguration, instance.getConfiguration());
    }
}

