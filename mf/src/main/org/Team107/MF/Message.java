package org.Team107.MF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Network org.Team107.MF.Messages Wrapper
 *
 * @author Fabian Schneider
 */
public abstract class Message {

    private UUID messageID;         // the message ID used for loop prevention
    private Class topic;

    //@org.Team107.MF.Ignore
    //private Class<ENUM> ENUM;

    /**
     * Constructs a org.Team107.MF.Message with a random UUID
     *
     * @param ENUM The Topic enum class the message belongs to
     * @see UUID.randomUUID()
     */
    public Message(Class topic) {
        this.messageID = UUID.randomUUID();
        this.topic = topic;
    }
    
    Message(){}

    /**
     * Returns the org.Team107.MF.Message id
     *
     * @return the message UUID
     */
    public UUID getMessageID() {
        return this.messageID;
    }

    ;

    /**
     * Every network message needs to have a Topic for deserialization purposes
     *
     * @return The topic of the message
     * @see JSONDeserializer.parse
     */
    public String getTopic() {
        return topic.getSimpleName();
    }

    /**
     * Retrieves the topic as the class it represents;
     * used in the MF
     * @return The Topic Class of the Message
     */
    public Class<Message> getTopicClass(){
        return (Class<Message>) topic;
    }

    @Override
    public String toString() {
        return JSONSerializer.serialize(this, Arrays.asList(topic));
    }

}
