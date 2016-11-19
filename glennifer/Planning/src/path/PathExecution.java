package path;

import commands.MidLevelCommand;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This class is responsible for publishing Messages detailing the next
 * MidLevelCommand the robot should execute. The queue of commands to
 * be executed comes from path.PathPlanning. The next queued command will be
 * executed when path.PathDriver receives a message from the robot state
 * indicating it is ready for the next command.
 *
 * Created by Brian on 11/9/2016.
 */
public class PathExecution {

    // FIELDS

    private MidLevelCommand currentCommand;
    private PriorityQueue<MidLevelCommand> commandQueue;


    // PUBLIC METHODS

    public PathExecution() {

    }

    public void addCommandToQueue(MidLevelCommand command){
        if(commandQueue == null){
            MidLevelComparator comparator = new MidLevelComparator();
            commandQueue = new PriorityQueue(11, comparator);
        }

        commandQueue.add(command);
        
    }


    // GET / SET    

    public MidLevelCommand getCurrentCommand() {

        return currentCommand;
    }

    public MidLevelCommand getNextCommand() {
        return commandQueue!=null ? commandQueue.peek() : null;
    }

    public void setCurrentCommand(MidLevelCommand currentCommand) {
        this.currentCommand = currentCommand;
    }

    public PriorityQueue<MidLevelCommand> getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(PriorityQueue<MidLevelCommand> commandQueue) {
        this.commandQueue = commandQueue;
    }


    //Comparator used for the priority queue
    private class MidLevelComparator implements Comparator<MidLevelCommand> {
        @Override
        public int compare(MidLevelCommand x, MidLevelCommand y) {
        // We just want to compare the priority values of the commands

            MidLevelCommand.MidLevelCommandPriority xPriority = x.getPriority();
            MidLevelCommand.MidLevelCommandPriority yPriority = y.getPriority();

            int compareVal = xPriority.compare(yPriority);

            return compareVal;
        }
    }


}
