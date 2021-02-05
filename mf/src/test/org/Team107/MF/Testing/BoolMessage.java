package org.Team107.MF.Testing;


import org.Team107.MF.Message;

import java.util.UUID;

public class BoolMessage extends Message {

    private Boolean b;

    public BoolMessage() {
        super(BoolMessage.class);
        this.b = false;
    }

    public BoolMessage(boolean id) {
        super(BoolMessage.class);
        this.b = id;
    }

    public Boolean getB() {
        return this.b;
    }


}
