package com.wasianish.hci;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class Main {
	private static HardwareControlInterface hci;
	public static void main(String[] args) {
		String port = null;
		for(String s:SerialPortList.getPortNames()) {
			SerialPort sp = new SerialPort(s);
			try {
				sp.openPort();
				sp.writeByte((byte)0x5A);
				byte[] b = sp.readBytes(1,1000);
				if(b[0] == (byte)0x5A) {
					port = s;
					sp.closePort();
					break;
				}
				sp.closePort();
			} catch(SerialPortException e) {
				e.printStackTrace();
				continue;
			} catch(SerialPortTimeoutException e) {
				try {
					sp.closePort();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}
		if(port == null) {
			System.out.println("Couldn't find attached arduino, please try again");
			return;
		}
		SerialPort sport = new SerialPort(port);
		try {
			sport.openPort();
		} catch (SerialPortException e) {
			e.printStackTrace();
			return;
		}
		hci = new HardwareControlInterface(sport);
		// Initialize sensors
		
		
		
		// Add sensors
		
		
		
		// Initialize actuators
		ActuatorConfig configLBM = new ActuatorConfig();
		configLBM.ID = 0;
		configLBM.name = "Left Rear Drive Motor";
		configLBM.description = "Vex BAG Motor, 270:1";
		configLBM.anglin = true;
		configLBM.nomVoltage = 12;
		configLBM.noLoadCurrent = 1.8;
		configLBM.noLoadVel = (13180/270)*2*Math.PI/60;
		configLBM.stallCurrent = 53;
		configLBM.tfStall = 0.43*270;
		configLBM.tfCurrentRatio = (configLBM.stallCurrent - configLBM.noLoadCurrent)/configLBM.tfStall;
		
		ActuatorConfig configRBM = configLBM.copy();
		configRBM.ID = 1;
		configRBM.name = "Right Rear Drive Motor";
		
		ActuatorConfig configLFM = configLBM.copy();
		configLFM.ID = 2;
		configLFM.name = "Right Front Drive Motor";
		
		ActuatorConfig configRFM = configLBM.copy();
		configRFM.ID = 3;
		configRFM.name = "Right Front Drive Motor";
		
		ActuatorConfig configLBA = new ActuatorConfig();
		configLBA.ID = 4;
		configLBA.name = "Left Rear Turning Actuator";
		configLBA.description = "Progressive Automations PA-14P-4-150";
		configLBA.anglin = false;
		configLBA.nomVoltage = 12;
		configLBA.noLoadCurrent = 0;
		configLBA.noLoadVel = 0.015;
		configLBA.stallCurrent = 5;
		configLBA.tfStall = 33.7;
		configLBA.tfCurrentRatio = (configLBA.stallCurrent - configLBA.noLoadCurrent)/configLBA.tfStall;
		
		ActuatorConfig configRBA = configLBA.copy();
		configRBA.ID = 5;
		configRBA.name = "Right Rear Turning Actuator";
		
		ActuatorConfig configLFA = configLBA.copy();
		configLFA.ID = 6;
		configLFA.name = "Left Front Turning Actuator";
		
		ActuatorConfig configRFA = configLBA.copy();
		configRFA.ID = 5;
		configRFA.name = "Right Front Turning Actuator";
		
		// Add actuators
		hci.addActuator(new Actuator(configLBM, hci), configLBM.ID);
		hci.addActuator(new Actuator(configRBM, hci), configRBM.ID);
		hci.addActuator(new Actuator(configLFM, hci), configLFM.ID);
		hci.addActuator(new Actuator(configRFM, hci), configRFM.ID);
		hci.addActuator(new Actuator(configLBA, hci), configLBA.ID);
		hci.addActuator(new Actuator(configRBA, hci), configRBA.ID);
		hci.addActuator(new Actuator(configLFA, hci), configLFA.ID);
		hci.addActuator(new Actuator(configRFA, hci), configRFA.ID);
		
		// Constrain actuators

	}

}
