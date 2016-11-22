package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import java.util.EnumMap;

/**
 * A LocomotionState object encapsulates the current state of the robot's
 * locomotion subsystem. It has update methods to give it sensor data, and
 * getter methods to query the state. Its update methods can raise fault
 * exceptions for all kinds of reasons. These faults can be responded to using
 * the adjustment method.
 * 
 * This class does not deal with messages or wire formats. It works purely at
 * the logical level.
 */
public class LocomotionState {
    
    /**
     * The Wheel enum is used to specify one of the locomotion subsystem's 4
     * wheels.
     */
    public enum Wheel {
        FRONT_LEFT,
        FRONT_RIGHT,
        BACK_LEFT,
        BACK_RIGHT;
    }
    
    /**
     * The Configuration enum is used to represent the locomotion subsystem's
     * overall wheel pod configuration.
     */
    public enum Configuration {
        STRAIGHT,
        TURN,
        STRAFE,
        INTERMEDIATE;
    }
    
    /* Data members */
    private EnumMap <Wheel, Float> wheelRpm;
    private EnumMap <Wheel, Float> wheelPodPos;
    private float forwardSpeed;
    private float turnSpeed;
    private float strafeSpeed;
    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    /* Constructor */
    
    public LocomotionState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never recieved input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor
        
        wheelRpm = new EnumMap<>(Wheel.class);
        wheelRpm.put(Wheel.FRONT_LEFT, (float)0);
        wheelRpm.put(Wheel.FRONT_RIGHT, (float)0);
        wheelRpm.put(Wheel.BACK_LEFT, (float)0);
        wheelRpm.put(Wheel.BACK_RIGHT, (float)0);
        
        wheelPodPos = new EnumMap<>(Wheel.class);
        wheelPodPos.put(Wheel.FRONT_LEFT, (float)0);
        wheelPodPos.put(Wheel.FRONT_RIGHT, (float)0);
        wheelPodPos.put(Wheel.BACK_LEFT, (float)0);
        wheelPodPos.put(Wheel.BACK_RIGHT, (float)0);
        
        forwardSpeed = 0;
        turnSpeed = 0;
        strafeSpeed = 0;
    }
    
    /* Update methods */
    
    public void updateWheelRpm (Wheel wheel, float rpm, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored forward/turn/strafe speed here
        wheelRpm.put(wheel, rpm);
    }
    
    public void updateWheelPodPos (Wheel wheel, float pos, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored configuration
        wheelPodPos.put(wheel, pos);
    }
    
    public void updateWheelPodLimitExtended (Wheel wheel, boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    public void updateWheelPodLimitRetracted (Wheel wheel, boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    /* State getter methods */
    
    public Configuration getConfiguration() {
        // TODO: use physical constants, real or made up, to get configuration
        return Configuration.STRAIGHT;
    }
    
    public float getStraightSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return 0;
    }
    
    public float getTurnSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return 0;
    }
    
    public float getStrafeSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return 0;
    }
    
    public float getWheelRpm(Wheel wheel) {
        return wheelRpm.get(wheel);
    }
    
    public float getWheelPodPos(Wheel wheel) {
        return wheelPodPos.get(wheel);
    }
}
