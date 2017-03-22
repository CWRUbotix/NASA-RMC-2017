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
     * The LoadCell enum is used to specify one of the deposition subsystem's 4
     * load cells.
     */
    public enum LoadCell {
        FRONT_LEFT,
        FRONT_RIGHT,
        BACK_LEFT,
        BACK_RIGHT
    }
    
    /* Data members */
    private EnumMap <LoadCell, Float> loadCellValue;
    private float dumpPos;
    private boolean extended;
    private boolean retracted;

    // TODO: Store the time most recently updated, either for the whole system
    // or for each sensor. If you want to handle out of order updates, you'll
    // need to do it for each sensor I think.
    
    /* Constructor */
    
    public DepositionState() {
        /* Implementation note: In this constructor, all data members are
         * initialized to 0 because this class does not currently consider the
         * case where it has never received input from a particular sensor. In
         * order to handle that case, initialization would need to be done
         * differently.
         */
        
        // TODO: handle no input from sensor

        loadCellValue = new EnumMap<>(LoadCell.class);
        loadCellValue.put(LoadCell.FRONT_LEFT, (float)0);
        loadCellValue.put(LoadCell.FRONT_RIGHT, (float)0);
        loadCellValue.put(LoadCell.BACK_LEFT, (float)0);
        loadCellValue.put(LoadCell.BACK_RIGHT, (float)0);

        dumpPos = 0;

    }
    /* Update methods */
    
    public void updateDumpLoad (LoadCell cell, float load, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        loadCellValue.put(cell, load);
    }
    
    public void updateDumpPos (float pos, Instant time) throws RobotFaultException {
        // TODO: use timestamp to validate data
        // TODO: detect impossibly sudden changes
        dumpPos = pos;
    }
    
    public void updateDumpLimitExtended (boolean pressed, Instant time) throws RobotFaultException {
        extended = pressed;
    }
    
    public void updateDumpLimitRetracted (boolean pressed, Instant time) throws RobotFaultException {
        retracted = pressed;
    }
    
    /* State getter methods */
    
    public boolean isStored() {
        // TODO: use position too
        return retracted;
    }
    
    public float getDumpLoad(LoadCell cell) {
        return loadCellValue.get(cell);
    }
    
    public float getDumpPos() {
        return dumpPos;
    }
}	