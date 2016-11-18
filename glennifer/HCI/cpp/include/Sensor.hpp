#ifndef SENSOR_HPP
#define SENSOR_HPP

#include <vector>
#include <string>
#include <chrono>

namespace hci {

template <class T>
struct SensorData {
	T data;
	std::chrono::high_resolution_clock::time_point timestamp;
}

template <class T>
class Sensor {
	private:
		std::string name;
		std::string description;
		uint8_t id;
		std::vector<T> data;
	public:
		
		void update(T val);
		SensorData getData(uint8_t back);
		Sensor(std::string file);
		~Sensor();

}

}
