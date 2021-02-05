package org.Team107.MF;

import org.Team107.MF.Testing.BoolMessage;
import org.Team107.MF.Testing.ContainerInfo_testing;
import org.Team107.MF.functionalbasics.Tuple;
import org.junit.jupiter.api.Test;
import org.Team107.MF.Messages.*;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Test suite for the JSON Serializer and Deserializer Classes
 * @author Fabian Schneider
 */
public class JSON_De_Serializer_Test {


    @Test
    void SimpleContainerInfoTest(){
        ContainerInfo_testing ci = new ContainerInfo_testing(UUID.randomUUID());
        assertEquals(ci.toString().replace(" ", "")
                    ,("{\"messageID\":\"" + ci.getMessageID() + "\";\"topic\":\"" + ci.getTopicClass().getCanonicalName() + "\";\"data\":{\"containerID\":\"" + ci.getContainerID() + "\"}}"));
    }

   @Test
   void AbstractMessageTest() {
        try {
            ContainerInfo_testing ci = new ContainerInfo_testing(UUID.randomUUID());
            AbstractMessage m = JSONDeserializer.parseMessage(AbstractMessage.class, ci.toString()); //"{ \"messageID\": \"f32c4841-98b5-4371-a1c2-3331dca7a2e8\"; \"topic\": \"ContainerInfo_testing\"; \"data\": { \"containerID\": \"8cde04f4-2def-41f4-9fbb-6c34a80e1e6f\" } }");
            assertEquals(ci.toString().replace(" ", "")
                    ,("{\"messageID\":\"" + ci.getMessageID() + "\";\"topic\":\"" + ci.getTopicClass().getCanonicalName() + "\";\"data\":{\"containerID\":\"" + ci.getContainerID() + "\"}}"));
        } catch (Exception e) {fail(e);}
   }


    /**
     * testing all the test subjects initialized at the end of the module
     */
    @Test
    void serializePOJOs(){
        for(Tuple<Class, Tuple<Object, String>> e : testSubjects){
            Class c = e.first(); Object o = e.second().first(); String s = e.second().second();
            assertEquals(s.replace(" ", ""), JSONSerializer.serialize(o, new ArrayList<>()).replace(" ", ""),
				() -> c.getName() + " should equal " + s);
        }
    }

    /**
     * Testing Serialization for messaging test subjects (initialized at the end of the module)
     */
    @Test
    void serializeMessages(){
        for(Tuple<Class, Tuple<Message, String>> e : messagingTestSubjects){
            Message o = e.second().first(); String s = e.second().second();
            String serialized = o.toString();
            assertEquals(s.replace(" ", ""), serialized.replace(" ", ""),
                    () -> serialized + " should equal " + s);
        }

    }

    /**
     * testing deserialization for messaging test subjects
     */
    @Test
    void de_serializeMessages(){
        for(Tuple<Class, Tuple<Message, String>> e : messagingTestSubjects){
            Class c = e.first(); Message o = e.second().first(); String s = e.second().second();
            UUID mid = o.getMessageID();
            String serialized = o.toString();
            try {
                Object deserialized = JSONDeserializer.parseMessage(c, serialized);
                assertEquals(s.replace(" ", ""), deserialized.toString().replace(" ", ""),
                        () -> deserialized.toString() + " should equal \n" + s);
            } catch (ParseException ex){
                System.out.println(serialized);
                System.out.println("not correct");
                fail(ex);

            }
        }

    }



    //------------------------------------------------------
    //           MESSAGING_TEST_SUBJECTS
    //------------------------------------------------------
    static List<Tuple<Class, Tuple<Message, String>>> messagingTestSubjects;
    static{
        messagingTestSubjects = new LinkedList<>();
    }
    static void addToMTests(Class c, Message m, String s){
        messagingTestSubjects.add(new Tuple(c, new Tuple(m, s)));
    }

    static {
        org.Team107.MF.Messages.ContainerInfo ci = new org.Team107.MF.Messages.ContainerInfo(UUID.randomUUID(), 10.0, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        addToMTests(ContainerInfo.class, ci
                ,"{\"messageID\":\"" + ci.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.ContainerInfo\";\"data\":{\"containerID\":\"" + ci.getContainerID() + "\"; \"weight\": 10.0; \"src\": \"" + ci.getSrc() + "\"; \"dest\": \"" + ci.getDest() + "\"; \"vehicle\": \"" + ci.getVehicle() + "\"}}");

        ContainerInfoRequest cir = new ContainerInfoRequest(UUID.randomUUID());
        addToMTests(ContainerInfoRequest.class, cir
                ,"{\"messageID\":\"" + cir.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.ContainerInfoRequest\";\"data\":{\"containerID\":\"" + cir.getContainerID() + "\"}}");

        OccupationReport or = new OccupationReport(UUID.randomUUID(), 10.0, 12.0);
        addToMTests(OccupationReport.class, or
                ,"{\"messageID\":\"" + or.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.OccupationReport\";\"data\":{\"hubID\":\"" + or.getHubID() + "\"; \"storage\": " + or.getStorage() + "; \"capacity\": " + or.getCapacity() +" }}");

        PickupNotification pn = new PickupNotification(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0d);
        addToMTests(PickupNotification.class, pn
                ,"{\"messageID\":\"" + pn.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.PickupNotification\";\"data\":{\"hubID\":\"" + pn.getHubID() + "\"; \"containerID\": \"" + pn.getContainerID() + "\"; \"vehicleID\": \"" + pn.getVehicleID() +"\"; \"src\": \"" + pn.getSrc() + "\"; \"hopDistance\": " + pn.getHopDistance() + "}}");

        PickupRequest pr = new PickupRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),10.0);
        addToMTests(PickupRequest.class, pr
                ,"{\"messageID\":\"" + pr.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.PickupRequest\";\"data\":{\"sourceID\":\"" + pr.getSourceID()+ "\"; \"containerID\": \"" + pr.getContainerID() + "\"; \"destinationID\": \"" + pr.getDestinationID() +"\"; \"containerWeight\": " + pr.getContainerWeight() + " }}");

        System.out.println();
        ReadyToLoad rtl = new ReadyToLoad(UUID.randomUUID(), UUID.randomUUID());
        addToMTests(ReadyToLoad.class, rtl
                ,"{\"messageID\":\"" + rtl.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.ReadyToLoad\";\"data\":{\"vehicleID\":\"" + rtl.getVehicleID() + "\"; \"location\":\"" + rtl.getLocation() + "\" }}");


        RouteResult rr = new RouteResult(UUID.randomUUID(), UUID.randomUUID(), "route to target", UUID.randomUUID());
        addToMTests(RouteResult.class, rr
                ,"{\"messageID\":\"" + rr.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.RouteResult\";\"data\":{\"hubID\":\"" + rr.getHubID() + "\"; \"containerID\": \"" + rr.getContainerID() + "\"; \"route\": \"" + rr.getRoute() +"\"; \"destinationID\": \"" + rr.getDestinationID() + "\" }}");

        VehicleLoaded vl = new VehicleLoaded(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0d);
        addToMTests(VehicleLoaded.class, vl
                ,"{\"messageID\":\"" + vl.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.VehicleLoaded\";\"data\":{\"vehicleID\":\"" + vl.getVehicleID() + "\"; \"containerID\": \"" + vl.getContainerID() + "\"; \"destination\": \"" + vl.getDestination() +"\"; \"hopDistance\": " + vl.getHopDistance() + " }}");

        VehicleLocationUpdate vlu = new VehicleLocationUpdate(UUID.randomUUID(), UUID.randomUUID());
        addToMTests(VehicleLocationUpdate.class, vlu
                ,"{\"messageID\":\"" + vlu.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.VehicleLocationUpdate\";\"data\":{\"vehicleID\":\"" + vlu.getVehicleID() + "\"; \"location\": \"" + vlu.getLocation() + "\" }}");

        VehicleUnloaded vu = new VehicleUnloaded(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        addToMTests(VehicleUnloaded.class, vu
                ,"{\"messageID\":\"" + vu.getMessageID() + "\";\"topic\":\"org.Team107.MF.Messages.VehicleUnloaded\";\"data\":{\"vehicleID\":\"" + vu.getVehicleID() + "\"; \"containerID\": \"" + vu.getContainerID() + "\"; \"location\": \"" + vu.getLocation() + "\" }}");

        Echo e = Echo.Request(UUID.randomUUID(), 0, "hi");
        addToMTests(Echo.class, e
                ,"{\"messageID\":\"" + e.getMessageID() + "\";\"topic\":\"org.Team107.MF.Echo\";\"data\":{\"uuid\":\"" + e.getUuid() + "\"; \"id\": " + e.getId() + "; \"otherInfo\": \"" + e.getOtherInfo() + "\"; \"request\": \"" + (e.isRequest()?"true":"false") + "\"}}");

        Echo e1 = Echo.Response(UUID.randomUUID(), 0, "hi");
        addToMTests(Echo.class, e1
                ,"{\"messageID\":\"" + e1.getMessageID() + "\";\"topic\":\"org.Team107.MF.Echo\";\"data\":{\"uuid\":\"" + e1.getUuid() + "\"; \"id\": " + e1.getId() + "; \"otherInfo\": \"" + e1.getOtherInfo() + "\"; \"request\": \"" + (e1.isRequest()?"true":"false") + "\"}}");

        Echo e2 = Echo.Request(UUID.fromString("7c915874-5457-436d-bdf5-07440376f7e8"), 1, "SD1");
        addToMTests(Echo.class, e2
                ,"{ \"messageID\": \"" + e2.getMessageID() + "\"; \"topic\": \"org.Team107.MF.Echo\"; \"data\": { \"uuid\": \"7c915874-5457-436d-bdf5-07440376f7e8\"; \"id\": 1; \"otherInfo\": \"SD1\"; \"request\": \"true\" } }");

        BoolMessage b = new BoolMessage();
        addToMTests(BoolMessage.class, b
                ,"{ \"messageID\": \"" + b.getMessageID() + "\"; \"topic\": \"org.Team107.MF.Testing.BoolMessage\"; \"data\": { \"b\": false } }");
        BoolMessage b1 = new BoolMessage(true);
        addToMTests(BoolMessage.class, b1
                ,"{ \"messageID\": \"" + b1.getMessageID() + "\"; \"topic\": \"org.Team107.MF.Testing.BoolMessage\"; \"data\": { \"b\": true } }");
    }



    //------------------------------------------------------
    //           TEST_SUBJECTS
    //------------------------------------------------------

//@formatter.off
    static List<Tuple<Class, Tuple<Object, String>>> testSubjects; // a list of test cases: message class, an object of the class and the string it should correspond to

    // initialize
    static{
        testSubjects = new LinkedList<>();
    }

    /**
     * adds a class, an object of the class and the string it should serialize to/be deserialized from to the test cases
     * @param c the class of the message
     * @param o the object to serialize / deserialize
     * @param s the corresponding message string
     */
    static void addToTests(Class c, Object o, String s){
        testSubjects.add(new Tuple(c, new Tuple(o, s)));
    }

    /*
    Following are exemplary test message classes covering a wide
    variety of classes with different members, nested classes at different levels, etc.

    to the right are test cases added to the test subjects for the specific class;
     */

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
/*  Test Class definition                                                                   |   Test cases added to the test subjects                                                              */
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    static class EmptyPOJO{}                                                              /*|*/ static { addToTests(EmptyPOJO.class,new EmptyPOJO(), "{}")
                                                                                          /*|*/        ; addToTests(EmptyPOJO.class, null, "null") ;}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO0{ private int x = 0; }                                       /*|*/ static { addToTests(TrivialPOJO0.class, new TrivialPOJO0(), "{ \"x\": 0 }")
                                                                                          /*|*/        ; TrivialPOJO0 t = new TrivialPOJO0()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO0.class, t, "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO1{ public int x = 0; }                                        /*|*/ static { addToTests(TrivialPOJO1.class, new TrivialPOJO1(), "{ \"x\": 0 }")
                                                                                          /*|*/        ; TrivialPOJO1 t = new TrivialPOJO1()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO1.class, t, "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO2{ protected int x = 0;}                                      /*|*/ static { addToTests(TrivialPOJO2.class, new TrivialPOJO2(), "{ \"x\": 0 }")
                                                                                          /*|*/        ; TrivialPOJO2 t = new TrivialPOJO2()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO2.class, t, "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO3{ @Ignore private int x = 0; }                               /*|*/ static { addToTests(TrivialPOJO3.class, new TrivialPOJO3(), "{ }");
                                                                                          /*|*/        ; TrivialPOJO3 t = new TrivialPOJO3()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO3.class, t, "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO5{ @Ignore public int x = 0; }                                /*|*/ static { addToTests(TrivialPOJO5.class, new TrivialPOJO5(), "{ }");
                                                                                          /*|*/        ; TrivialPOJO5 t = new TrivialPOJO5()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO5.class, t, "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO6{ @Ignore protected int x = 0;}                              /*|*/ static { addToTests(TrivialPOJO6.class, new TrivialPOJO6(), "{ }");
                                                                                          /*|*/        ; TrivialPOJO6 t = new TrivialPOJO6()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO6.class, t, "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO7{ private final int x = 3; }                                 /*|*/ static { addToTests(TrivialPOJO7.class, new TrivialPOJO7(), "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO8{ private static final int x = 3; }                          /*|*/ static { addToTests(TrivialPOJO8.class, new TrivialPOJO8(), "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO9{ @Ignore private final int x = 3; }                         /*|*/ static { addToTests(TrivialPOJO9.class, new TrivialPOJO9(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO16{ @Ignore private static final int x = 3; }                 /*|*/ static { addToTests(TrivialPOJO16.class, new TrivialPOJO16(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO10{ private final int x = 3; }                                /*|*/ static { addToTests(TrivialPOJO10.class, new TrivialPOJO10(), "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO11{ int x; }                                                  /*|*/ static { addToTests(TrivialPOJO11.class, new TrivialPOJO11(), "{ \"x\": 0 }");
                                                                                          /*|*/        ; TrivialPOJO11 t = new TrivialPOJO11()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO11.class, t, "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO12{ final int x; TrivialPOJO12(){ x = 12;}}                   /*|*/ static { addToTests(TrivialPOJO12.class, new TrivialPOJO12(), "{ \"x\": 12 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO13{ int x; TrivialPOJO13() {}}                                /*|*/ static { addToTests(TrivialPOJO13.class, new TrivialPOJO13(), "{ \"x\": 0 }");
                                                                                          /*|*/        ; TrivialPOJO13 t = new TrivialPOJO13()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO13.class, t, "{ \"x\": 3 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO14{ @Ignore int x; TrivialPOJO14() { x = 14; }}               /*|*/ static { addToTests(TrivialPOJO14.class, new TrivialPOJO14(), "{  }");
                                                                                          /*|*/        ; TrivialPOJO14 t = new TrivialPOJO14()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO14.class, t, "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO15{ @Ignore int x; TrivialPOJO15() { }}                       /*|*/ static { addToTests(TrivialPOJO15.class, new TrivialPOJO15(), "{  }");
                                                                                          /*|*/        ; TrivialPOJO15 t = new TrivialPOJO15()
                                                                                          /*|*/        ; t.x = 3
                                                                                          /*|*/        ; addToTests(TrivialPOJO15.class, t, "{ }"); }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO17{ private boolean x = false; }                              /*|*/ static { addToTests(TrivialPOJO17.class, new TrivialPOJO17(), "{ \"x\": false }")
                                                                                          /*|*/        ; TrivialPOJO17 t = new TrivialPOJO17()
                                                                                          /*|*/        ; t.x = true
                                                                                          /*|*/        ; addToTests(TrivialPOJO17.class, t, "{ \"x\": true }"); }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class TrivialPOJO18{ private Collection<Integer> xs = new LinkedList<>(); }    /*|*/ static { addToTests(TrivialPOJO18.class, new TrivialPOJO18(), "{ \"xs\": [ ] }")
                                                                                          /*|*/        ; TrivialPOJO18 t = new TrivialPOJO18()
                                                                                          /*|*/        ; t.xs.add(1)
                                                                                          /*|*/        ; addToTests(TrivialPOJO18.class, t, "{ \"xs\": [ 1 ] }")
                                                                                          /*|*/        ;
                                                                                          /*|*/        ; TrivialPOJO18 t1 = new TrivialPOJO18()
                                                                                          /*|*/        ; t1.xs.add(1)
                                                                                          /*|*/        ; t1.xs.add(2)
                                                                                          /*|*/        ; addToTests(TrivialPOJO18.class, t1, "{ \"xs\": [ 1, 2 ] }")
                                                                                          /*|*/        ; }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
static class FuncPOJO0   { private void method() {} }                                     /*|*/ static { addToTests(FuncPOJO0.class, new FuncPOJO0(), "{ }");}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class FuncPOJO1   { private int method() { return 0; } }                       /*|*/ static { addToTests(FuncPOJO1.class, new FuncPOJO1(), "{ }");}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class FuncPOJO2   { private int method(int x) { return x; } }                  /*|*/ static { addToTests(FuncPOJO2.class, new FuncPOJO2(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class POJO0       { private int x; private void method (int x) { } }           /*|*/ static { addToTests(POJO0.class, new POJO0(), "{ \"x\": 0 }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class POJO1       { @Ignore private int x; private void method (int x) { } }   /*|*/ static { addToTests(POJO1.class, new POJO1(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class POJO2       { private int x = 2; private void method (int x) { } }       /*|*/ static { addToTests(POJO2.class, new POJO2(), "{ \"x\": 2}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class POJO3       { @Ignore private int x = 2; private void method (int x) { }}/*|*/ static { addToTests(POJO3.class, new POJO3(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO0 { TrivialPOJO0 p; }                                          /*|*/ static { addToTests(NestedPOJO0.class, new NestedPOJO0(), "{ \"p\": null }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO1 { @Ignore TrivialPOJO0 p; }                                  /*|*/ static { addToTests(NestedPOJO1.class, new NestedPOJO1(), "{ }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO2 { private int x = 2; TrivialPOJO0 p; }                       /*|*/ static { addToTests(NestedPOJO2.class, new NestedPOJO2(), "{\"x\": 2; \"p\": null}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO3 { NestedPOJO0 p = new NestedPOJO0(); }                       /*|*/ static { addToTests(NestedPOJO3.class, new NestedPOJO3(), "{\"p\": { \"p\": null }}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO4 { NestedPOJO1 p = new NestedPOJO1(); }                       /*|*/ static { addToTests(NestedPOJO4.class, new NestedPOJO4(), "{\"p\": { } }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO5 { NestedPOJO2 p = new NestedPOJO2(); }                       /*|*/ static { addToTests(NestedPOJO5.class, new NestedPOJO5(), "{\"p\": {\"x\": 2; \"p\": null} }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO6 { int y = 3; NestedPOJO0 p; }                                /*|*/ static { addToTests(NestedPOJO6.class, new NestedPOJO6(), "{\"y\": 3; \"p\": null}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO7 { int y = 4; NestedPOJO1 p = new NestedPOJO1(); }            /*|*/ static { addToTests(NestedPOJO7.class, new NestedPOJO7(), "{\"y\": 4; \"p\": {} }"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO8 { int y = 5; NestedPOJO2 p; }                                /*|*/ static { addToTests(NestedPOJO8.class, new NestedPOJO8(), "{\"y\": 5; \"p\": null}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    static class NestedPOJO9 { private int x = 2; TrivialPOJO0 p = new TrivialPOJO0(); }  /*|*/ static { addToTests(NestedPOJO9.class, new NestedPOJO9(), "{\"x\": 2; \"p\": { \"x\": 0 }}"); }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//@formatter.on
}
