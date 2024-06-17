import java.io.Serializable;

public class AuctionItem implements java.io.Serializable {
    private int itemID;
    private String name;
    private String description;
    private int highestBid;

    public AuctionItem(){

    }

    public AuctionItem(int itemID, String name, String description, int highestBid) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.highestBid = highestBid;
    }

    public int getItemID() {
        return itemID;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getHighestBid() {
        return highestBid;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

    @Override
public String toString() {
    return "Item ID: " + itemID + ", Name: " + name + ", Description: " + description + ", Highest Bid: " + highestBid;
}

}