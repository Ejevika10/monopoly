package Cards;

import Cards.Card;
import model.Player;

import java.io.Serializable;

public abstract class PropertyCard extends Card implements Serializable{
    public int price;
    public Player owner;
    public Boolean isFree;

    public PropertyCard(int id, String name, int x, int y,int price) {
        super(id, name, x, y);
        this.price = price;
        owner = null;
        isFree = true;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void soldTo(Player player) {
        isFree = false;
        owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public abstract int getRentPrice();

    public void soldBack() {
        isFree= true;
        owner = null;
        if (this instanceof EstateCard) {
            ((EstateCard) this).hotelCount = 0;
            ((EstateCard) this).houseCount = 0;
        }
    }
}
