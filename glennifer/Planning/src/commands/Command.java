package commands;

/**
 * Wrapper class holding all the command enums
 *
 * Created by Brian on 11/9/2016.
 */
public class Command {

    private float speed;
    private float timeout;

    public Command(float speed, float timeout){
        this.speed = speed;
        this.timeout = timeout;
    }

    // GET/SET
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTimeout() {
        return timeout;
    }

    public void setTimeout(float timeout) {
        this.timeout = timeout;
    }



}
