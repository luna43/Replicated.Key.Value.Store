import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class DictImpl extends java.rmi.server.UnicastRemoteObject implements Dict {
    public boolean isUpdate = false;
    public HashMap<String,String> dict;
    /**
     * Constructor just inherits itself
     * @throws java.rmi.RemoteException
     */
    protected DictImpl() throws java.rmi.RemoteException {
        super();
    }

    /**
     * This function prints the current time the way I want
     * @return the date in a nice format
     */
    public String getTime(){
        return "(" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ") ";
    }

    /**
     * I use this function to set my map for each server
     * @param dict key/value map
     * @throws RemoteException part of rmi
     */
    @Override
    public void setMap(HashMap<String,String> dict) throws RemoteException {
        this.dict = dict;
    }

    /**
     * I use this flag to differentiate between a put/delete call coming from client,
     * from a call coming the coordinator
     * @param flag
     */
    @Override
    public void setUpdate(boolean flag) {
        isUpdate=flag;
    }

    /**
     * Remote Procedure call - PUT: Puts the key and value into the map if
     * it does not already exists. Does not overwrite.
     *
     * This function also handles the Abort, if there are not exactly 5 active servers
     *
     * @param key The key you wish to save
     * @param value The value you wish to save
     */
    public void put(String key, String value) {
        if (!isUpdate) {
            boolean result = Coordinator.prepServers();
            if (result==false) {
                System.out.println(getTime() + "Aborting Put because there is an incorrect amount of servers");
                return;
            }
            Coordinator.doPuts(key,value);
            return;
        } else {
        if (this.dict.containsKey(key)){
            System.out.println(getTime() + "PUT Command: Key Exists already. Please delete");
            System.out.println(getTime() + "PUT Command: Active thread count: " + Thread.activeCount());
            return;
        }

        this.dict.put(key,value);
        System.out.println(getTime() + "PUT Command: Added new key to dict: " + key);
        System.out.println(getTime() + "PUT Command: Active thread count: " + Thread.activeCount());
        return;
        }
    }

    /**
     * Remote Procedure Call - GET: checks for the key you input, and
     * returns the key/value pair if found
     *
     * This function is read only so is exactly the same as in proj2
     *
     * @param key the key you want to get
     */
    public void get(String key) {
        if (dict.containsKey(key)){
            System.out.println(getTime() + "GET Command: Key: " + key + " Value: " + dict.get(key));
            System.out.println(getTime() + "GET Command: Active thread count: " + Thread.activeCount());
            return;
        } else {
            System.out.println(getTime() + "GET Command: No value found for Key: " + key);
            System.out.println(getTime() + "GET Command: Active thread count: " + Thread.activeCount());
            return;
        }
    }

    /**
     * Remote Procedure Call - DELETE: removes key from map, or returns an error
     * message that it was not found
     *
     * This function also handles the Abort, if there are not exactly 5 active servers
     *
     * @param key the key you want to delete
     */
    public void delete(String key) {
        if (!isUpdate) {
            boolean result = Coordinator.prepServers();
            if (result==false) {
                System.out.println(getTime() + "Aborting Delete because there is an incorrect amount of servers");
                return;
            }
            Coordinator.doDels(key);
            return;
        } else {
            if (dict.containsKey(key)){
                System.out.println(getTime() + "DELETE Command: Deleted Key: " + key + " Value: " + dict.get(key));
                System.out.println(getTime() + "DELETE Command: Active thread count: " + Thread.activeCount());
                dict.remove(key);
                return;
            } else {
                System.out.println(getTime() + "DELETE COMMAND: No Key found to delete, Key: " + key);
                System.out.println(getTime() + "DELETE Command: Active thread count: " + Thread.activeCount());
                return;
                }
            }
    }
}
