package org.Team107.MF.Testing;

import org.Team107.MF.Message;

public class BasicMessage2 extends Message {

    public String sth;

    BasicMessage2 () {
        super(BasicMessage2.class);
        this.sth = "";
    }

    public BasicMessage2(String sth) {
        super(BasicMessage2.class);
        this.sth = sth;
    }

    public String getSth() {
        return this.sth;
    }

}
