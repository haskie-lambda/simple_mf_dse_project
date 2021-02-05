import Messages.SimpleMessage;
import org.Team107.MF.MF;
import org.Team107.MF.MFInitializationException;
import org.Team107.MF.Message;
import org.Team107.MF.Messages.ContainerInfo;
import org.Team107.MF.functionalbasics.Tuple;

import java.util.ArrayList;
import java.util.UUID;

public class Main {

    public static void main(String... args) throws MFInitializationException, InterruptedException {
        /*
        SimpleComponent comp = new SimpleComponent(new ArrayList<Tuple<String, Integer>>(), Messages.SimpleMessage.class.getPackage(), 8000);
        SimpleComponent comp2 = new SimpleComponent(new ArrayList<Tuple<String, Integer>>(), Messages.SimpleMessage.class.getPackage(), 8001);

        comp.addPeer("localhost", 8001);

        comp.send(new SimpleMessage(100 ));
        Thread.sleep(200);
        comp2.messages.forEach(x -> System.out.println(x));

        comp.send(new SimpleMessage(200));
        Thread.sleep(200);
        comp2.messages.forEach(x -> System.out.println(x));

        comp.stop();
        comp2.stop();

        System.out.println(ContainerInfo.class.getPackage().getName());
*/
        SimpleComponent comp3 = new SimpleComponent(new ArrayList<>(), ContainerInfo.class.getPackage(), 8002);
        SimpleComponent comp4 = new SimpleComponent(new ArrayList<>(), ContainerInfo.class.getPackage(), 8003);
        comp3.addPeer("localhost", 8003);

        comp3.send(new ContainerInfo(UUID.randomUUID(), 0.0, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        Thread.sleep(200);
        System.out.println(comp4.messages.get(0));

        comp3.stop();
        comp4.stop();
    }
}
