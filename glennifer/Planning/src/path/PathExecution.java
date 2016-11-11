package path;

import commands.MidLevelCommand;

import java.util.Queue;

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
    private Queue<MidLevelCommand> commandQueue;


    // PUBLIC METHODS

    public PathExecution() {

    }


    // GET / SET

    public MidLevelCommand getCurrentCommand() {
        return currentCommand;
    }

    public void setCurrentCommand(MidLevelCommand currentCommand) {
        this.currentCommand = currentCommand;
    }

    public Queue<MidLevelCommand> getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(Queue<MidLevelCommand> commandQueue) {
        this.commandQueue = commandQueue;
    }
}
