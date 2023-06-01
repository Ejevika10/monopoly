package Cards;

import Cards.PropertyCard;

public class RailroadCard extends PropertyCard {
    final private int BASE_RENT_PRICE = 25;

    public RailroadCard(int number, String name,int x, int y, int price) {
        super(number, name, x,y,price);
    }

    @Override
    public int getRentPrice() {
        return owner.getRailroadCount() * BASE_RENT_PRICE;
    }

}
