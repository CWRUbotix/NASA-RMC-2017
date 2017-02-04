package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocomotionStateTest {
    
    public LocomotionStateTest() { }

    /**
     * Test of updateWheelRpm method, of class LocomotionState.
     */
    @Test
    public void testWheelRpm() throws Exception {
        float rpmFrontLeft = 4.2F;
        Instant time = Instant.now();
        LocomotionState instance = new LocomotionState();
        instance.updateWheelRpm(LocomotionState.Wheel.FRONT_LEFT, rpmFrontLeft, time);
        float resultFrontLeft = instance.getWheelRpm(LocomotionState.Wheel.FRONT_LEFT);
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        float rpmFrontRight = 5.2F;
        instance.updateWheelRpm(LocomotionState.Wheel.FRONT_RIGHT, rpmFrontRight, time);
        resultFrontLeft = instance.getWheelRpm(LocomotionState.Wheel.FRONT_LEFT);
        float resultFrontRight = instance.getWheelRpm(LocomotionState.Wheel.FRONT_RIGHT);
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        assertEquals(rpmFrontRight, resultFrontRight, 0);
        float rpmBackLeft = -6.2F;
        instance.updateWheelRpm(LocomotionState.Wheel.BACK_LEFT, rpmBackLeft, time);
        resultFrontLeft = instance.getWheelRpm(LocomotionState.Wheel.FRONT_LEFT);
        resultFrontRight = instance.getWheelRpm(LocomotionState.Wheel.FRONT_RIGHT);
        float resultBackLeft = instance.getWheelRpm(LocomotionState.Wheel.BACK_LEFT);
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        assertEquals(rpmFrontRight, resultFrontRight, 0);
        assertEquals(rpmBackLeft, resultBackLeft, 0);
        float rpmBackRight = -1.2F;
        instance.updateWheelRpm(LocomotionState.Wheel.BACK_RIGHT, rpmBackRight, time);
        resultFrontLeft = instance.getWheelRpm(LocomotionState.Wheel.FRONT_LEFT);
        resultFrontRight = instance.getWheelRpm(LocomotionState.Wheel.FRONT_RIGHT);
        resultBackLeft = instance.getWheelRpm(LocomotionState.Wheel.BACK_LEFT);
        float resultBackRight = instance.getWheelRpm(LocomotionState.Wheel.BACK_RIGHT);
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        assertEquals(rpmFrontRight, resultFrontRight, 0);
        assertEquals(rpmBackLeft, resultBackLeft, 0);
        assertEquals(rpmBackRight, resultBackRight, 0);
        float averageRpm = (rpmFrontLeft + rpmFrontRight + rpmBackLeft + rpmBackRight) / 4;
        LocomotionState.Configuration resultConfiguration = instance.getConfiguration();
        float resultAverageStraightRpm = instance.getStraightSpeed();
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        assertEquals(rpmFrontRight, resultFrontRight, 0);
        assertEquals(rpmBackLeft, resultBackLeft, 0);
        assertEquals(rpmBackRight, resultBackRight, 0);
        assertEquals(LocomotionState.Configuration.STRAIGHT, instance.getConfiguration());
        assertEquals(averageRpm, resultAverageStraightRpm, 0);
        //TODO: test that this code works for TURN configuration
        instance.updateWheelPodLimitExtended(LocomotionState.Wheel.BACK_LEFT, true, time); //enter STRAFE configuration
        instance.updateWheelRpm(LocomotionState.Wheel.BACK_LEFT, rpmBackLeft, time); //update STRAFE speed (won't happen like this in reality)
        resultConfiguration = instance.getConfiguration();
        float resultAverageStrafeRpm = instance.getStrafeSpeed();
        assertEquals(rpmFrontLeft, resultFrontLeft, 0);
        assertEquals(rpmFrontRight, resultFrontRight, 0);
        assertEquals(rpmBackLeft, resultBackLeft, 0);
        assertEquals(rpmBackRight, resultBackRight, 0);
        assertEquals(LocomotionState.Configuration.STRAFE, instance.getConfiguration());
        assertEquals(averageRpm, resultAverageStrafeRpm, 0);
    }

    /**
     * Test of updateWheelPodPos method, of class LocomotionState.
     */
    @Test
    public void testWheelPodPos() throws Exception {
        float podPosFrontLeft = 4.2F;
        Instant time = Instant.now();
        LocomotionState instance = new LocomotionState();
        instance.updateWheelPodPos(LocomotionState.Wheel.FRONT_LEFT, podPosFrontLeft, time);
        float resultFrontLeft = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_LEFT);
        assertEquals(podPosFrontLeft, resultFrontLeft, 0);
        float podPosFrontRight = 5.2F;
        instance.updateWheelPodPos(LocomotionState.Wheel.FRONT_RIGHT, podPosFrontRight, time);
        resultFrontLeft = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_LEFT);
        float resultFrontRight = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_RIGHT);
        assertEquals(podPosFrontLeft, resultFrontLeft, 0);
        assertEquals(podPosFrontRight, resultFrontRight, 0);
        float podPosBackLeft = -6.2F;
        instance.updateWheelPodPos(LocomotionState.Wheel.BACK_LEFT, podPosBackLeft, time);
        resultFrontLeft = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_LEFT);
        resultFrontRight = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_RIGHT);
        float resultBackLeft = instance.getWheelPodPos(LocomotionState.Wheel.BACK_LEFT);
        assertEquals(podPosFrontLeft, resultFrontLeft, 0);
        assertEquals(podPosFrontRight, resultFrontRight, 0);
        assertEquals(podPosBackLeft, resultBackLeft, 0);
        float podPosBackRight = -1.2F;
        instance.updateWheelPodPos(LocomotionState.Wheel.BACK_RIGHT, podPosBackRight, time);
        resultFrontLeft = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_LEFT);
        resultFrontRight = instance.getWheelPodPos(LocomotionState.Wheel.FRONT_RIGHT);
        resultBackLeft = instance.getWheelPodPos(LocomotionState.Wheel.BACK_LEFT);
        float resultBackRight = instance.getWheelPodPos(LocomotionState.Wheel.BACK_RIGHT);
        assertEquals(podPosFrontLeft, resultFrontLeft, 0);
        assertEquals(podPosFrontRight, resultFrontRight, 0);
        assertEquals(podPosBackLeft, resultBackLeft, 0);
        assertEquals(podPosBackRight, resultBackRight, 0);
    }

    /**
     * Test of updateWheelPodLimitExtended and UpdateWheelPodLimitRetracted methods, of class LocomotionState.
     */
    @Test
    public void testLimitSwitchConfigurations() throws Exception {
        Instant time = Instant.now();
        LocomotionState instance = new LocomotionState();
        LocomotionState.Configuration straightConfiguration = LocomotionState.Configuration.STRAIGHT;
        LocomotionState.Configuration strafeConfiguration = LocomotionState.Configuration.STRAFE;
        instance.updateWheelPodLimitExtended(LocomotionState.Wheel.BACK_LEFT, true, time); //enter STRAFE configuration
        assertEquals(strafeConfiguration, instance.getConfiguration());
        instance.updateWheelPodLimitRetracted(LocomotionState.Wheel.BACK_LEFT, true, time); //enter STRAIGHT configuration
        assertEquals(straightConfiguration, instance.getConfiguration());
    }
}
