package org.Team107.MF.Testing;

import org.Team107.MF.Message;

public class BasicMessage1 extends Message {

    public String sth;

    BasicMessage1 () {
        super(BasicMessage1.class);
        this.sth = "";
    }

    public BasicMessage1(String sth) {
        super(BasicMessage1.class);
        this.sth = sth;
    }

    public String getSth() {
        return this.sth;
    }

}
