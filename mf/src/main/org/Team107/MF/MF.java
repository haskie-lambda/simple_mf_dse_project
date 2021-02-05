package org.Team107.MF;

import org.Team107.MF.functionalbasics.Tuple;
import org.Team107.MF.functionalbasics.UnaryHandler;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Messaging Framework for the Project
 * Provides implementing classes means of communicating in a distributed
 * network using bridging and subscribing to messages of certain topics using callback functions.
 * Implements loop prevention using message UUIDs.
 *
 * @author Fabian Schneider
 */
public abstract class MF {

    boolean verbose = false;                                        // for debugging 
    private Map<Class, UnaryHandler<Message>> topicHandlers;        // callaback handlers
    private List<UUID> seenIDs;                                     // loop prevention id store
    private Collection<Tuple<String, Integer>> peers;               // network peers in the format [("host", port),...]
    private int portNumber;                                         // port number the service should be available at
    private Collection<Class<? extends Message>> topics;            // a list of available message classes to be received and sent6
    private Thread listener = null;                                 // network listener
    private boolean stop = false;

    /**
     * Standard Constructor
     * @param peers the immediately known peers of the component; may be an empty list.
     * @param messages a list of message classes to be available for sending and listening
     * @param portNumber the port number the MF should listen on
     * @throws MFInitializationException thrown when the message classes could not be found or construction of the Topics enum used for deserialization failed.
     */
    public MF(Collection<Tuple<String,Integer>> peers, Collection<Class<? extends Message>> messages, int portNumber) throws MFInitializationException {
        this.peers = peers;
        if(this.peers == null) this.peers = new LinkedList<>();
        this.topics = messages;
        topicHandlers = new HashMap<>();
        seenIDs = new LinkedList<>();
        this.portNumber = portNumber;
    }

    /**
     * Minimal constructor after which the MF is not in a working state.
     * Some Spring Components need this though.
     * @param messages a package containing the messages that should be available.
     * @throws MFInitializationException thrown when the message classes could not be found or construction of the Topics enum used for deserialization failed.
     */
    public MF(Package messages) throws MFInitializationException {
        this(new LinkedList<>(), messages, -1);
    }

    /**
     * Super constructor for messaging components
     *
     * @param peers      The entities network peers
     * @param portNumber The entities port number where it should be reachable
     * @throws MFInitializationException thrown when the topic enum could not be created for some reason
     */
    public MF(List<Tuple<String, Integer>> peers, Package messagePackage, int portNumber) throws MFInitializationException {
        this(peers, new LinkedList<>(), portNumber);
        try {
            this.topics =  getClassesInPackage((messagePackage.getName()));
        } catch (IOException | URISyntaxException | InterruptedException | ClassNotFoundException e) {
            throw new MFInitializationException(e);
        }
    }

    /**
     * Only used for debugging
     * non blocking printing of topics
     * @deprecated Should only be used for debugging and manual testing; not in production code
     */
    public void printTopics(){
        new Thread(() -> {
            for (Class e : topics) {
                System.out.println(e.getCanonicalName());
            }
        }).start();
    }

    /**
     * retrieve the topic class list
     * @return a list of topic classes
     */
    public Collection<Class<? extends Message>> getTopics(){
        Collection<Class<? extends Message>> l = new LinkedList<>();
        l.addAll(topics);
        return l;
    }

    /**
     * generates a ENUM of package names
     * this is used by `addGeneralHandler` to subscribe to all messages
     * that can be reconstructed based on the available message classes in the message package.
     * Basically ensures that every message coming in is only handed to the component if it is reconstructable
     * and therefore adheres to the JSON messaging standard.
     *
     *  modified from https://stackoverflow.com/questions/1456930/how-do-i-read-all-classes-from-a-java-package-in-the-classpath/7461653#7461653
     *
     * @param pkg the package containing message classes (classes extending the Message class)
     * @return returns the class description of a dynamically created and already loaded Java ENUM class
     * @throws IOException thrown when the folder location or the JAR containing the messages does not exist or is protected
     * @throws URISyntaxException thrown when the URI of one of the class files is incorrect
     * @throws ClassNotFoundException thrown when a class cannot be loaded (e.g. due to version conflicts
     * @throws InterruptedException thrown when the compilation process of the enum is interrupted
     */
    static public List<Class<? extends Message>> getClassesInPackage(String pkg) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        URL packageURL = Thread.currentThread().getContextClassLoader().getResource(pkg.replace(".","/"));
        new File(packageURL.getPath() + File.separator + "Topicc.java").delete();
        new File(packageURL.getPath() + File.separator + "Topicc.class").delete();

        String basePath;

        List<Class<? extends Message>> messages = new LinkedList<>();

        if(packageURL.getProtocol().equals("jar")){
            String jarFileName = packageURL.getPath().substring(5);
            jarFileName = jarFileName.substring(0,jarFileName.lastIndexOf("!"));
            JarFile jf ;
            Enumeration<JarEntry> jarEntries;
            String entryName;

            File f = new File(jarFileName);
            basePath = f.getParent();

            // build jar file name, then loop through zipped entries
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while(jarEntries.hasMoreElements()){
                entryName = jarEntries.nextElement().getName();
                //System.out.println(entryName);
                if(entryName.startsWith(pkg.replace(".","/")) && entryName.endsWith(".class")){
                    entryName = entryName.replace(".class", "").replace("/","."); //.substring(pkg.length(),entryName.lastIndexOf('.'));

                    Class<?> c = Class.forName(entryName);
                    if(c.getSuperclass()!=null) {
                        if (c.getSuperclass().getName().contentEquals("org.Team107.MF.Message")) {
                            messages.add((Class<? extends Message>) c);
                        }
                    }
                }
            }

            // loop through files in classpath
        }else{
            URI uri = new URI(packageURL.toString());
            File folder = new File(uri.getPath());
            basePath = packageURL.getPath();
            // won't work with path which contains blank (%20)
            // File folder = new File(packageURL.getFile());
            File[] contenuti = folder.listFiles();
            String entryName;
            for(File actual: contenuti) {
                try {
                    entryName = actual.getName();
                    entryName = entryName.substring(0, entryName.lastIndexOf('.'));

                    Class<?> c = Class.forName(pkg + "." + entryName);
                    Class x = c.getSuperclass();
                    if (c.getSuperclass().getName().contentEquals("org.Team107.MF.Message")) {
                        messages.add((Class<? extends Message>) c);
                    }
                } catch (Exception e){
                    //skip
                }
            }
        }

        return messages;
    }

    /**
     * starts the network listener
     * this will run in a background thread, waiting for messages to arrive.
     * if the message has not already been seen, the messsage will be partly
     * deserialized to an org.Team107.MF.AbstractMessage using the org.Team107.MF.JSONDeserializer.
     * Subsequently the appropriate (according to the deserialized message topic)
     * user set handler will be invoked along with the AbscractMessage.
     *
     * @see AbstractMessage
     * @see JSONDeserializer
     */
    public void listen() {
        if(this.portNumber<1) throw new RuntimeException("port is faulty; has to be larger than 0 " + this.portNumber);
        listener = listener != null ? listener : new Thread(     //for not constructing it over and over again
                () -> {
                    try {
                        // setting up the listening socket
                        ServerSocket serverSocket = new ServerSocket(portNumber);
                        // wait for a peer to connect, exctract the message and then start waiting again
                        while (!stop) {
                            //System.out.println("message");
                            try {
                                // wait for peer to connect and accept
                                //System.out.println("opening client socket");
                                Socket clientSocket = serverSocket.accept();
                                BufferedReader in = new BufferedReader(
                                        new InputStreamReader(clientSocket.getInputStream()));
                                // read message
                                String msg = "";
                                String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    msg += inputLine;
                                }
                                //System.out.println("received message: " + msg);
            
                                in.close();
                                clientSocket.close();
                                // deserialize message
                                // `(new org.Team107.MF.AbstractMessage<ENUM>()).getClass()` used to pass the ENUM information to the deserializer
                                AbstractMessage msgo = JSONDeserializer.parseMessage((new AbstractMessage()).getClass(), msg);
                                // loop prevention
                                if (!seenIDs.contains(msgo.getMessageID())) {
                                    seenIDs.add(msgo.getMessageID());
                                    // invoke handler
                                    receiveHandler(msgo);
                                }
                            } catch (IOException e) {
                                // thrown on socket error
                                // handled by try again
                                System.out.println("Exception caught when trying to listen on port "
                                        + portNumber + " or listening for a connection");
                                System.out.println(e.getMessage());
                            } catch (ParseException e) {
                                // thrown for malformed messages
                                // ignored
                                System.out.println("malformed message received:");
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (IOException io) { // socket error
                        System.err.println("server could not be created");
                        io.printStackTrace();
                    }
                });
        listener.start();
    }

    /**
     * Simple procedure for stopping the listener
     */
    public void stop() {
        stop = true;
    }

    /**
     * Adds a peer to the peer list;
     * works also if the listener is already running
     *
     * @param host the host where the peer is running
     * @param port the port the peer is listening on
     */
    public void addPeer(String host, Integer port) {
        peers.add(new Tuple<>(host, port));
    }

    /**
     * Subscribes to a Messaging Topic with a callback function
     * Works also if the listener is already running
     *
     * @param t The topic to subscribe to
     * @param f The callback function to invoke when a message of appropriate type arrives
     */
    public void addHandler(Class t, UnaryHandler<Message> f) {
        topicHandlers.put(t, f);
    }

    /**
     * Subscribes to all Messaging Topics with a callback function.
     * Also works if the listener is running already.
     * Simply adds the same listener for all toerializatiopics.
     * using addHandler after this will override the handler for a specific topic.
     *
     * @param f The callback function to invoke when any parseable message arrives
     */
    public void addGeneralHandler(UnaryHandler<Message> f) {
        for(Class<? extends Message> e : topics){
            try {
                addHandler(e,f); //Class.forName(msgPackage.getName() + "." + e.toString()), f);
            } catch(Exception ex){
                ex.printStackTrace();
                System.err.println("probably the message package was specified wrongly. Try `SomeMessage.class.getPackage()`");
                // can de disregraded
            }
        }
    }

    /**
     * Unsubscribes from a messaging topic
     * Also works if the listener is already running.
     *
     * @param t The topic to unsubscribe from.
     */
    public void removeHandler(Class t) {
        topicHandlers.remove(t);
    }

    /**
     * Unsubscribes from all messaging topics
     * Also works if the listener is already running.
     */
    public void removeAllHandlers() {
        topicHandlers.clear();
    }

    /**
     * Sends a message into the distributed network
     * without ensuring delivery.
     * @param m The message to send
     */
    public void send(Message m){
        send(m, false);
    }

    public Collection<Tuple<String, Integer>> getPeers(){
        return peers;
    }
    /**
     * Sends a message into the distributed network
     *
     * @param m The message to send
     * @param reliable if message delivery should be reliable (true) or not (false)
     */
    public void send(Message m, boolean reliable) {
        // send message to each peer
        seenIDs.add(m.getMessageID());
        new Thread(()-> {
            for (Tuple<String, Integer> peer : peers) {
                final String host = String.valueOf(peer.first());
                final Integer port = Integer.valueOf(peer.second());
                new Thread(()->{
                    boolean delivered = false;
                    if(verbose) System.out.println("sending message to " + host + " " + port);
                    do {
                        try (
                            // open sending socket
                            Socket s = new Socket(host, port);
                            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        ) {
                            // send message
                            out.println(m.toString());
                            delivered = true;
                        } catch (UnknownHostException e) {
                            // host problem, can be ignored;
                            if (verbose) System.err.println("Don't know about host " + host + " " + port);
                            try{Thread.sleep(1000);} catch (Exception ex){}
                        } catch (IOException e) {
                            // connection error
                            // can be ignored, maybe right now not online
                            if (verbose) System.err.println("Couldn't get I/O for the connection to " + host + " " + port);
                            if (verbose) e.printStackTrace();


                            try{Thread.sleep(1000);} catch (Exception ex){}
                        }

                    } while(reliable && !delivered); // this is a bug;
                    if(verbose) System.out.println("message sending to " + host + " " + port + " done");
                }).start();
            }
        }).start();
    }

    /**
     * Handles incoming AbstractMessages
     * - bridges the message to peers
     * - invokes the corresponding handler or ignores the message
     *
     * @param m The message to handle
     */
    private void receiveHandler(AbstractMessage m) {
        //System.out.println("received " + m.getMessageID().toString());
        bridge(m);
        //System.out.println(Echo.class.getCanonicalName());
        //System.out.println(m.getTopicClass().getCanonicalName());
        if(m.getTopicClass().getCanonicalName().contentEquals(Echo.class.getCanonicalName())) {
            try {
                Echo e = JSONDeserializer.parseMessage(Echo.class, m.toString());
                if(e.isRequest()){
                    send(getMyInformation());

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        try {
            String className = m.getTopicClass().getCanonicalName().contentEquals(Echo.class.getCanonicalName())
                                ? Echo.class.getCanonicalName()
                                : m.getTopicClass().getCanonicalName();
            Class<Message> msgClass = (Class<Message>) Class.forName(className);
            //System.out.println("invoking");
            if(topicHandlers.containsKey(m.getTopicClass())) {
                topicHandlers.get(m.getTopicClass()).run(JSONDeserializer.parseMessage(msgClass, m.toString()));
            }
            //System.out.println("finished");
        } catch (Exception e) {
            System.err.println("message deserialization failed for " + m.toString());
            System.err.println(e.toString());
            e.printStackTrace();
            // no handler, ignore message
        }
    }

    /**
     * Bridges the message to the enities peers
     *
     * @param m The message to be bridged
     */
    private void bridge(Message m) {
        send(m);
    }

    /**
     * encodes information about the component for the networks echo responses
     * @return an Echo.Respond with the corresponding information
     */
    public abstract Echo getMyInformation();

    // debugging settings
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Sets the port number the MF should listen to
     * @param portNumber the port the MF should use to listen on; this must be greater than 0.
     * @throws RuntimeException when the portNumber is smaller than 1.
     */
    public void setPortNumber(int portNumber) {
        if(portNumber<1) throw new RuntimeException("port must be greater than 0");
        this.portNumber = portNumber;
    }
}
