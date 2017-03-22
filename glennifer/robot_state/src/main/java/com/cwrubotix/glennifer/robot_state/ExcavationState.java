package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;

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
    
    /* Data members */
	private float conveyorRpm;
	private float conveyorTranslationDisplacement;
	private float armPos;
	private boolean armRetracted;
    private boolean armExtended;
    private boolean translationRetracted;
    private boolean translationExtended;

    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    /* Constructor */
    
    public ExcavationState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never received input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor
        conveyorRpm = 0;
        conveyorTranslationDisplacement = 0;
		armPos = 0;
        armRetracted = false;
        armExtended = false;
        translationRetracted = false;
        translationExtended = false;

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
        armExtended = pressed;
    }
    
    public void updateArmLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
        armRetracted = pressed;
    }
	
	public void updateConveyorTranslationLimitExtended (boolean pressed, Instant time) throws RobotFaultException {
        translationExtended = pressed;
    }
    
    public void updateConveyorTranslationLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        translationRetracted = pressed;
    }
    
    /* State getter methods */
    
    public boolean isClearOfStoredHopper() {
        // TODO: Formula for excavation being clear of stored hopper
        return false;
    }
    public boolean isClearOfDumpingHopper() {
        // TODO: Formula for excavation being clear of dumping hopper
        return false;
    }
    public boolean isInGround() {
        // TODO: Formula for excavation being clear of stored hopper
        return false;
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
}	