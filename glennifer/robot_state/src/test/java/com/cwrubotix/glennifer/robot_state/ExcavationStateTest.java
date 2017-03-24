package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExcavationStateTest {

    public ExcavationStateTest() { }

    /**
     * Test of updateConveyorRpm method, of class ExcavationState.
     */
    @Test
    public void testConveyorRpm() throws Exception {
        float rpmConveyor = 4.1F;
        Instant time = Instant.now();
        ExcavationState instance = new ExcavationState();
        instance.updateConveyorRpm(rpmConveyor, time);
        float resultConveyor = instance.getConveyorRpm();
        assertEquals(rpmConveyor, resultConveyor, 0);
    }

    /**
     * Test of updateArmPos method, of class ExcavationState.
     */
    @Test
    public void testArmPos() throws Exception {
        float armPos = 2.7F;
        Instant time = Instant.now();
        ExcavationState instance = new ExcavationState();
        instance.updateArmPos(armPos, time);
        float resultArmPos = instance.getArmPos();
        assertEquals(armPos, resultArmPos, 0);
    }

    /**
     * Test of updateConveyorTranslationDisplacement method, of class ExcavationState.
     */
    @Test
    public void testConveyorTranslationDisplacement() throws Exception {
        float disp = 3.5F;
        Instant time = Instant.now();
        ExcavationState instance = new ExcavationState();
        instance.updateConveyorTranslationDisplacement(disp, time);
        float resultDisplacement = instance.getConveyorTranslationDisplacement();
        assertEquals(disp, resultDisplacement, 0);
    }

    /**
     * Test of updateArmLimitExtended, UpdateArmLimitRetracted methods,
     * UpdateConveyorTranslationLimitExtended, and UpdateConveyorTranslationLimitRetracted of class ExcavationState.
     */
    @Test
    public void testLimitSwitchConfigurations() throws Exception {
        Instant time = Instant.now();
        ExcavationState instance = new ExcavationState();
        ExcavationState.Configuration extendedConfiguration = ExcavationState.Configuration.EXTENDED;
        ExcavationState.Configuration retractedConfiguration = ExcavationState.Configuration.RETRACTED;
        instance.updateArmLimitExtended(true, time); //enter arm EXTENDED configuration
        assertEquals(extendedConfiguration, instance.getConfiguration());
        instance.updateArmLimitRetracted(true, time); //enter arm RETRACTED configuration
        assertEquals(retractedConfiguration, instance.getConfiguration());
        //TODO: Add configuration states for both types of limit switches in excavation
    }
}
