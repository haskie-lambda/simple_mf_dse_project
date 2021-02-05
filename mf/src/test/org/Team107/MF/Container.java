package org.Team107.MF;

import org.Team107.MF.Messages.ContainerInfo;
import org.Team107.MF.Testing.ContainerInfo_testing;

import org.Team107.MF.functionalbasics.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A Class for simulating a simple network component.
 * This is for manual testing only and should not be deployed!
 *
 * @author Fabian Schneider
 */
public class Container extends MF{

    private UUID containerID; // ID of the simulated container
    List<ContainerInfo_testing> messages = new LinkedList<>();

    /**
     * Constructor for a simulated container
     *
     * @param peers      A list of network peers in the form of [(hostname, port number),...]
     * @param portNumber The port number the simulated container should be reachable at
     */
    public Container(List<Tuple<String, Integer>> peers, int portNumber) throws MFInitializationException {
        super(peers, Package.getPackage("org.Team107.MF"), portNumber);
        this.containerID = UUID.randomUUID();

        // handling messages interesting for the simulated component.
        this.addHandler(ContainerInfo.class,
                (Message msg) -> {
                        // parse message
                        ContainerInfo_testing c = (ContainerInfo_testing) msg;
                        // simply print it for testing

                        System.out.println("org.Team107.MF.Container " + containerID.toString() + " got ContainerInfoID:" +
                                c.getContainerID().toString());
                        System.out.println(c.toString());
                        messages.add(c);
                });
        listen();
    }

    @Override
    public Echo getMyInformation() {
        return Echo.Response(containerID, 0, "");
    }
}
