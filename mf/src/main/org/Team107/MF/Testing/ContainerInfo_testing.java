package org.Team107.MF.Testing;


import org.Team107.MF.Ignore;
import org.Team107.MF.Message;
import org.Team107.MF.Messages.ContainerInfo;

import java.util.UUID;

/**
 * A class for testing the MF located in a visible package (see testing code)
 */
public class ContainerInfo_testing extends Message {

    private UUID containerID;

    public ContainerInfo_testing() {
        super(ContainerInfo_testing.class);
        this.containerID = UUID.randomUUID();
    }

    public ContainerInfo_testing(UUID id) {
        super(ContainerInfo_testing.class);
        this.containerID = id;
    }

    public UUID getContainerID() {
        return this.containerID;
    }


}
