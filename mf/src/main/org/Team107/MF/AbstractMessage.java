package org.Team107.MF;

/**
 * A Wrapper for network messages and for partial deserialization.
 *
 * @author Fabian Schneider
 */
public class AbstractMessage extends Message {

    private String data;
            // the data contained in the actual message; set only by the deserializer

    /**
     * Nullary constructor for the Deserializer
     */
    public AbstractMessage() {
        super();
    }

    /**
     * Returns the message data
     *
     * @return Returns the message data as JSON String (validity of JSON is not guaranteed)
     */
    public String getData() {
        return data;
    }

}
