package org.Team107.MF;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for deserializing JSON network objects to org.Team107.org.Team107.MF.MF.org.Team107.MF.Messages POJOs
 *
 * @author Fabian Schneider
 */
public class JSONDeserializer {

    /**
     * Uses regex to extract a number value from a JSON string with the key key.
     *
     *
     * @param type
     * @param key The key the number is bound to in the JSON String.
     * @param s   The string to be searched in.
     * @return Returns the string representation of the number that was found in the JSON String.
     * @throws ParseException Thrown when the key could not be found
     */
    private static String getNumber(Class<?> type, String key, String s) throws ParseException {
        Pattern p;
        if(type.equals(Boolean.class)){
            p = Pattern.compile(
                            String.format("\"%s\" *: *(true|false)", key));
        } else {
            p = Pattern.compile(
                    String.format("\"%s\" *: *((-?\\d+(\\.d+)?))", key));
        }
        Matcher m = p.matcher(s);
        if (m.find()) {
            if(type.equals(Boolean.class)){
                return m.group(1);
            } else {
                return m.group(m.groupCount() - 1);
            }
        } else {
            throw new ParseException("number parsing failed: " + s, 0);
        }
    }

    /**
     * Uses regex to extract a string formatted as specified by pattern
     * from a JSON string with the key key.
     *
     * @param key     The key the string is bound to in the JSON String.
     * @param pattern The pattern the searched for String should conform to.
     * @param s       The string to be searched in.
     * @return Returns the string without quotation marks.
     * @throws ParseException Thrown when the key could not be found
     */
    private static String getString(String key, String pattern, String s) throws ParseException {
        Pattern p = Pattern.compile(
                String.format("\"%s\" *: *\"(%s)\"", key, pattern));
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new ParseException("string parsing failed: " + s, 0);
        }
    }

    /**
     * Uses regex to extract a JSON Object (string) from a JSON string
     * with the key key.
     *
     * @param key The key the object is bound to in the JSON String.
     * @param s   The string to be searched in.
     * @return Returns the JSON Object as a string;
     * Note that the Object is not guaranteed to be valid.
     * @throws ParseException Thrown when the key could not be found.
     */
    private static String getObject(String key, String s) throws ParseException {
        Pattern p = Pattern.compile(
                String.format("\"%s\" *: *(\\{[^\\}]*\\})", key));
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new ParseException("object parsing failed: " + s, 0);
        }
    }

    /**
     * Parses a UUID value bound to tag from a JSON String.
     *
     * @param tag The tag the UUID valued is bound to
     * @param s   The JSON string to be searched in
     * @return Returns the UUID string as parsed UUID
     * @throws ParseException Thrown when the key was not found
     * @see UUID.fromString(String uuid);
     */
    private static UUID parseUuid(String tag, String s) throws ParseException {
        // from https://ihateregex.io/expr/uuid/
        String uuidMatch = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
        // end copy
        String uuid = getString(tag, uuidMatch, s);
        return UUID.fromString(uuid);
    }

    /**
     * Retrieves the topic of a network message contained in the "topic" JSON field.
     *
     *
     * @param s The network message to be searched in.
     * @return Returns the Topic of the message or Topic.ErrorMessage
     * if the key was not found or the Topic string was not valid.
     */
    private static Class getMessageTopic(String s) throws ParseException {
        try {
            return Class.forName(getString("topic", "[a-zA-Z0-9_\\.]+", s));
        } catch (ClassNotFoundException e) {
            throw new ParseException(e.getMessage(),0);
        }
    }

    /**
     * Reconstructs a network message into an existing object.The objects values are set to the messages
     * corresponding values.
     * Assumes that the org.Team107.MF.Message to be reconstructed contains a (public|private|protected)
     * nullary constructor.
     *
     * @param <A> The type of the org.Team107.MF.Message to be reconstructed.
     * @param a   An existing object that should be updated with the message contents.
     * @param s   The network message; can e.g. be obtained from an Abstract org.Team107.MF.Message
     * @return returns a reference to the given object whose values have been altered.
     * @throws ParseException Thrown, when a tag corresponding to a message
     *                        JSON tag could not be found.
     * @see AbstractMessage.toString();
     * @see parseMessage(Class<A> a, Class<TOPIC> t, String s)
     *//*
    public static <A extends Message> A parseMessage(Class<A> a, String s) throws ParseException {
        try {
            Constructor<A>[] constrs = (Constructor<A>[]) a.getDeclaredConstructors();      // retrieve all constructors
            for (Constructor<A> con : constrs) {
                if (con.getParameterCount() ==
                        0) {                                             // get nullary constructor
                    con.setAccessible(
                            true);                                                // set accessible if private
                    Constructor<A> nullaryConstructor = con;
                    A inst =
                            nullaryConstructor.newInstance();                              // instantiating a new
                    return (A) parseMessage(a, inst.getTopic().getClass(), s);
                }
            }
            throw new ParseException("no nullary constructor for message", 0);
        } catch (InstantiationException e) {
            throw new ParseException("message could not be instantiated", 0);
        } catch (IllegalAccessException e) {
            throw new ParseException("message constructor not available", 0);
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            throw new ParseException("message not constructable", 0);
        }
    }
    */
    /**
     * Reconstructs a network message into an existing object.The objects values are set to the messages
     * corresponding values.
     * Assumes that the org.Team107.MF.Message to be reconstructed contains a (public|private|protected)
     * nullary constructor.
     * This method is only used in this package because org.Team107.MF.AbstractMessage is parametrized over the topic enum
     * which results in the type of topic being object and the getTopic method being unable to reconstruct
     * values from the enum. Explicitly passing the Enum solves this problem.
     *
     * @param <A> The type of the org.Team107.MF.Message to be reconstructed.
     * @param a   An existing object that should be updated with the message contents.
     * @param ts   The topic Enum to use for deserialization
     * @param s   The network message; can e.g. be obtained from an Abstract org.Team107.MF.Message
     * @return returns a reference to the given object whose values have been altered.
     * @throws ParseException Thrown, when a tag corresponding to a message
     *                        JSON tag could not be found.
     * @see AbstractMessage.toString();
     */
    static <A extends Message> A parseMessage(Class<A> a, String s) throws ParseException {
        try {
            Constructor<A>[] constrs = (Constructor<A>[]) a.getDeclaredConstructors();      // retrieve all constructors
            for (Constructor<A> con : constrs) {
                if (con.getParameterCount() ==
                        0) {                                             // get nullary constructor
                    con.setAccessible(
                            true);                                                // set accessible if private
                    Constructor<A> nullaryConstructor = con;
                    A inst =
                            nullaryConstructor.newInstance();                              // instantiating a new
                    // message
                    List<Field> fields = JSONSerializer.getFields(inst);
                    for (Field f : fields) {                                                  // set its' fields
                        deserialize(inst, f, s);
                    }
                    return inst;                                                            // return the object if
                    // everything went fine
                }
            }
            throw new ParseException("no nullary constructor for message", 0);
        } catch (InstantiationException e) {
            throw new ParseException("message could not be instantiated", 0);
        } catch (IllegalAccessException e) {
            throw new ParseException("message constructor not available", 0);
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            throw new ParseException("message not constructable", 0);
        }
    }

    private static String toNoun(String a){
        return Character.toUpperCase(a.charAt(0)) + a.substring(1);
    }

    /**
     * Reconstructs a specific field of the existing object using the java reflection API.
     * Works for Object fields of primitive types.
     * Works for Complex built-in and user defined types by recursively deserializing values.
     * Does not work for Collections right now.
     * Special treatment for
     * UUID.class: is reconstructed as normal UUID String, not UUIDs internal representation
     * Topic.class: reconstructed using Topic.fromString()
     * JSON tag "data": reconstructed as String variable named data containing the Object as a String (used for
     * org.Team107.MF.AbstractMessage)
     * JSON tag "topic": reconstructed as topic ENUM corresponding to the message type variable
     *
     * @param <A> The type of the org.Team107.MF.Message to be reconstructed.
     * @param a   An existing object that should be updated with the message contents.
     * @param f   The Objects field to be reconstructed
     * @param s   The network message; can e.g. be obtained from an Abstract org.Team107.MF.Message
     * @return Returns a reference to the altered object.
     * @throws ParseException Thrown, when a tag corresponding to a message
     *                        JSON tag could not be found. The value will remain unchanged.
     *                        Also thrown when reflection could not access the variable in the class to be deserialized.
     * @see parseMessage(A a, String s)
     */
    private static <A> A deserialize(A a, Field f, String s) throws ParseException {

        Class abstractestClassBeforeObject = Object.class;
        if (a.getClass() != null) abstractestClassBeforeObject = a.getClass();
        while(abstractestClassBeforeObject.getSuperclass() != null && !abstractestClassBeforeObject.getSuperclass().equals(Object.class)){
            abstractestClassBeforeObject = abstractestClassBeforeObject.getSuperclass();
        }

        f.setAccessible(true);
        try {
            if (JSONSerializer.PRIMITIVES_NUMERIC.contains(f.getType())) {               // Base case numeric primitives
                Class<?> primClass = f.getType();
                if(f.getType().getName().toLowerCase().contentEquals(f.getType().getName())){
                    primClass = Class.forName("java.lang." + toNoun(f.getType().getName()));
                }
                String num = getNumber(f.getType(), f.getName(), s);
                f.set(a, primClass.getMethod("valueOf", new Class[]{String.class}).invoke(null, num));
            } else if (JSONSerializer.PRIMITIVES_NON_NUMERIC.contains(f.getType())) {   // Base case non numeric primitives
                if (f.getType() == UUID.class) {                            // UUID reconstruction
                    f.set(a, parseUuid(f.getName(), s));
                } else if (f.getName().equals("data")) {                 // "data" reconstruction
                    f.set(a, getObject(f.getName(), s));
                } else {                                                // string reconstruction
                    f.set(a, getString(f.getName(), "[^\"]*", s));
                }
            } else if (f.getName().equals("topic")) {                     // topic reconstruction
                f.set(a, getMessageTopic(s));
            } else if (abstractestClassBeforeObject.equals(AbstractCollection.class)){
                f.set(a, getCollection(s,a));
            }else {                                                    // recursively reconstruct complex type
                for (Field sub : JSONSerializer.getFields(f.getType())) {
                    sub.set(f.get(a), deserialize(a, f, s));
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            throw new ParseException("Deserialization of message failed", 0);
        }
        return a;
    }

    private static <A> Collection getCollection(String s, A a) {
        LinkedList l = new LinkedList<>();


        return l;
    }


}
