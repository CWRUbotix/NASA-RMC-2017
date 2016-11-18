package path;

import cli.CommandLineHarness;
import commands.MidLevelCommand;
import message.Message;
import message.MessageBuilder;
import message.MessageSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class functions as the driver for the Planning module. This class will receive and
 * subscribe to relevant Messages. To respond to those messages, it will make the appropriate
 * method calls and status updates to the path.PathPlanning and path.PathExecution classes.
 *
 * This class abstracts the Messaging interface away from PathPlanning and PathExecution. That
 * is, PathDriver receives Messages, translates them into function calls to PathPlanning and
 * PathExecution, then formulates and publishes the appropriate Message in response.
 *
 * Created by Brian on 11/9/2016.
 */
public class PathDriver {

    // FIELDS

    private PathPlanning planner;
    private PathExecution executor;
    private MessageBuilder messageBuilder;
    private MessageSender messageSender;



    // CONSTRUCTORS

    public PathDriver(){

        /* Message Subscriptions */

        //Put topics into queues
            //declare queue (name, durability, name callback for queue creation)

        //basic consume
            //call szomething parse message into Java objects we can use
            //


        /* Message Advertisements */

        planner = new PathPlanning();
        executor = new PathExecution();
        messageBuilder = new MessageBuilder();
        messageSender = new MessageSender();

    }

    public PathDriver(MessageSender sender){
        planner = new PathPlanning();
        executor = new PathExecution();
        messageBuilder = new MessageBuilder();
        messageSender = sender;
    }


    // PUBLIC METHODS


    // HELPER METHODS

    /**
     * This method is implemented as a callback procedure on the event
     * that the PathDriver receives a Message asking for the current
     * command being executed. PathDriver now queries
     */
    private void publishCurrentCommand() {

        MidLevelCommand currentCommand = executor.getCurrentCommand();

        if(currentCommand==null)
            return;

        Message ccMessage = messageBuilder.commandMessage(currentCommand);

        try {
            messageSender.publish(ccMessage);
        }
        catch(IOException e){
            //Log error, don't send a message. We'd like to report error,
            //but we just had an IO error trying to communicate to the
            //person we'd like to report an error to.
        }
    }

    private void addCommandToQueue(MidLevelCommand command){
        executor.addCommandToQueue(command);
    }



    private void addDefaultCommandToQueue() {

        MidLevelCommand command = new MidLevelCommand(  0.0f,
                                                        0.0f,
                                                        MidLevelCommand.MidLevelCommandEnum.MOVE_STRAIGHT,
                                                        MidLevelCommand.MidLevelCommandPriority.MIDDLE );

        addCommandToQueue(command);
    }

    private void publishNextCommand(){

        MidLevelCommand nextCommand = executor.getNextCommand();

        if(nextCommand==null)
            return;

        Message ccMessage = messageBuilder.commandMessage(nextCommand);

        try {
            messageSender.publish(ccMessage);
        }
        catch(IOException e){
            //Log error, don't send a message. We'd like to report error,
            //but we just had an IO error trying to communicate to the
            //person we'd like to report an error to.
        }
    }

    private void publishQueue(){
        PriorityQueue<MidLevelCommand> queue = executor.getCommandQueue();

        if(queue == null)
            return;

        Message message = messageBuilder.queueMessage(queue);

        try{
            messageSender.publish(message);
        }
        catch(IOException e){
            ;
        }
    }



    // GET/SET


    // MAIN METHOD

    public static void main(String[] args){

        PathDriver driver = new PathDriver();

    }

    public class CommandLineHarness {


        public void runCLI(){

            String nextLine = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


            while( nextLine != null && !nextLine.equals("quit") ){

                try {
                    nextLine = reader.readLine();

                    String[] inputArr = nextLine.split(" ");

                    List<String> inputList = new ArrayList<>(Arrays.asList(inputArr));
                    String command = inputList.get(0);
                    inputList.remove(0);
                    List<String> args = new ArrayList<>(inputList);

                    Callback callback = Callback.callbackOf(command);

                    doCallback(callback, args);

                }
                catch(IOException e){
                    nextLine = "";
                }
            }

            System.out.println("Exiting");

        }

        private void doCallback(Callback callback, List<String> args) throws IOException {

            switch(callback){

                case PUBLLISH_CURRENT_COMMAND:
                    PathDriver.this.publishCurrentCommand();
                    break;

                case ADD_DEFAULT_COMMAND_TO_QUEUE:
                    PathDriver.this.addDefaultCommandToQueue();
                    break;

                case ADD_COMMAND_TO_QUEUE:
                    MidLevelCommand command = new MidLevelCommand(
                            Float.parseFloat(args.get(0)),
                            Float.parseFloat(args.get(1)),
                            MidLevelCommand.MidLevelCommandEnum.valueOf(args.get(2)),
                            MidLevelCommand.MidLevelCommandPriority.valueOf(args.get(3))
                            );

                    PathDriver.this.addCommandToQueue(command);
                    break;

                case PUBLISH_NEXT_COMMAND:
                    PathDriver.this.publishNextCommand();
                    break;

                case PUBLISH_QUEUE:
                    PathDriver.this.publishQueue();
                    break;

                default:
                    sendDefaultMessage();

            }
        }

        private void sendDefaultMessage() throws IOException {
            Message message = PathDriver.this.messageBuilder.readableMessage("Unrecognized command");
            PathDriver.this.messageSender.publish(message);
        }
    }

    private enum Callback {

        PUBLLISH_CURRENT_COMMAND,
        PUBLISH_NEXT_COMMAND,
        ADD_DEFAULT_COMMAND_TO_QUEUE,
        PUBLISH_QUEUE,
        ADD_COMMAND_TO_QUEUE,
        UNKNWON_CALLBACK;


        private static Callback callbackOf(String callbackName){

            Callback callback;

            switch(callbackName){

                case "current":
                    callback = PUBLLISH_CURRENT_COMMAND;
                    break;

                case "addDefault":
                    callback = ADD_DEFAULT_COMMAND_TO_QUEUE;
                    break;

                case "next":
                    callback = PUBLISH_NEXT_COMMAND;
                    break;

                case "queue":
                    callback = PUBLISH_QUEUE;
                    break;

                case "add":
                    callback = ADD_COMMAND_TO_QUEUE;
                    break;

                default:
                    callback = UNKNWON_CALLBACK;
            }
            return callback;
        }



    }

}
