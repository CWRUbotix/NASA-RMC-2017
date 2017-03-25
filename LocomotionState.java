package com.cwrubotix.glennifer.robot_state;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Optional;

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
    private Configuration configuration = Configuration.STRAIGHT;
    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    //Floats for containing time since updates
    private Instant timeSinceWheelRPM;
    private Instant timeSincePodPos;
    private Instant timeSystem;
    
    /* Constructor */
    
    public LocomotionState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never received input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor
    	
        wheelRpm = new EnumMap<>(Wheel.class);
        wheelRpm.put(Wheel.FRONT_LEFT, null);
        wheelRpm.put(Wheel.FRONT_RIGHT, null);
        wheelRpm.put(Wheel.BACK_LEFT, null);
        wheelRpm.put(Wheel.BACK_RIGHT, null);
        
        wheelPodPos = new EnumMap<>(Wheel.class);
        wheelPodPos.put(Wheel.FRONT_LEFT, null);
        wheelPodPos.put(Wheel.FRONT_RIGHT, null);
        wheelPodPos.put(Wheel.BACK_LEFT, null);
        wheelPodPos.put(Wheel.BACK_RIGHT, null);
        
        forwardSpeed = 0;
        turnSpeed = 0;
        strafeSpeed = 0;
    }
    
    /* Update methods */
    
    public void updateWheelRpm (Wheel wheel, float rpm, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
    	
        wheelRpm.put(wheel, rpm);
       
        //Check if time is null
        Optional<Instant> opTime = Optional.ofNullable(time);
        if((opTime.isPresent()) && timeSinceWheelRPM != null){
        Duration duration = Duration.between(timeSinceWheelRPM, time);
        
    	//some hardcoded consistency value?
    	if(duration.toMillis() > 2000){
    		//throw something?
    		}
        }
    	
        //else update PodPos time
    	timeSinceWheelRPM = Instant.now();
        
        //update speed
        //currently speed is just the average of the rpm of the 4 wheels
        //TODO: Use real constants to make this actually accurate
        //Gets value of Optional<Float>, else throws NoSuchElement
    	
    	//the optional, honestly could be more efficient to
    	//check the wheelrpm.get for null rather than do this conversion
        Optional<Float> rpmWL = Optional.ofNullable(wheelRpm.get(Wheel.FRONT_LEFT));
        //number of wheels reporting values
        int divNum = 0;
        //total RPM
        Float rpmTot = (float)0;
        
        if(rpmWL.isPresent()){
        	divNum++;
        	rpmTot+= rpmWL.get();
        }
        rpmWL = Optional.ofNullable(wheelRpm.get(Wheel.FRONT_RIGHT));
        if(rpmWL.isPresent()){
        	divNum++;
        	rpmTot+= rpmWL.get();
        }
        rpmWL = Optional.ofNullable(wheelRpm.get(Wheel.BACK_RIGHT));
        if(rpmWL.isPresent()){
        	divNum++;
        	rpmTot+= rpmWL.get();
        }
        rpmWL = Optional.ofNullable(wheelRpm.get(Wheel.BACK_RIGHT));
        if(rpmWL.isPresent()){
        	divNum++;
        	rpmTot+= rpmWL.get();
        }
        //speed is the averaged RPM for reporting wheels
        float speed = (Optional.of(rpmTot)).get() / Optional.of(divNum).get();
        
        switch(configuration){
            case STRAIGHT:
                forwardSpeed = speed;
                break;
            case TURN:
                turnSpeed = speed;
                break;
            case STRAFE:
                strafeSpeed = speed;
                break;
        }
    }
    
    public void updateWheelPodPos (Wheel wheel, float pos, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        // TODO: consider updating stored configuration
    	
    	if(timeSincePodPos != null && time != null){
    	Duration duration = Duration.between(timeSincePodPos, time);
    	
    	//some hardcoded consistency value?
    		if(duration.toMillis() > 2000){
    		//throw something?
    		}
    	}
    	//else update PodPos time
    	timeSincePodPos = Instant.now();
        wheelPodPos.put(wheel, pos);
    }
    
    public void updateWheelPodLimitExtended (Wheel wheel, boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit 
        // From Paul:
        //For Locomotion, there is only one limit switch. 
        //When it is fully retracted, we know we are in STRAIGHT
        //When it is fully extended, we know we are in STRAFE
        //Turning configuration is somewhere in between, we can't tell with the limit switches.
        //For all situations, we should apparently use the potentiometer to double check

        //For now, when we are extended, we are probably in STRAFE
        configuration = Configuration.STRAFE;
    }
    
    public void updateWheelPodLimitRetracted (Wheel wheel, boolean pressed, Instant time) throws RobotFaultException {
        // TODO: use limit switches

        //For now, when we are retracted, we are probably in STRAIGHT
        configuration = Configuration.STRAIGHT;
    }
    
    /* State getter methods */
    
    public Configuration getConfiguration() {
        // TODO: use physical constants, real or made up, to get configuration
        return configuration;
    }
    
    public float getStraightSpeed() {
        // TODO: use physical constants, real or made up, to get speed
    	//0.1596 is the conversion factor from RPM to M/s, assuming no slippage
        return (float)(forwardSpeed * 0.1596);
    }
    
    public float getTurnSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return (float)(turnSpeed * 0.1596);
    }
    
    public float getStrafeSpeed() {
        // TODO: use physical constants, real or made up, to get speed
        return (float)(strafeSpeed * 0.1596);
    }
    
    public float getWheelRpm(Wheel wheel) {
        return wheelRpm.get(wheel);
    }
    
    public float getWheelPodPos(Wheel wheel) {
        return wheelPodPos.get(wheel);
    }
}
