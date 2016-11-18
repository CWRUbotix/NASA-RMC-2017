package path;

import cli.CommandLineHarness;
import commands.MidLevelCommand;
import message.Message;
import message.MessageBuilder;
import message.MessageSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

                    Callback callback = Callback.callbackOf(nextLine);

                    doCallback(callback);

                }
                catch(IOException e){
                    nextLine = "";
                }
            }

            System.out.println("Exiting");

        }

        private void doCallback(Callback callback) throws IOException {

            switch(callback){

                case PUBLLISH_CURRENT_COMMAND:
                    PathDriver.this.publishCurrentCommand();
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
        UNKNWON_CALLBACK;


        private static Callback callbackOf(String callbackName){

            Callback callback;

            switch(callbackName){

                case "publish":
                    callback = PUBLLISH_CURRENT_COMMAND;
                    break;

                default:
                    callback = UNKNWON_CALLBACK;
            }
            return callback;
        }



    }

}
