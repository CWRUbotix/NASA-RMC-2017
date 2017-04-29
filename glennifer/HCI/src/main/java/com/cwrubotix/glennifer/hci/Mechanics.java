package com.cwrubotix.glennifer.hci;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Mechanics {
	// These constants I don't know the values of, but I know they need to be
	// here
	public static final double wheelShaftOffsetX = 1f;
	public static final double wheelShaftOffsetY = 1f;
	public static final double wheelActuatorMinLength = 1f;

	// I'm not sure what these constants are, but they are used for Wheel Pod
	// Position.
	public static double WHEEL_POD_POS_PARAM_A;
	public static double WHEEL_POD_POS_PARAM_B;
	public static double WHEEL_POD_POS_PARAM_C;
	public static double WHEEL_POD_POS_PARAM_D;
	public static double WHEEL_POD_POS_PARAM_E;
	public static double WHEEL_POD_POS_PARAM_F;
	public static double WHEEL_POD_POS_PARAM_G;
	public static double WHEEL_POD_POS_PARAM_H;
	public static double WHEEL_POD_POS_PARAM_I;
	public static double WHEEL_POD_POS_PARAM_J;
	public static double WHEEL_POD_POS_PARAM_K;

	// I think this is for the correct order of magnitude, but not sure
	//public static final double unknown = 100.0;

	// I think these are what the constants are.
	public static double secToMin;
	public static double tickRate;
	public static double gearRatio;

	public static Map<String, Double> initialize() throws IOException{
		// Read Mechanics config
		InputStream input = new FileInputStream("config/Mechanics.yml");
		Yaml yaml = new Yaml();
		Object connectionConfigObj = yaml.load(input);
		Map<String, Double> mechanicsConfig = (Map<String, Double>) connectionConfigObj;
		
		WHEEL_POD_POS_PARAM_A = mechanicsConfig.get("WHEEL_POD_POS_PARAM_A");
		WHEEL_POD_POS_PARAM_B = mechanicsConfig.get("WHEEL_POD_POS_PARAM_B");
		WHEEL_POD_POS_PARAM_C = mechanicsConfig.get("WHEEL_POD_POS_PARAM_C");
		WHEEL_POD_POS_PARAM_D = mechanicsConfig.get("WHEEL_POD_POS_PARAM_D");
		WHEEL_POD_POS_PARAM_E = mechanicsConfig.get("WHEEL_POD_POS_PARAM_E");
		WHEEL_POD_POS_PARAM_F = mechanicsConfig.get("WHEEL_POD_POS_PARAM_F");
		WHEEL_POD_POS_PARAM_G = mechanicsConfig.get("WHEEL_POD_POS_PARAM_G");
		WHEEL_POD_POS_PARAM_H = mechanicsConfig.get("WHEEL_POD_POS_PARAM_H");
		WHEEL_POD_POS_PARAM_I = mechanicsConfig.get("WHEEL_POD_POS_PARAM_I");
		WHEEL_POD_POS_PARAM_J = mechanicsConfig.get("WHEEL_POD_POS_PARAM_J");
		WHEEL_POD_POS_PARAM_K = mechanicsConfig.get("WHEEL_POD_POS_PARAM_K");
		
		secToMin = mechanicsConfig.get("secToMin");
		tickRate = mechanicsConfig.get("tickRate");
		gearRatio = mechanicsConfig.get("gearRatio");
		
		return mechanicsConfig;
	}

	// TODO: Small issue: does name match what it does
	public static double wheelPodValueToPos(double value) {
		/*
		 * return WHEEL_POD_POS_PARAM_A / Math.PI
		 * Math.asin((((WHEEL_POD_POS_PARAM_B / WHEEL_POD_POS_PARAM_C *
		 * (WHEEL_POD_POS_PARAM_D - value) - WHEEL_POD_POS_PARAM_E) /
		 * WHEEL_POD_POS_PARAM_F - WHEEL_POD_POS_PARAM_G)) /
		 * WHEEL_POD_POS_PARAM_H) - WHEEL_POD_POS_PARAM_I;
		 */
		//// value = ((3.3 / 1024 * (1023 - value) - 0.04624) / 0.79547 -
		//// 1.03586);
		return (((WHEEL_POD_POS_PARAM_B / WHEEL_POD_POS_PARAM_C * (WHEEL_POD_POS_PARAM_D - value)
				- WHEEL_POD_POS_PARAM_E) / WHEEL_POD_POS_PARAM_F - WHEEL_POD_POS_PARAM_G));

	}

	public static double wheelRPMToValue(double rpm) {
		return (rpm / secToMin) * gearRatio * tickRate / 100;
	}

	public static double wheelValueToRPM(double value) {
		return 100.0F * value * secToMin / gearRatio / tickRate;
	}

	// Confirm what this does...
	public static double wheelPosToRad(double value) {

		return WHEEL_POD_POS_PARAM_J / Math.PI * Math.asin(value / WHEEL_POD_POS_PARAM_H) - WHEEL_POD_POS_PARAM_I
				+ WHEEL_POD_POS_PARAM_K;
	}

	// TODO: Confirm how this is used, maybe change name
	public static double wheelPodPosToValue(double value) {
		return WHEEL_POD_POS_PARAM_D - (WHEEL_POD_POS_PARAM_E + WHEEL_POD_POS_PARAM_F * (WHEEL_POD_POS_PARAM_G
				+ WHEEL_POD_POS_PARAM_H * Math.sin(Math.PI * (value + WHEEL_POD_POS_PARAM_I) / WHEEL_POD_POS_PARAM_J)))
				* (WHEEL_POD_POS_PARAM_C / WHEEL_POD_POS_PARAM_B);

	}
}
	// Quick sanity verification
//	public static void main(String args[]) throws IOException {
//
//		Map<String, Double> mechanicsConfig = initialize();
//		System.out.println("unknown " + mechanicsConfig.get("unknown"));
		
//		double testVal = 100;
//		double val1;
//		double val2;
//
//		val1 = (-(testVal / 60.0F) * 270 * 4096 / 100.0F);
//		val2 = -Mechanics.wheelRPMToValue(testVal);
//
//		if (val1 != val2) {
//			System.out.println("Problem at wheelRPMToValue");
//		}
//
//		val1 = 1023 - (.04624 + 0.79547 * (1.03586 + 1.50175 * Math.sin(Math.PI * (testVal + 316.63691) / 180.2324)))
//				* (1024 / 3.3);
//		val2 = Mechanics.wheelPodPosToValue(testVal);
//
//		if (val1 != val2) {
//			System.out.println("Problem at wheelPodPosToValue");
//		}
//
//		val1 = -100.0F * testVal * 60.0F / 270 / 4096;
//		val2 = -(Mechanics.wheelValueToRPM(testVal));
//
//		if (val1 != val2) {
//			System.out.println("Problem at wheelPValueToRPM");
//		}
//
//		// Test PoDValueToPos followed by PosToRad
//		val1 = ((3.3 / 1024 * (1023 - testVal) - 0.04624) / 0.79547 - 1.03586);
//		val2 = Mechanics.wheelPodValueToPos(testVal);
//
//		// System.out.println("Val1: " + val1);
//		// System.out.println("Val2: " + val2);
//		if (val1 != val2) {
//
//			System.out.println("Problem at wheelPodValueToPos");
//		}
//
//		val1 = 1;
//		val2 = 1;
//
//		val1 = 180.2324 / Math.PI * Math.asin(val1 / 1.50175) - 316.63691 + 360;
//		val2 = Mechanics.wheelPosToRad(val2);
//
//		if (val1 != val2) {
//			System.out.println("Val1: " + val1);
//			System.out.println("Val2: " + val2);
//			System.out.println("Problem at wheelPosToRad");
//		}
//
//	}
//}
