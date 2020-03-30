import java.util.HashMap;

public interface Dict extends java.rmi.Remote{

    void setMap(HashMap<String,String> dict) throws java.rmi.RemoteException;

    void setUpdate(boolean flag) throws java.rmi.RemoteException;

    void put(String key, String value) throws java.rmi.RemoteException;

    void get(String key) throws java.rmi.RemoteException;

    void delete(String key) throws java.rmi.RemoteException;

    String getTime() throws java.rmi.RemoteException;
}
