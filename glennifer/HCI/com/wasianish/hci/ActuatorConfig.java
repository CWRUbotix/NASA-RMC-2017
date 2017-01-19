package com.wasianish.hci;

/**
 * Configuration values for an Actuator
 */
public class ActuatorConfig {
	// Name of the sensor
	public String name;
	// Description of the sensor
	public String description;
	// ID of the actuator
	public int ID;
	// Conversion from linear to angular values
	// linValue*angLin = angValue
	private double angLinConv;
	// True if angular values, false if linear
	private boolean anglin;
	// Slope of the current vs. torque/force graph (current on y axis)
	private double tfCurrentRatio;
	// Stall torque/force
	private double tfStall;
	// Stall  current
	private double stallCurrent;
	// No load velocity
	private double noLoadVel;
	// No load current
	private double noLoadCurrent;
}
