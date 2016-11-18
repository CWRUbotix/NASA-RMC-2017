#ifndef ACTUATOR_HPP
#define ACTUATOR_HPP

#include <vector>
#include <string>
#include <chrono>

#include "Sensor.hpp"

namespace hci {

enum OutputType { OutSpeed, OutPos };
enum FeedbackType { FBAngVel, FBAngPos, FBLinVel, FBLinPos, FBCurrent, FBVoltage };

struct ActuatorData {
	double volt, amp, linvel, linpos, angvel, angpos, torque, force, powerM, powerE, powerH, efficiency, temp;
	std::chrono::high_resolution_clock::time_point timestamp;
}

struct SensorFeedback {
	// 8 bit sensor ID
	uint8_t sensorID;
	// Type of feedback
	FeedbackType fbType;
	// Conversion from sensor value to linear or angular quantity defined in fbType
	// FBCurrent and FBVoltage should ideally have convFactor = 1;
	double convFactor;
}

class Actuator {
	private:
		/**
		 * Configuration
		 */

		// Short name of the motor
		std::string name;
		// Long description of the motor
		std::string description;
		// 8 bit ID of motor
		// Used to communicate with hardware
		uint8_t id;
		// Conversion between angular and linear velocities
		// ang_value = anglinConv * lin_value
		// Also used for torque/force conversion
		// Commonly used as the radius
		double anglinConv;
		// Defines which values are used.  True for angular
		// Angular: torque (Nm), angular velocity (rad/s)
		// Linear: force (N), linear velocity (m/s)
		bool anglin;
		// Defines the relationship between torque/force and current draw
		// tfcRatio is the slope of the torque vs. current plot
		// current = noloadCurrent + tfCurrentRatio * (torque or force)
		double tfCurrentRatio;
		// Stall torque or force
		double stallTF;
		// Stall current
		double stallCurrent;
		// No load speed, in rad/s or m/s
		double noloadVel;
		// No load current
		double noloadCurrent;
		
		// Vector of actuator data
		std::vector<ActuatorData> data;
		// Output type of actuator
		OutputType outputType;
		
		// Vector of feedback sources
		std::vector<SensorFeedback> sensors;
	public:
		// Polls sensors and generates an ActuatorData that is then stored in data
		void update();
		// Get's the data "back" spaces from the end of the vector
		ActuatorData getState(uint8_t back);
		// Adds a sensor to the list of feedback sensors
		void addFeedback(uint8_t sensorID, FeedbackType fbType, double conv);
		// Constructor.  Loads data from config
		Actuator(std::string file);
		~Actuator();

}


}
