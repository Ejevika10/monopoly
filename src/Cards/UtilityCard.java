package Cards;

import Cards.PropertyCard;

public class UtilityCard extends PropertyCard {
    public UtilityCard(int id, String name, int x, int y,int price) {
        super(id, name,x,y, price);
    }

    public int getRentPrice() {
        if(owner.getUtilityCount() == 1)
            return 4;
        else
            return 10;
    }
}
