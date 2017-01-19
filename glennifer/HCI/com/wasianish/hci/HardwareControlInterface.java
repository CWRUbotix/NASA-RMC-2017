package com.wasianish.hci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class HardwareControlInterface implements Runnable {
	// The types of actuations and constraints that can be made
	enum ActuationType {
		AngVel, AngPos,
		LinVel, LinPos,
		Torque, Force,
		Current, PowerE,
		PowerM, PowerH,
		Temp;
	}
	
	// Held true when the interface is running 
	private volatile boolean running = false;
	// Queue of actuations to be checked in
	private LinkedBlockingQueue<Actuation> actuationQueue = new LinkedBlockingQueue<Actuation>();
	// Queue of coordinated actuations to be checked in
	private LinkedBlockingQueue<CoordinatedActuation> coordinatedActuationQueue = new LinkedBlockingQueue<CoordinatedActuation>();
	// List of constraints set on various motors, etc.
	private ArrayList<ActuationConstraint> constraints = new ArrayList<ActuationConstraint>();
	// Hashmap of actuators to their ID's
	private HashMap<Integer, Actuator> actuators = new HashMap<Integer,Actuator>();
	// Hashmap of sensors to their ID's
	private HashMap<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
	// List of active actuation jobs
	private ArrayList<Actuation> activeActuations = new ArrayList<Actuation>();
	// List of active coordinated actuation jobs
	private ArrayList<CoordinatedActuation> activeCoordinatedActuations = new ArrayList<CoordinatedActuation>();
	
	/**
	 * Queue's an actuation to be checked in
	 * @param actuation The actuation job that is to be checked in
	 */
	public void queueActuation(Actuation actuation) {
		actuationQueue.add(actuation);
	}
	
	/**
	 * Queue's a coordinated actuation to be checked in
	 * @param coordinatedActuation The actuation job that is to be checked in
	 */
	public void queueCoordinatedActuation(CoordinatedActuation coordinatedActuation) {
		coordinatedActuationQueue.add(coordinatedActuation);
	}
	
	/**
	 * Halts the interface
	 * @return 0 if success, 1 if it was not running
	 */
	public int halt() {
		if(running) {
			running = false;
			return 0;
		}
		return 1;
	}
	
	/**
	 * Get the sensor object from its ID
	 * @param ID The sensor ID
	 * @return The sensor object
	 */
	public Sensor getSensorFromID(int ID) {
		return sensors.get(ID);
	}
	
	/**
	 * Adds an actuator to the list of actuators
	 * @param actuator The actuator to be added
	 * @param id The ID of the actuator
	 * @return 0 if success, 1 if that ID is already registered
	 */
	public int addActuator(Actuator actuator, int id) {
		if(actuators.containsKey(id)) {
			return 1;
		} else {
			actuators.put(id, actuator);
			return 0;
		}
	}
	
	/**
	 * Adds a sensor to the list of sensors
	 * @param sensor The sensor to be added
	 * @param id The ID of the sensor
	 * @return 0 if success, 1 if that ID is already registered
	 */
	public int addSenor(Sensor sensor, int id) {
		if(sensors.containsKey(id)) {
			return 1;
		} else {
			sensors.put(id, sensor);
			return 0;
		}
	}
	
	@Override
	public void run() {
		running = true;
		while(running) {
			// Read sensors
			
			// Update actuator data
			
			// Process queue of actuations and coordinated actuations
			
			// Calculate errors in actuation targets with actuator data
			
			// PID
			
			// Calculate errors in coordinated actuation targets with actuator data
			
			// PID
			
			// Set outputs
		}
	}
	
}