import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class AuctionClient {

    private static SecretKey loadAESKeyFromFile(String keyPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(keyPath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (SecretKey) ois.readObject();
        }
    }

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); 

            Auction auction = (Auction) registry.lookup("Auction");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter itemID to get item details: ");
            int itemID = scanner.nextInt(); 

            SealedObject sealedObject = auction.getSpec(itemID); 
            SecretKey aesKey = loadAESKeyFromFile("keys/testKey.aes");

            Cipher cipher = Cipher.getInstance("AES"); 
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            AuctionItem auctionItem = (AuctionItem) sealedObject.getObject(cipher);

            System.out.println("Item name:" + auctionItem.getName() + "   Item Description:" + auctionItem.getDescription()+ "   Highest Bid:"+ auctionItem.getHighestBid());

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
