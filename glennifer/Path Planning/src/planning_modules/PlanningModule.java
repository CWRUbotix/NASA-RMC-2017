package planning_modules;

import java.util.Queue;

import commands.HighLevelCommand;
import commands.MidLevelCommand;

/**
 * PlanningModule provides an interface for a class to take in 
 * high level commands and output a queue of mid level commands
 */
public abstract class PlanningModule {

	HighLevelCommand highCommand;
	private Queue<MidLevelCommand> commandQueue;
	/**
	 * Recieves the command and fills the commandQueue with appropriate
	 * MidLevelCommands
	 * @param highCommand
	 */
	public void receiveCommand(HighLevelCommand command){
		highCommand = command;
	}
	
	/**
	 * Returns the queue of MidLevelCommands required to complete
	 * a HighLevelCommand.
	 * @return commandQueue
	 */
	public Queue<MidLevelCommand> getCommandQueue(){
		return commandQueue;
	}
	
	/**
	 * Not sure if I'll need this method. Don't use it yet cuz it doesn't do much 
	 * right now.
	 */
	public void waitOrSomething(){
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
