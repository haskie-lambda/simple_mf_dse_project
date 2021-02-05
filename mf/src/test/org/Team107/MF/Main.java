package org.Team107.MF;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.Team107.MF.Testing.ContainerInfo_testing;
import org.Team107.MF.functionalbasics.Tuple;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Class for manual testing of the Messaging Framework.
 * This contains no important logic to the Network itself and should therefore not be deployed with it!
 * @see MF_FunctionalityTest
 * @author Fabian Schneider
 */
class Main {

    /**
     * Manual testing procedure during development.
     * You are probably looking for MF_FunctionalityTest
     * @see MF_FunctionalityTest
     * @param args Commandline arguments
     */
    public static void main(String... args) throws MFInitializationException {
        // creating testing containers
        System.out.println("setting up");
        final Container c =
                new Container(new LinkedList<>(Arrays.asList(new Tuple<String, Integer>("localhost", 8002))), 8001);
        final Container c2 =
                new Container(new LinkedList<>(Arrays.asList(new Tuple<String, Integer>("localhost", 8001))), 8002);

        // testing serialization and deserialisation
        ContainerInfo_testing x = new ContainerInfo_testing();
        System.out.println(x.toString());

        try {
            ContainerInfo_testing x2 = JSONDeserializer.parseMessage(ContainerInfo_testing.class, x.toString());
            System.out.println(x2.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // testing message sending and bridging
        System.out.println("simulating wait");
        wait(1000);
        //sendMsg("localhost", 8001, "testmsg");
        UUID test = UUID.randomUUID();
        sendMsg("localhost", 8002, String.format(
                "{ \"messageID\": \"%s\"" +
                        ", \"topic\": \"%s\"" +
                        ", \"data\": { " +
                        "\"containerID\": \"%s\" } }"
                , test, "org.Team107.MF.ContainerInfo", test));

        wait(1000);
        sendMsg("localhost", 8001, new ContainerInfo_testing());
        //while(true);
    }

    /**
     * Convenience method for sleeping
     *
     * @param i How many seconds to sleep
     */
    public static void wait(int i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {
        }
    }

    /**
     * Sends a message to the specified host and port
     * org.Team107.MF.Message will be sent from host:port
     *
     * @param host    The host to send the message to
     * @param port    The port to deliver the message to
     * @param message The message object to send
     * {@link #sendMsg(String host, int port, String message)}
     */
    public static void sendMsg(String host, int port, Message message) {
        sendMsg(host, port, message.toString());
    }

    /**
     * Sends a message to the specified host and port
     * org.Team107.MF.Message will be sent from host:port
     *
     * @param host    The host to send the message to
     * @param port    The port to deliver the message to
     * @param message The message object to send
     */
    public static void sendMsg(String host, int port, String message) {
        System.out.println("sending message");

        try (
                // constructing socket
                Socket echoSocket = new Socket(host, port);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
        ) {
            // sending message
            String msg = message;
            out.println(msg);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    host);
            System.exit(1);
        }
    }
}
