package commands;

/**
 * Created by Brian on 11/9/2016.
 */
public class MidLevelCommand extends Command{

    // FIELDS


    private MidLevelCommandEnum command;


    // ENUM

    public enum MidLevelCommandEnum {

        MOVE_STRAIGHT;

    }


    // CONSTRUCTORS

    public MidLevelCommand(float speed, float timeout, MidLevelCommandEnum command){
        super(speed, timeout);
        this.command = command;
    }


    // GET/SET

    public MidLevelCommandEnum getCommand() {
        return command;
    }

    public void setCommand(MidLevelCommandEnum command) {
        this.command = command;
    }

    //

}
