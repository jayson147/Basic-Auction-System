import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;




public class AuctionServer extends UnicastRemoteObject implements Auction {

    private SecretKey aesKey;

    private Map<Integer, AuctionItem> auctionItems;

    public AuctionServer() throws RemoteException {

        super();
        auctionItems = new HashMap<>();

        try {
            // Load AES Key from file 
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("keys/testKey.aes"));
            aesKey = (SecretKey) keyIn.readObject();
            keyIn.close();
        } catch (Exception e) {
            e.printStackTrace();

            // if key doesn't exist, generate a new one and save
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                aesKey = keyGen.generateKey();
                ObjectOutputStream keyOut = new ObjectOutputStream(new FileOutputStream("keys/testKey.aes"));
                keyOut.writeObject(aesKey);
                keyOut.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // auction items that are hardcoded
        auctionItems.put(1, new AuctionItem(1, "Vase", "Exquisite colours", 1500));
        auctionItems.put(2, new AuctionItem(2, "Painting", "Mona Lisa", 3000));
        auctionItems.put(3, new AuctionItem(3, "Table", "Sturdy fine wood", 5000));

        
    }

    @Override
    public SealedObject getSpec(int itemID) throws RemoteException {
        AuctionItem item = auctionItems.get(itemID);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return new SealedObject(item, cipher);
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }


    public static void main(String[] args) throws RemoteException {


        try {

            AuctionServer auctionServer = new AuctionServer();

            Registry registry = LocateRegistry.getRegistry(1099);

            registry.rebind("Auction", auctionServer);

            System.out.println("Auction Server is ready");
    
        } catch (RemoteException e) {

            System.out.println("Error, failed to connect");
            e.printStackTrace();
        }
        

    }

    
    
}
