import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class RMIServer extends Thread{
    HashMap<String,String> dict; //this map is where the key/value pairs are stored
    public boolean isGo;
    int port;
    int id;

    /**
     * Constructor for server, does initiation for a server including threading
     * @param id the unique id for each server
     * @param port the port number for the server
     */
    public RMIServer(int id, int port) {
        this.dict =  new HashMap<String,String>();
        this.port = port;
        this.id=id;
        this.isGo = false;
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            Dict serverDict = new DictImpl();
            serverDict.setMap(this.dict);
            registry.bind("Dict", serverDict);
            System.out.println(Coordinator.getTime() + "Server " + id + " is ready on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.start();
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function sets a flag for the two phases of 2pc. isGo is true when the commit is happening but
     * in order for the commit it must be flagged false to signify it is in the preparation stage
     */
    public void prepareServer() {
        this.isGo = false;
    }

}
