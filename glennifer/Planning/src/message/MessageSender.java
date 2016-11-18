package message;

import java.io.*;

/**
 * Created by Brian on 11/9/2016.
 */
public class MessageSender {

    // FIELDS

    OutputStream target;


    public MessageSender(){
        target = System.out;
    }

    public MessageSender(OutputStream outputStream){
        target = outputStream;
    }

    // PUBLIC METHODS

    public void publish(Message message) throws IOException{
        target.write(message.getMsg().getBytes());
    }



}
