package org.Team107.MF;

import java.util.UUID;

/**
 * Scaffold org.Team107.MF.Message
 * This is not used in the network, but can be used as a simple
 * scaffold to create network message classes with minimum effort
 *
 * @author Fabian Schneider
 */
public class Echo extends Message {

    private UUID uuid;
    private Integer id;
    private String otherInfo;
    private String request;

    /**
     * Nullary constructor for Deserializer
     */
    private Echo() {
        super(Echo.class);
    }

    /**
     * Creates an Echo.Request
     */
    public static Echo Request(UUID uuid, Integer id, String otherInfo){
        return new Echo(uuid, id, otherInfo, true);
    }

    /**
     * Creates an Echo.Response
     */
    public static Echo Response(UUID uuid, Integer id, String otherInfo){
        return new Echo(uuid, id, otherInfo, false);
    }

    /**
     *
     * @param uuid the echo UUID
     * @param id the echo ID (semanticts application defined)
     * @param otherInfo other information to spread (semantics application defied)
     * @param request whether it should be a request or nota
     */
    private Echo(UUID uuid, Integer id, String otherInfo, Boolean request) {
        super(Echo.class);
        this.uuid = uuid;
        this.id = id;
        this.otherInfo = otherInfo;
        this.request = request?"true":"false";
    }


    //-------- GETTERS --------------

    public UUID getUuid() {
        return uuid;
    }

    public Integer getId() {
        return id;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public Boolean isRequest() {
        return request.contentEquals("true");
    }
}
