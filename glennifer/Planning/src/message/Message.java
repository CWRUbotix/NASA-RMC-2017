package message;

/**
 * This class is a local, Java wrapper for a Message. idk how these are actually
 * going to work, so this is what i'm working with for the moment.
 *
 * Created by Brian on 11/9/2016.
 */
public class Message {

    // FIELDS
    private final String msg;

    // CONSTRUCTORS
    public Message(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
