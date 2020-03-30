import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    /**
     * client uses up to 4 parameters, which are input using command arguments
     * @param args inputted command argumments
     */
    public static void main(String[] args) {
        //check arguments
        if (args.length<=1 || args.length >4){
            System.out.println("Format is [port] [command] [key] [value]");
            System.out.println("Command 0 for put");
            System.out.println("Command 1 for get");
            System.out.println("Command 2 for delete");
            return;
        }
        try {
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            Dict localdict = (Dict) registry.lookup("Dict");

            //branch for command
            if(Integer.parseInt(args[1])==0){
                localdict.put(args[2],args[3]);
                return;
            }
            else if (Integer.parseInt(args[1])==1){
                localdict.get(args[2]);
                return;
            }
            else if (Integer.parseInt(args[1])==2){
                localdict.delete(args[2]);
                return;
            } else {
                System.out.println("Format is [port] [command] [key] [value]");
                System.out.println("Command 0 for put");
                System.out.println("Command 1 for get");
                System.out.println("Command 2 for delete");
                return;
            }

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
