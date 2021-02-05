package org.Team107.MF.Messages;

import org.Team107.MF.Ignore;
import org.Team107.MF.Message;


import java.util.UUID;

/**
 * Scaffold org.Team107.MF.Message
 * This is not used in the network, but can be used as a simple
 * scaffold to create network message classes with minimum effort
 *
 * @author Fabian Schneider
 */
public class Empty extends Message {


    /**
     * Nullary constructor for Deserializer
     */
    private Empty() {
        super(Empty.class);
    }

    /**
     * @param id
     */
    public Empty(UUID id) {
        super(Empty.class);
    }

}
