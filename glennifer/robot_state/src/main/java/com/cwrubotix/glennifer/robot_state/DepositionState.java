package com.cwrubotix.glennifer.robot_state;

import java.time.Instant;
import java.util.EnumMap;

/**
 * A DepositionState object encapsulates the current state of the robot's
 * deposition subsystem. It has update methods to give it sensor data, and
 * getter methods to query the state. Its update methods can raise fault
 * exceptions for all kinds of reasons. These faults can be responded to using
 * the adjustment method.
 * 
 * This class does not deal with messages or wire formats. It works purely at
 * the logical level.
 */
public class DepositionState {
    
    /**
     * The Configuration enum is used to represent the deposition subsystem's
     * current dumping configuration
     */
    public enum Configuration {
        EXTENDED,
		RETRACTED
    }
    
    /* Data members */
    private Configuration configuration;
	private float dumpLoad;
	private float dumpPos;
	private float dumpingSpeed; //probably not needed but i'm not sure
	
    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    /* Constructor */
    
    public DepositionState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never recieved input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor
        configuration = Configuration.RETRACTED;
        dumpLoad = 0;
		dumpPos = 0;
		dumpingSpeed = 0;

    }
    /* Update methods */
    
    public void updateDumpLoad (float load, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        dumpLoad = load;
    }
    
    public void updateDumpPos (float pos, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored configuration
        dumpPos = pos;
    }
    
    public void updateDumpLimitExtended (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    public void updateDumpLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches
    }
    
    /* State getter methods */
    
    public Configuration getConfiguration() {
        // TODO: use physical constants, real or made up, to get configuration
		// probably update with limit switches, not sure right now
        return configuration;
    }
    
    public float getDumpLoad() {
		return dumpLoad;
	}
    
    public float getDumpPos() {
        return dumpPos;
    }
	
	public float getDumpingSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return dumpingSpeed;
    }
}	