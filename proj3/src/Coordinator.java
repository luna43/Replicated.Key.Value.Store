import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.format.DateTimeFormatter;

public class Coordinator {
    static RMIServer[] servers;
    static boolean busy = false;

    /**
     * This function prints the current time the way I want
     * @return the date in a nice format
     */
    public static String getTime(){
        return "(" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ") ";
    }

    /**
     * This is the main function, that starts 5 servers based on
     * the 5 ports provided in the args[]
     * @param args 5 ports from program arguments
     */
    public static void main(String[] args) {
        if (args.length < 5 || args.length > 6) {
            System.out.println(getTime() + "Please use 5 port numbers for command arguments separated by a space");
        }
        servers =  new RMIServer[5];
        //set up 5 servers
        for (int i=0; i < 5; i++) {
            int port = Integer.parseInt(args[i]);
            servers[i]= new RMIServer(i,port);
        }
        System.out.println(getTime() + "All servers ready! Thread Count: " + Thread.activeCount());
    }

    /**
     * Sets up first phase of 2pc- preparation for a read/write command. Also this function has a lock around it to make sure
     * only one client is doing things at a time.
     *
     * Also this function does an Abort check, to make sure exactly 5 servers are live
     * @return true if servers are prepared, false if an abort is needed
     */
    public static boolean prepServers(){
        //check for busy flag, if it is busy that means this thread needs to wait
        if (busy) {
            try {
                System.out.println(getTime() + Thread.currentThread().getName() + " is waiting..");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //Setting busy flag before starting preparation
            busy = true;
        }
        int servercount = 0;
        for(RMIServer s : servers) {
            s.prepareServer();
            if(s.isGo==false) {
                servercount++;
            }
            System.out.println(getTime() + "Server " + s.id + " is prepared on port " + s.port);
        }
        //This is where the Abort happens. The server count has to be exactly 5 or it will not continue
        if (servercount==5){
            return true;
        }
        return false;
    }

    /**
     * This function makes the same put command on all 5 servers
     * @param key they key to put
     * @param value the value to put
     */
    public static void doPuts( String key, String value) {
        for (RMIServer s: servers) {
            try {
                int port = s.port;
                Registry registry = LocateRegistry.getRegistry(port);
                Dict localdict = (Dict) registry.lookup("Dict");

                localdict.setUpdate(true);
                localdict.put(key,value);
                System.out.println(getTime() + "Put " + key + " into server on port " + port);
                localdict.setUpdate(false);
                busy=false; //before exiting set busy flag to not busy

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function does the same delete on all 5 servers
     * @param key to delete
     */
    public static void doDels( String key) {
        for (RMIServer s: servers) {
            try {
                int port = s.port;
                Registry registry = LocateRegistry.getRegistry(port);
                Dict localdict = (Dict) registry.lookup("Dict");

                localdict.setUpdate(true);
                localdict.delete(key);
                System.out.println(getTime() + "Deleted " + key + " from server on port " + port);
                localdict.setUpdate(false);
                busy=false; //before exiting set busy flag to not busy

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
