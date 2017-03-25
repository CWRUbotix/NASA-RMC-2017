package com.cwrubotix.glennifer.hci;

import com.cwrubotix.glennifer.Messages;

import com.rabbitmq.client.*;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ModuleMain {
	public static final int baud = 9600;
	
	/* Listen for Topics */
	//Motor Controls
	public static final String motorTopic = "motorcontrol.#";
	
	private static HardwareControlInterface hci;

	public static void runWithConnectionExceptions() throws IOException, TimeoutException {
		// Read connection config
		InputStream input = new FileInputStream("config/connection.yml");
		Yaml yaml = new Yaml();
		Object connectionConfigObj = yaml.load(input);
		Map<String, String> connectionConfig = (Map<String, String>)connectionConfigObj;
		String serverAddress = connectionConfig.get("server-addr");
		String serverUsername = connectionConfig.get("server-user");
		String serverPassword = connectionConfig.get("server-pass");
		String exchangeName = connectionConfig.get("exchange-name");

		//Connect and Configure AMPQ
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(serverAddress); //replace local host with host name
		factory.setUsername(serverUsername);
		factory.setPassword(serverPassword);
		Connection connection = factory.newConnection(); // throws
		Channel channel = connection.createChannel(); // throws
		String queueName = channel.queueDeclare().getQueue();

		// Initialize port as null
		String port = null;
		// For each attached serial port
		for(String s:SerialPortList.getPortNames()) {
			SerialPort sp = new SerialPort(s);
			try {
				// Open the port
				sp.openPort();
				sp.setParams(baud, 8, 1, 0);
				sp.setDTR(false);
				// Create test packet
				byte[] bt = {0x5A,0x01,0x00};
				// Write test byte 0x5A
				sp.writeBytes(bt);
				Thread.sleep(2000);
				// Read response bytes
				byte[] b = sp.readBytes(1,1000);
				// If response is 0xA5, it is the arduino
				System.out.println(Byte.toString(b[0]));
				if(b[0] == (byte)0xA5) {
					// Capture the string of correct port
					port = s;
					// Close the port
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// If port is still null, couldn't find it
		if(port == null) {
			System.out.println("Couldn't find attached arduino, please try again");
			return;
		} else {
			System.out.println("Found arduino at " + port);
		}
		// Open the found arduino port
		SerialPort sport = new SerialPort(port);
		// Try open port
		try {
			sport.openPort();
			Thread.sleep(1000);
			sport.setParams(baud, 8, 1, 0);
			sport.setDTR(false);
		} catch (SerialPortException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		// Start HCI

		Thread hciThread = new Thread(hci);
		hciThread.start();
		/*
		try {
			Thread.sleep(100);
			Actuation a = new Actuation();
			a.override = true;
			a.hold = true;
			a.targetValue = 1;
			a.type = HardwareControlInterface.ActuationType.AngVel;
			a.actuatorID = 0;
			hci.queueActuation(a);
			Thread.sleep(3000);
			hci.halt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/

		//Start AMPQ Thread
		//Listen for messages
		//if

		channel.queueBind(queueName, exchangeName, motorTopic);


		//Print waiting for messages

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				//String message = new String(body "UTF-8");
				//Print Received Message
				String routingKey = envelope.getRoutingKey();
				String[] keys = routingKey.split("\\.");
				System.out.println(routingKey);
				if(keys.length < 4) {
					System.out.println("Failed to interpret routing key");
					return;
				}
				if (keys[3].equals("wheel_rpm")) {
					Messages.SpeedContolCommand scc = Messages.SpeedContolCommand.parseFrom(body);
					System.out.println(scc.getRpm());
					Actuation a = new Actuation();
					a.override = true;
					a.hold = true;
					a.targetValue = sign(scc.getRpm());
					a.type = HardwareControlInterface.ActuationType.AngVel;
					a.actuatorID = 2;
					hci.queueActuation(a);

				} else if (keys[3].equals("wheel_pod_pos")) {
					Messages.PositionContolCommand pcc = Messages.PositionContolCommand.parseFrom(body);
					System.out.println(pcc.getPosition());
					Actuation a = new Actuation();
					a.override = true;
					a.hold = true;
					a.targetValue = sign(pcc.getPosition());
					a.type = HardwareControlInterface.ActuationType.AngVel;
					a.actuatorID = 3;
					hci.queueActuation(a);
				}
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}

	private static int sign(double x) {
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		try {
			runWithConnectionExceptions();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}



	}

}
