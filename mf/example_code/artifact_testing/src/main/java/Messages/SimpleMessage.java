package Messages;

import org.Team107.MF.Message;

public class SimpleMessage extends Message {
    Integer myInt;

    public SimpleMessage() {
        super(SimpleMessage.class);
    }

    public SimpleMessage(Integer myInt){
        super(SimpleMessage.class);
        this.myInt = myInt;
    }

}
