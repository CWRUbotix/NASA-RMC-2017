#ifndef HARDWARECONTROLINTERFACE_HPP
#define HARDWARECONTROLINTERFACE_HPP

#include <queue>
#include <vector>
#include <unordered_map>

#include "Actuator.hpp"
#include "Sensor.hpp"

namespace hci {

enum ActuationType {
	AngVel, AngPos,
	LinVel, LinPos,
	Torque, Force,
	Current, PowerE,
	PowerM, PowerH,
	Temp;
}

struct ActuationConstraint {
	ActuationType type;
	bool max;
	double value;
}

struct Actuation {
	bool ovr, hold;
	uint8_t actuator;
	ActuationType type;
	double value;
}

class HardwareControlInterface {
	private:
		volatile bool running, stop, queue_lock;
		std::queue<Actuation> actuationQueue;
		std::vector<ActuationConstraint> constraints;

		std::unordered_map<uint8_t, Actuator> actuators;
		std::unordered_map<uint8_t, Sensor> sensors;

	public:
		int queueActuation(Actuation actuation);
		void halt();
		void hardwareInterface();
}

}
