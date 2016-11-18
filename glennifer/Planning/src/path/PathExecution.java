package path;

import commands.MidLevelCommand;

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
        if(PriorityQueue != null){
            PriorityQueue.add(command);
        }
        else{   
            MidLevelComparator comparator = new MidLevelComparator<MidLevelCommand>();
            PriorityQueue = new PriorityQueue<MidLevelCommand>(11, comparator);
        }
        
    }


    // GET / SET    

    public MidLevelCommand getCurrentCommand() {
        return currentCommand;
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
    class MidLevelComparator implements Comparator<MidLevelCommand>{
        @Override
        public int compare(MidLevelCommand x, MidLevelCommand y) {
        // We just want to compare the priority values of the commands
            if (x.getPriority() < y.getPriority()){
                return -1;
            }
            if (x.getPriority() > y.getPriority()){
                return 1;
            }
            return 0;
        }
    }


}
