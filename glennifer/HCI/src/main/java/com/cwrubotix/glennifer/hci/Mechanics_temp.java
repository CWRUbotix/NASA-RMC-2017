package com.cwrubotix.glennifer.hci;

public class Mechanics_temp {
	// These constants I don't know the values of, but I know they need to be
	// here
	public static final double wheelShaftOffsetX = 1f;
	public static final double wheelShaftOffsetY = 1f;
	public static final double wheelActuatorMinLength = 1f;

	// I'm not sure what these constants are, but they are used for Wheel Pod
	// Position.
	public static final double WHEEL_POD_POS_PARAM_A = 180.2324;
	public static final double WHEEL_POD_POS_PARAM_B = 3.3;
	public static final double WHEEL_POD_POS_PARAM_C = 1024;
	public static final double WHEEL_POD_POS_PARAM_D = 1023;
	public static final double WHEEL_POD_POS_PARAM_E = 0.04624;
	public static final double WHEEL_POD_POS_PARAM_F = 0.79547;
	public static final double WHEEL_POD_POS_PARAM_G = 1.03586;
	public static final double WHEEL_POD_POS_PARAM_H = 1.50175;
	public static final double WHEEL_POD_POS_PARAM_I = 316.63691;

	// I think this is for the correct order of magnitude, but not sure
	public static final double unknown = 100.0;

	// I think these are what the constants are.
	public static final double secToMin = 60.0f;
	public static final double tickRate = 4096;
	public static final double gearRatio = 270;

	public static double wheelPodValueToPos(double value) {
		return WHEEL_POD_POS_PARAM_A / Math.PI
				* Math.asin((((WHEEL_POD_POS_PARAM_B / WHEEL_POD_POS_PARAM_C * (WHEEL_POD_POS_PARAM_D - value)
						- WHEEL_POD_POS_PARAM_E) / WHEEL_POD_POS_PARAM_F - WHEEL_POD_POS_PARAM_G))
						/ WHEEL_POD_POS_PARAM_H)
				- WHEEL_POD_POS_PARAM_I;
	}

	public static double wheelRPMToValue(double rpm) {
		return (rpm / secToMin) * gearRatio * tickRate / unknown;
	}

	public static double wheelValueToRPM(double value) {
		return 100.0F * value * secToMin / gearRatio / tickRate;
	}
}
