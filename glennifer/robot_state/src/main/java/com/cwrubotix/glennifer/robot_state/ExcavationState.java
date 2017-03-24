package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import java.util.EnumMap;

/**
 * A ExcavationState object encapsulates the current state of the robot's
 * excavation subsystem. It has update methods to give it sensor data, and
 * getter methods to query the state. Its update methods can raise fault
 * exceptions for all kinds of reasons. These faults can be responded to using
 * the adjustment method.
 * 
 * This class does not deal with messages or wire formats. It works purely at
 * the logical level.
 */
public class ExcavationState {
    
    /**
     * The Configuration enum is used to represent the excavation subsystem's
     * current arm configuration
     */
    public enum Configuration {
        EXTENDED,
		RETRACTED
    }
    
    /* Data members */
    private Configuration configuration;
	private float conveyorRpm;
	private float conveyorTranslationDisplacement;
	private float armPos;
	
	private float conveyorSpeed;
    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    /* Constructor */
    
    public ExcavationState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never recieved input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor
        configuration = Configuration.RETRACTED;
        conveyorRpm = 0;
        conveyorTranslationDisplacement = 0;
		armPos = 0;
		conveyorSpeed = 0;

    }
    
    /* Update methods */
    
    public void updateConveyorRpm (float rpm, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored conveyor speed here
        conveyorRpm = rpm;
    }
    
    public void updateArmPos (float pos, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored configuration
        armPos = pos;
    }
	
	public void updateConveyorTranslationDisplacement (float displacement, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored configuration
        conveyorTranslationDisplacement = displacement;
    }
    
    public void updateArmLimitExtended (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
        configuration = Configuration.EXTENDED;
    }
    
    public void updateArmLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
        configuration = Configuration.RETRACTED;
    }
	
	public void updateConveyorTranslationLimitExtended (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    public void updateConveyorTranslationLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    /* State getter methods */
    
    public Configuration getConfiguration() {
        // TODO: use physical constants, real or made up, to get configuration
		// probably update with limit switches, not sure right now
        return configuration;
    }
    
    public float getConveyorTranslationDisplacement() {
		return conveyorTranslationDisplacement;
	}
        
    public float getConveyorRpm() {
        return conveyorRpm;
    }
    
    public float getArmPos() {
        return armPos;
    }
	
	public float getConveyorSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return conveyorSpeed;
    }
}	