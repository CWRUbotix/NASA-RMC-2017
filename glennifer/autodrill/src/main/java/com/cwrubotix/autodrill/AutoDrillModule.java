package com.cwrubotix.autodrill;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import com.rabbitmq.client.AMQP;

import com.cwrubotix.glennifer.Messages;
import com.cwrubotix.glennifer.Messages.LocomotionControlCommandStraight;
import com.cwrubotix.glennifer.Messages.SpeedContolCommand;
import com.cwrubotix.glennifer.Messages.Fault;
import com.cwrubotix.glennifer.Messages.UnixTime;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

/**
 * Reads and sends messages needed to auto drill.
 *
 */
public class AutoDrillModule {

	/*Runnable to monitor the current while the module is running.*/
	class CurrentMonitorRunnable implements Runnable{
		private String exchangeName;
		private Channel channel;
		
		public CurrentMonitorRunnable(Channel channel){
			this.exchangeName = "amq.topic";
			this.channel = channel;
		}
		
		private class CurrentMonitorConsumer extends DefaultConsumer{
			
			public CurrentMonitorConsumer(Channel channel) {
				super(channel);
			}
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
				Messages.CurrentUpdate msg = Messages.CurrentUpdate.parseFrom(body);
				float currentValue = msg.getCurrent();
				if(currentValue > getStallCurrent())
					dealWithStallSituation();
			}
			
			private float getStallCurrent(){
				return 0.0F; //TODO Needs to be changed
			}
			
			private void dealWithStallSituation() throws IOException{
				isStalled = true;
				//Stopping what was going on.
				/* TODO waiting for branches to be merged.
				 *
				 * Messages.StopAllCommand msg1 = Messages.StopAllCommand.newBuilder()
				 * 		   .setTimeOut(123)
				 *         .setStop(true)
				 *         .build();
				 * AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.system.stop_all", null, msg1.toByteArray());
				 *
				 * Messages.StopAllCommand msg2 = Messages.StopAllCommand.newBuilder()
				 *         .setTimeOut(123)
				 *         .setStop(false)
				 *         .build();
				 * AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.system.stop_all", null, msg2.toByteArray());
				 *
				 */	
				
				/*
				 * TODO Come up with what to do when BC is in stall.
				 */
							
				//Going back to where it was before.
				//TODO wait for enough time between commands to make sure they are getting done one at a time.
				if(currentJob == "drill.deep"){
					//excavationConveryorRPM(THE MAGIC DIGGING SPEED);
					//excavationTranslationControl(targetDepth);
				} else if(currentJob == "drill.surface"){
					locomotionStraight();
					//excavationConveryorRPM(THE MAGIC DIGGING SPEED);
					//excavationTranslationControl(targetDepth);
					//locomotionSpeedControl(targetRPM);
				} else if(currentJob == "drill.end"){
					locomotionSpeedControl(0.0F);
					excavationTranslationControl(0.0F);
					excavationConveyorRPM(0.0F);
					excavationAngleControl(0.0F);
				} else{
					System.out.println("Didn't do anything but BC was in stall. Something is wrong,");
					try {
						AutoDrillModule.this.stop();
					} catch (IOException | TimeoutException | InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
				isStalled = false;
			}
		}
		
		@Override
		public void run(){
			String queueName;
			try {
				queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, exchangeName, "sensor.excavation.conveyor_current");
				this.channel.basicConsume(queueName, true, new CurrentMonitorConsumer(channel));
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private class DrillDeepConsumer extends DefaultConsumer{
		public DrillDeepConsumer(Channel channel){
			super(channel);
		}
		
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
			currentJob = "drill.deep";
			if(!isStalled){
				//Messages.DigDeepCommand cmd = Messages.DigDeepCommand.parseFrom(body);
				//float targetDepth = cmd.getDepth();
				locomotionSpeedControl(0.0F);
				excavationTranslationControl(0.0F);
				excavationConveyorRPM(0.0F);
				//excavationAngleControl(THE MAGIC DIGGING ANGLE);
				//excavationConveryorRPM(THE MAGIC DIGGING SPEED);
				//excavationTranslationControl(targetDepth);
				//TODO wait for enough time between commands to make sure they are getting done one at a time.
				//TODO fill in magic numbers via testing and add DigDeepCommand Features in protobuff so we can get desired depth from message.
			}
		}
	}
	
	private class DrillSurfaceConsumer extends DefaultConsumer{
		public DrillSurfaceConsumer(Channel channel){
			super(channel);
		}
		
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
			currentJob = "drill.surface";
			if(!isStalled){
				//Messages.DigSurfaceCommand cmd = Messages.DigSurfaceCommand.parseFrom(body);
				//float targetDepth = cmd.getTargetDepth();
				//float targetRPM = cmd.getRPM();
				locomotionSpeedControl(0.0F);
				excavationTranslationControl(0.0F);
				excavationConveyorRPM(0.0F);
				//excavationAngleControl(THE MAGIC DIGGING ANGLE);
				//excavationConveryorRPM(THE MAGIC DIGGING SPEED);
				//excavationTranslationControl(targetDepth);
				locomotionStraight();
				//locomotionSpeedControl(targetRPM);
				//TODO wait for enough time between commands to make sure they are getting done one at a time.
				//TODO fill in magic numbers via testing and add DigSurfaceCommand Features in protobuff so we can get desired depth and RPM from message.
			}
		}
	}
	
	private class DrillEndConsumer extends DefaultConsumer{
		public DrillEndConsumer(Channel channel){
			super(channel);
		}
		
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
			currentJob = "drill.end";
			if(!isStalled){
				locomotionSpeedControl(0.0F);
				excavationTranslationControl(0.0F);
				excavationConveyorRPM(0.0F);
				excavationAngleControl(0.0F);
			}
			//TODO wait for enough time between commands to make sure they are getting done one at a time.
		}
	}

	
	private void excavationTranslationControl(float targetValue) throws IOException{
		Messages.PositionContolCommand pcc = Messages.PositionContolCommand.newBuilder()
				.setPosition(targetValue)
				.setTimeout(123)
				.build();
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.excavation.conveyor_translation_displacement", null, pcc.toByteArray());
	}
	
	private void excavationAngleControl(float targetValue) throws IOException{
		Messages.PositionContolCommand pcc = Messages.PositionContolCommand.newBuilder()
				.setPosition(targetValue)
				.setTimeout(123)
				.build();
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.excavation.arm_pos", null, pcc.toByteArray());
	}
	
	private void excavationConveyorRPM(float targetValue) throws IOException{
		Messages.SpeedContolCommand msg = SpeedContolCommand.newBuilder()
				.setRpm(targetValue)
				.setTimeout(123)
				.build();
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.excavation.bucket_conveyor_rpm", null, msg.toByteArray());
	}
	
	private void locomotionSpeedControl(float targetValue) throws IOException{
		Messages.SpeedContolCommand msg = SpeedContolCommand.newBuilder()
				.setRpm(targetValue)
				.setTimeout(123)
				.build();
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.locomotion.front_left.wheel.RPM", null, msg.toByteArray());
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.locomotion.front_right.wheel.RPM", null, msg.toByteArray());
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.locomotion.back_right.wheel.RPM", null, msg.toByteArray());
		AutoDrillModule.this.channel.basicPublish(exchangeName, "motorcontrol.locomotion.back_left.wheel.RPM", null, msg.toByteArray());
	}

	private void locomotionStraight() throws IOException{
		Messages.LocomotionControlCommandStraight msg = LocomotionControlCommandStraight.newBuilder()
				.setTimeout(123)
				.setSpeed(0.5F)
				.build();
		AutoDrillModule.this.channel.basicPublish(exchangeName, "subsyscommand.locomotion.straight", null, msg.toByteArray());
	}
	
	private String exchangeName;
	private Connection connection;
	private Channel channel;
	private Thread currentMonitor;
	private String currentJob;
	private boolean isStalled = false;
	
	public AutoDrillModule(){
		this("amq.topic");
	}
	
	public AutoDrillModule(String exchangeName){
		this.exchangeName = exchangeName;
	}
	
	private UnixTime instantToUnixTime(Instant time) {
		UnixTime.Builder unixTimeBuilder = UnixTime.newBuilder();
		unixTimeBuilder.setTimeInt(time.getEpochSecond());
		unixTimeBuilder.setTimeFrac(time.getNano() / 1000000000F);
		return unixTimeBuilder.build();
	}
	    
	private void sendFault(int faultCode, Instant time) throws IOException {
		Fault.Builder faultBuilder = Fault.newBuilder();
		faultBuilder.setFaultCode(faultCode);
		faultBuilder.setTimestamp(instantToUnixTime(time));
		Fault message = faultBuilder.build();
		channel.basicPublish(exchangeName, "fault", null, message.toByteArray());
	}
	
	public void runWithExceptions() throws IOException, TimeoutException{
		//Setup connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		this.connection = factory.newConnection();
		this.channel = connection.createChannel();
		
		//Start Current Monitor Thread
		currentMonitor = new Thread(new CurrentMonitorRunnable(channel));
		currentMonitor.run();
		
		//Listen for DrillDeep command
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchangeName, "drill.deep");
		this.channel.basicConsume(queueName, true, new DrillDeepConsumer(channel));
		
		//Listen for DrillSurface command
		queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchangeName, "drill.surface");
		this.channel.basicConsume(queueName, true, new DrillSurfaceConsumer(channel));
		
		//Listen for DrillEnd command
		queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchangeName, "drill.end");
		this.channel.basicConsume(queueName, true, new DrillEndConsumer(channel));
	}
	
	public void start(){
		try{
			runWithExceptions();
		} catch(Exception e){
			try{
				sendFault(999, Instant.now());
			} catch(Exception e1){}
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public void stop() throws IOException, TimeoutException, InterruptedException{
		currentMonitor.interrupt();
		currentMonitor.join();
		channel.close();
		connection.close();
	}
	
	public static void main(String[] args){
		AutoDrillModule module = new AutoDrillModule();
		module.start();
	}
}
