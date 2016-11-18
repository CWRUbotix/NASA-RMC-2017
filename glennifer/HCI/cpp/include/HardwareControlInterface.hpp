#ifndef HARDWARECONTROLINTERFACE_HPP
#define HARDWARECONTROLINTERFACE_HPP

#include <queue>
#include <vector>
#include <unordered_map>

#include "Actuator.hpp"
#include "Sensor.hpp"

namespace hci {

// Function that takes in data from coordinated actuator and
// returns a target data point.  Used in coordinated actuations
typedef double(*coord_map) (double);

// Types of actuation
enum ActuationType {
	AngVel, AngPos,
	LinVel, LinPos,
	Torque, Force,
	Current, PowerE,
	PowerM, PowerH,
	Temp;
}

struct ActuationConstraint {
	// Type of actuation
	ActuationType type;
	// True for maximum limit
	// False for minimum limit
	bool max;
	// Value of constraint
	double value;
}

struct Actuation {
	// Override previous actuations for this actuator
	bool ovr;
	// Hold target value
	// If false, actuation is removed when completed
	bool hold;
	// Actuator of this actuation
	uint8_t actuator;
	// Type of actuation
	ActuationType type;
	// Target actuation
	double targetValue;
}

struct CoordinatedActuation {
	// Override previous actuations for this actuator
	bool ovr;
	// Hold target value
	// If false, actuation is removed when completed
	bool hold;
	// Actuator of this actuation
	uint8_t actuator;
	// Coordinated actuator (actuator move relative to this)
	uint8_t coordActuator;
	// Type of coordinated actuation
	ActuationType type;
	// Defines relationship between target output and output of coordActuator
	coord_map target;
}

// Generates a linear map between actuators
coord_map genLinearMap(double offset, double scalar);

class HardwareControlInterface {
	private:
		// States whether hardware control interface thread is running
		// Setting false will stop the hardware control interface
		volatile bool running;
		// Lock on actuationQueue to prevent multiple threads from
		// accessing it at the same time
		volatile bool queue_lock;
		// Queue of actuations to be processed
		std::queue<Actuation> actuationQueue;
		// Queue of coordinated actuations to be processed
		std::queue<CoordinatedActuation> coordActuationQueue;
		// Vector of constraints
		std::vector<ActuationConstraint> constraints;
		// Map from actuator ID to actuator
		std::unordered_map<uint8_t, Actuator> actuators;
		// Map from sensor ID to sensor
		std::unordered_map<uint8_t, Sensor> sensors;
		// Vector of all active actuations
		std::vector<Actuation> activeActuations;
		// Vector of all coordinated actuations
		std::vector<CoordinatedActuation> activeCoordActuations;
		
	public:
		// Add an actuation to the queue
		// Will reject actuations if they conflict and are not overriding
		int queueActuation(Actuation actuation);
		// Add a coordinated actuation to the queue
		// Will reject actuations if they conflict and are not overriding
		int queueCoordinatedActuation(CoordinatedActuation coordActuation);
		// Halts the hardware control interface
		void halt();
		// Function for thread of hardware control interface
		void hardwareInterface();
}

}
