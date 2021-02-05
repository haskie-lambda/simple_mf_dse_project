package org.Team107.MF;

import org.Team107.MF.Messages.ContainerInfo;
import org.Team107.MF.Testing.BasicMessage1;
import org.Team107.MF.Testing.BasicMessage2;
import org.Team107.MF.Testing.ContainerInfo_testing;
import org.Team107.MF.functionalbasics.Tuple;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * main test suite for the MF
 * @author Fabian Schneider
 */
public class MF_FunctionalityTest {

    /**
     * Testing whether the class detection using the package works as expected
     */
    @Test
    void AutomaticClassListCreationOk(){
        try {
            BasicComponent1 c = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 9000);
            assertEquals
                    (c.getTopics().stream()
                                    .map(clazz -> clazz.getCanonicalName())
                                    .collect(Collectors.joining(";"))
                    ,"org.Team107.MF.Testing.BoolMessage;org.Team107.MF.Testing.BasicMessage2;org.Team107.MF.Testing.BasicMessage1;org.Team107.MF.Testing.ContainerInfo_testing");
        } catch (Exception e){
            fail (e);
        }
    }

    /**
     * Testing whether initializing a component that initializes the MF with a null peer list
     * results in correct starting of the component.
     */
    @Test
    void FaultyComponentCorrectFunctionalityTest(){
        try {
            FaultyComponent f = new FaultyComponent();
        } catch (Exception e){
            fail(e);
        }
    }

    /**
     * Testing basic communication with one compenent
     */
    @Test
    void basicCommunication() {
        BasicComponent1 c = null;
        try {
            c = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8004);
            // testing serialization and deserialisation
            ContainerInfo_testing x = new ContainerInfo_testing();

            // sending a faulty message
            sendMsg("localhost", 8004, "testmsg");
            wait(200);
            if (c.messages.size() != 0) fail("parsing error not thrown");

            // sending a correct message
            String testStr = "this is a test";
            BasicMessage1 m = new BasicMessage1(testStr);
            sendMsg("localhost", 8004, m.toString());

            wait(200);
            if (c.messages.size() != 1
                    || c.messages.get(0).getClass() != BasicMessage1.class
                    || ((BasicMessage1) c.messages.get(0)).sth == testStr)
                fail("correct message retrieval falied: " + c.messages.get(0));


            // another correct message
            testStr = "this is a secondTest";
            BasicMessage2 m2 = new BasicMessage2(testStr);

            sendMsg("localhost", 8004, m2.toString());

            wait(200);
            if (c.messages.size() != 2
                    || c.messages.get(1).getClass() != BasicMessage2.class
                    || ((BasicMessage2) c.messages.get(1)).sth == testStr)
                fail("correct message retrieval falied: " + c.messages.get(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        } finally {
            if(c != null) {
                c.stop();
            }
        }

    }

    /**
     * Testing communications between two components
     */
    @Test
    void twoComponentCommunication() {
        BasicComponent1 c1 = null;
        BasicComponent1 c2 = null;
        try {
            c1 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8005);
            c2 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8006);

            // simple messaging
            String testStr = "this is a test";
            BasicMessage1 m = new BasicMessage1(testStr);
            sendMsg("localhost", 8005, m.toString());

            wait(200);
            if (c1.messages.size() != 1
                    || c1.messages.get(0).getClass() != BasicMessage1.class
                    || ((BasicMessage1) c1.messages.get(0)).sth == testStr)
                fail("correct message retrieval falied: " + c1.messages.get(0));
            if (c2.messages.size() != 0) fail("got message that i shoulnd't have gotten");


            // bridging of messages
            testStr = "bridging test";
            m = new BasicMessage1(testStr);
            // adding a peer to test bridging
            c1.addPeer("localhost", 8006);
            sendMsg("localhost", 8005, m.toString());

            wait(200);
            if (c1.messages.size() != 2
                    || c1.messages.get(1).getClass() != BasicMessage1.class
                    || ((BasicMessage1) c1.messages.get(1)).sth == testStr)
                fail("correct message retrieval falied: " + c1.messages.get(1));
            if (c2.messages.size() != 1
                    || c2.messages.get(0).getClass() != BasicMessage1.class
                    || ((BasicMessage1) c2.messages.get(0)).sth == testStr)
                fail("correct message retrieval failed: " + c2.messages.get(0));


            // testing that non existant links are not used for bridging
            testStr = "reverse Bridging negative test";
            m = new BasicMessage1(testStr);
            sendMsg("localhost", 8006, m.toString());

            wait(200);
            if(c2.messages.size() != 2
                    || c2.messages.get(1).getClass() != BasicMessage1.class
                    || ((BasicMessage1) c2.messages.get(1)).sth == testStr)
                fail("correct message retrieval fialed: " + c2.messages.get(1));
            if(c1.messages.size() != 2) fail("message bridged incorrectly");


            // runtime removal of handler test
            c1.removeHandler(BasicMessage2.class);
            BasicMessage2 msg2 = new BasicMessage2("not received message");
            sendMsg("localhost", 8005, msg2.toString());

            wait(200);
            if(c1.messages.size() != 2) fail("message received that shouldn't have been received");
            System.out.println("finished twoComponentCommunicationTest");
        } catch (Exception e){
            e.printStackTrace();
            fail(e);
        } finally {
            if(c1!=null) c1.stop();
            if(c2!=null) c2.stop();
        }
    }

    /**
     * testing communications between 4 components
     */
    @Test
    void multiComponentCommunication() {
        BasicComponent1 c1 = null;
        BasicComponent1 c2 = null;
        BasicComponent1 c3 = null;
        BasicComponent2 c4 = null;

        try {
            c1 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8007);
            c2 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8008);
            c3 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8009);
            c4 = new BasicComponent2(UUID.randomUUID(), new LinkedList<>(Arrays.asList()), 8010);

            // constructing a message line from c1 through c4
            c1.addPeer("localhost", 8008);
            c2.addPeer("localhost", 8009);
            c3.addPeer("localhost", 8010);

            // testing bridging via multiple components
            ContainerInfo_testing msg = new ContainerInfo_testing();
            sendMsg("localhost", 8007, msg.toString());
            wait(300);
            if(!c4.receivedContainerInfo.contentEquals(msg.getContainerID().toString())) fail("message not relayed properly");

            // testing runtime adding of handlers
            final List<String> s = new LinkedList<>();
            c1.addHandler(ContainerInfo_testing.class, (Message m)-> {
                try {
                    ContainerInfo_testing c = (ContainerInfo_testing) m;
                    s.add(c.getContainerID().toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            c4.addPeer("localhost", 8007);

            msg = new ContainerInfo_testing();
            sendMsg("localhost", 8007, msg.toString());

            wait(600);
        if(s.size()!=1 || !s.get(0).contentEquals(msg.getContainerID().toString())) fail("message bridging failed");
        } catch (Exception e) {
            e.printStackTrace();
            fail("something happened", e);
        } finally {
            if(c1 != null) c1.stop();
            if(c2 != null) c2.stop();
            if(c3 != null) c3.stop();
            if(c4 != null) c4.stop();
        }

    }

    /**
     * Testing the echo functionality of the network
     */
    @Test
    void EchoTest(){
        BasicComponent1 c1 = null;
        BasicComponent2 c2 = null;
        try {
            c1 = new BasicComponent1(UUID.randomUUID(), new LinkedList<>(), 8011);
            c2 = new BasicComponent2(UUID.randomUUID(), new LinkedList<>(), 8012);
            c1.addPeer("localhost", 8012);
            c2.addPeer("localhost", 8011);
            sendMsg("localhost", 8011, Echo.Request(UUID.randomUUID(), 100,""));
            wait(500);
            if(c1.receivedEcho.size()!=1 || c2.receivedEcho.size()!=1
                && c1.receivedEcho.get(0).contains("100") && c2.receivedEcho.get(0).contains("100")) fail("echo not working correctly");

            c1.send(Echo.Request(c1.myID, 0, ""));
            wait(500);
            if(c1.receivedEcho.size()!=2
                    || c1.receivedEcho.get(1).contains("100")) fail("echo2 not working correctly");
        } catch (MFInitializationException e) {
            e.printStackTrace();
            fail(e);
        } finally {
            if(c1 != null) c1.stop();
            if(c2 != null) c2.stop();
        }

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
     * org.Team107.MF.Message will be sent from localhost:8001
     *
     * @param host    The host to send the message to
     * @param port    The port to deliver the message to
     * @param message The message object to send
     */
    public static void sendMsg(String host, int port, Message message) {
        sendMsg(host, port, message.toString());
    }

    /**
     * Sends a message to the specified host and port
     * org.Team107.MF.Message will be sent from localhost:8001
     *
     * @param host    The host to send the message to
     * @param port    The port to deliver the message to
     * @param message The message object to send
     */
    public static void sendMsg(String host, int port, String message) {

        try (
                // constructing socket
                Socket echoSocket = new Socket(host, port);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
        ) {
            // sending message
            String msg = message;
            System.out.println("sending msg: " + msg);
            out.println(msg);
            System.out.println("message sent");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    host);
            System.exit(1);
        }
    }

    /**
     * Component that initializes the MF with a null peer object
     */
    public class FaultyComponent extends MF{
        public FaultyComponent() throws MFInitializationException {
            super((List<Tuple<String, Integer>>) null, ContainerInfo_testing.class.getPackage(), 9000);
            listen();
        }

        @Override
        public Echo getMyInformation() {
            return Echo.Response(UUID.randomUUID(), 0, "");
        }
    }

    /**
     * Component with a handler for both basic messages, the echo messages and accessible message stores for testing;
     */
    public class BasicComponent1 extends MF {
        private int id = 0;
        private UUID myID = UUID.randomUUID();
        private UUID containerID; // ID of the simulated container
        private List<Object> messages = new LinkedList<>();

        List<String> receivedEcho = new LinkedList<>();
        /**
         * Constructor for a simulated container
         *
         * @param peers      A list of network peers in the form of [(hostname, port number),...]
         * @param portNumber The port number the simulated container should be reachable at
         */
        public BasicComponent1(UUID uuid, List<Tuple<String, Integer>> peers, int portNumber) throws MFInitializationException {
            super(peers, ContainerInfo_testing.class.getPackage(), portNumber);
            this.myID = uuid;
            this.containerID = UUID.randomUUID();

            // handling messages interesting for the simulated component.
            this.addHandler(BasicMessage1.class,
                    (Message msg) -> {

                            BasicMessage1 c = (BasicMessage1) msg; //JSONDeserializer.parseMessage(BasicMessage1.class, msg.toString());
                            messages.add(c);
                    });
            // handling messages interesting for the simulated component.
            this.addHandler(BasicMessage2.class,
                    (Message msg) -> {

                            BasicMessage2 c = (BasicMessage2) msg;// JSONDeserializer.parseMessage(BasicMessage2.class, msg.toString());
                            messages.add(c);
                    });
            this.addHandler(Echo.class,
                    (Message msg) -> {
                        Echo e = (Echo) msg;
                        if (!e.isRequest()) receivedEcho.add(msg.toString());
                    });
            listen();
        }

        @Override
        public Echo getMyInformation() {
            return Echo.Response(myID, id, "");
        }
    }

    /**
     * Externalized network message class specification for @see BasicComponent2
     */
    static List<Class<? extends Message>> VALID_MESSAGES = new LinkedList<>();
    static{
        VALID_MESSAGES.add(BasicMessage1.class);
        VALID_MESSAGES.add(BasicMessage2.class);
        VALID_MESSAGES.add(ContainerInfo_testing.class);
    }

    /**
     * exemplary basic component for testing with handlers for {@see ContainerInfo_testing} and {@see Echo}
     * @link VALID_MESSAGES
     */
    public class BasicComponent2 extends MF {
        private int id = 1;
        private UUID myID;
        private UUID containerID; // ID of the simulated container
        String receivedContainerInfo = "";
        List<String> receivedEcho = new LinkedList<>();
        public BasicComponent2(UUID uuid, List<Tuple<String, Integer>> peers, int portNumber) throws MFInitializationException {
            super(peers, VALID_MESSAGES, portNumber);
            this.myID = uuid;
            this.containerID = UUID.randomUUID();

            // handling messages interesting for the simulated component.
            this.addHandler(ContainerInfo_testing.class,
                    (Message msg) -> {

                            ContainerInfo_testing c = (ContainerInfo_testing) msg; //JSONDeserializer.parseMessage(ContainerInfo_testing.class, msg.toString());
                            receivedContainerInfo = c.getContainerID().toString();
                    });
            this.addHandler(Echo.class,
                    (Message msg) -> {
                        Echo e = (Echo) msg;
                        if (!e.isRequest()) receivedEcho.add(msg.toString());
                    });
            listen();

        }

        @Override
        public Echo getMyInformation() {
            return Echo.Response(myID, id, "");
        }
    }

}
