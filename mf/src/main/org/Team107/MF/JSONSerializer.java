package org.Team107.MF;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Class for serialising POJOs to JSON strings
 *
 * @author Fabian Schneider
 */
public class JSONSerializer {

    // @see edited for our needs from https://www.baeldung.com/java-object-primitive-type
    // List of numeric primitiye types including void.
    static final List<Class<?>> PRIMITIVES_NUMERIC;

    static {
        PRIMITIVES_NUMERIC = new ArrayList<Class<?>>(16);
        PRIMITIVES_NUMERIC.add(Integer.class);
        PRIMITIVES_NUMERIC.add(int.class);
        PRIMITIVES_NUMERIC.add(Byte.class);
        PRIMITIVES_NUMERIC.add(byte.class);
        PRIMITIVES_NUMERIC.add(Boolean.class);
        PRIMITIVES_NUMERIC.add(boolean.class);
        PRIMITIVES_NUMERIC.add(Double.class);
        PRIMITIVES_NUMERIC.add(double.class);
        PRIMITIVES_NUMERIC.add(Float.class);
        PRIMITIVES_NUMERIC.add(float.class);
        PRIMITIVES_NUMERIC.add(Long.class);
        PRIMITIVES_NUMERIC.add(long.class);
        PRIMITIVES_NUMERIC.add(Short.class);
        PRIMITIVES_NUMERIC.add(short.class);
        PRIMITIVES_NUMERIC.add(Void.class);
        PRIMITIVES_NUMERIC.add(void.class);
    }

    // List of non numeric primitive types including
    //   UUID and Topic classes for easy (de-)serialization
    static final List<Class<?>> PRIMITIVES_NON_NUMERIC;

    static {
        PRIMITIVES_NON_NUMERIC = new ArrayList<Class<?>>(4);
        PRIMITIVES_NON_NUMERIC.add(Character.class);
        PRIMITIVES_NON_NUMERIC.add(char.class);
        PRIMITIVES_NON_NUMERIC.add(String.class);
        PRIMITIVES_NON_NUMERIC.add(UUID.class);
    }
    // end copy

    /**
     * Serializes a java object to JSON.Works for classes containing only primitive types.
     * Works for classes containing complex built-in or user defined types by
     * recursively serializing the objects.
     *
     * @param <A>                   The type of the Object to be serialized
     * @param a                     An instance of the Object to be serialized
     * @param additionalNonNumerics additional complex classes that should be serialized using toString()
     * @return Returns the JSON string representation of the object.
     */
    public static <A> String serialize(A a, List<Class<?>> additionalNonNumerics) {
        if(a == null) return "null";
        Class abstractestClassBeforeObject = Object.class;
        if (a.getClass() != null) abstractestClassBeforeObject = a.getClass();
        while(abstractestClassBeforeObject.getSuperclass() != null && !abstractestClassBeforeObject.getSuperclass().equals(Object.class)){
            abstractestClassBeforeObject = abstractestClassBeforeObject.getSuperclass();
        }
        if (a.getClass().getSuperclass() ==
                Message.class) {                                      // base case for network messages
            return serializeMessage((Message) a,
                    additionalNonNumerics);                        // casting does not alter the object but simply
            // makes the call typechek
        } else if (PRIMITIVES_NON_NUMERIC.contains(a.getClass()) ||
                a.getClass().isEnum()) {      // base case for non numeric types and enums
            return "\"" + a.toString() + "\"";
        } else if (a.getClass().equals(Class.class)){
            return "\"" + ((Class) a).getCanonicalName() + "\"";
        } else if (PRIMITIVES_NUMERIC.contains(
                a.getClass())) {                                   // base case for numeric types
            return a.toString();
        } else if (abstractestClassBeforeObject.equals(Collection.class)
                    || abstractestClassBeforeObject.equals(AbstractCollection.class)) {
            StringBuilder s = new StringBuilder("[");
            for(Object x: (Collection) a){
                s.append(serialize(x, additionalNonNumerics)).append(", ");
            }
            if (s.length()>=2) s = new StringBuilder(s.substring(0, s.length() - 2));
            s.append("]");
            return s.toString();
        } else {
            return serializeComplex(a,
                    additionalNonNumerics);                                  // recursively serialize a complex type
        }
    }


    /**
     * Serializes Objects with org.Team107.MF.Message as superclass.
     *
     * @param <A>                   The type of the org.Team107.MF.Message
     * @param a                     An instance of a org.Team107.MF.Message extending object
     * @param additionalNonNumerics additional complex classes that should be serialized using toString()
     * @return A JSON representation of the org.Team107.MF.Message
     */
    private static <A extends Message> String serializeMessage(A a, List<Class<?>> additionalNonNumerics) {
        Class superc = a.getClass().getSuperclass();                // will be org.Team107.MF.Message

        String s = "{ ";

        // altered from from https://stackoverflow.com/questions/17095628/loop-over-all-fields-in-a-java-class
        // extracting objects fields (variables) from the org.Team107.MF.Message class;
        Field[] fields = superc.getDeclaredFields();
        // looping the fields
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);  // if field is private make it accessible

            if (f.getAnnotation(Ignore.class) != null) {
                continue;                                             // classes can choose to ignore variables such
            }
            // as the static topic variable in org.Team107.MF.Message-implementations
            try {
                s += String.format("\"%s\": %s", f.getName(), serialize(f.get(a),
                        additionalNonNumerics));  // extract the fields value and add it the the JSON string
            } catch (IllegalAccessException e) {
                // should not happen
                // if it happens it can be ignored
                // because it only happens if setAccessible(true) was not called
                // to prevent changes on private variables
                System.err.println(e.getMessage());
            }
            s += "; ";
        }

        // serialize fields of the messages subclass; (serialize complex ignores the superclass variables
        s += String.format("\"data\": %s", serializeComplex(a, additionalNonNumerics));
        s += " }";
        return s;
    }

    /**
     * Serializes java Objects to JSON strings
     *
     * @param a                     An instance of a org.Team107.MF.Message extending object
     * @param additionalNonNumerics additional complex classes that should be serialized using toString()
     * @return A JSON representation of the org.Team107.MF.Message
     */
    private static <A> String serializeComplex(Object a, List<Class<?>> additionalNonNumerics) {
        Field[] fields = a.getClass().getDeclaredFields();
        if(fields.length == 1 && fields[0].getName().contentEquals("data")){
            try {
                fields[0].setAccessible(true);
                return (String) fields[0].get(a);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            String s = "{ ";

            // edited from https://stackoverflow.com/questions/17095628/loop-over-all-fields-in-a-java-class
            // extracting objects fields (variables)
            // looping object fields
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);  // if field is private make it accessible
                if (f.getAnnotation(Ignore.class) != null || f.getName().startsWith("this$")) {
                    continue;                                                 // classes can choose to ignore variables
                }
                // such as the static topic variable in org.Team107.MF.Message-implementations
                try {
                    // serialize field
                    s += String.format("\"%s\": %s", f.getName(), serialize(f.get(a), additionalNonNumerics));
                    // if not last field add a ,
                    if (i < fields.length - 1) {
                        s += "; ";
                    }
                } catch (IllegalAccessException e) {
                    // should not happen
                    // if it happens it can be ignored
                    // because it only happens if setAccessible(true) was not called
                    // to prevent changes on private variables
                    System.err.println(e.getMessage());
                }

            }
            s += " }";
            //end from
            return s;
        }
    }

    /**
     * Retrieves all variables of an Object and its parent Objects except for Object.class
     * obtained from https://stackoverflow.com/questions/16295949/get-all-fields-even-private-and-inherited-from-class
     *
     * @param <T> The type of the Object to be read
     * @param t   An instance of the Object to be read
     * @return Returns a list of all variables (public and private) of an objects
     * and it's recursive parents up to Object.class
     * @web https://stackoverflow.com/questions/16295949/get-all-fields-even-private-and-inherited-from-class
     */
    protected static <T> List<Field> getFields(T t) {
        List<Field> fields = new ArrayList<>();
        Class clazz = t.getClass();
        while (clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getAnnotation(Ignore.class) ==
                        null) {                     // classes can choose to ignore variables such as the static
                    // topic variable in org.Team107.MF.Message-implementations
                    fields.add(f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    // end copy
}
