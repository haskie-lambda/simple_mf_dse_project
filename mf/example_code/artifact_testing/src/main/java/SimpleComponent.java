import Messages.SimpleMessage;
import org.Team107.MF.Echo;
import org.Team107.MF.MF;
import org.Team107.MF.Message;
import org.Team107.MF.MFInitializationException;
import org.Team107.MF.Messages.ContainerInfo;
import org.Team107.MF.functionalbasics.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleComponent extends MF {
    public ArrayList<String> messages = new ArrayList<String>();

    public SimpleComponent(List<Tuple<String, Integer>> peers, Package messagePackage, int portNumber) throws MFInitializationException {
        super(peers, messagePackage, portNumber);
        // either:
        /*
        addHandler(SimpleMessage.class,(Message m) -> {

            messages.add(m.toString());
        });*/
        // or:
        //printTopics();
        addGeneralHandler((Message m) -> {
            messages.add(m.toString());
        });
        listen();
    }

    @Override
    public Echo getMyInformation() {
        return Echo.Response(UUID.randomUUID(), 0, "");
    }
}
