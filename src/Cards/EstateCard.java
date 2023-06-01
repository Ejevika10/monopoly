package Cards;

import Cards.PropertyCard;

import java.util.ArrayList;

public class EstateCard extends PropertyCard {

    public int groupId;
    public int groupMaxCount;
    public int housePrice;
    public int hotelPrice;
    public int houseCount;
    public int hotelCount;
    private ArrayList<Integer> rentPrices;
    final public int MAX_HOTEL = 1;
    final public int MAX_HOUSE_COUNT = 4;
    public EstateCard(int id,String name, int x, int y, int price,int groupId,int groupMaxCount, int rent, int oneHouseRent,
                      int twoHouseRent, int threeHouseRent,int fourHouseRent, int hotelRent, int housePrice,
                      int hotelPrice) {
        super(id, name,x,y, price);
        this.groupId = groupId;
        this.groupMaxCount = groupMaxCount;
        this.housePrice = housePrice;
        this.hotelPrice = hotelPrice;
        houseCount = 0;
        hotelCount = 0;
        rentPrices = new ArrayList<>();

        rentPrices.add(rent);
        rentPrices.add(oneHouseRent);
        rentPrices.add(twoHouseRent);
        rentPrices.add(threeHouseRent);
        rentPrices.add(fourHouseRent);
        rentPrices.add(hotelRent);
    }
    public ArrayList<Integer> getRentPrices(){ return rentPrices; }

    public int getHousePrice() {
        return housePrice;
    }

    public int getHotelPrice() {
        return hotelPrice;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public int getHotelCount() {
        return hotelCount;
    }

    public int getRentPrice() {
        int rentPrice;
        if (hotelCount == MAX_HOTEL)
            rentPrice = rentPrices.get(4);
        else
            rentPrice = rentPrices.get(houseCount);
        return rentPrice;
    }
    public void buildHouse() {
        this.houseCount++;
    }
    public void buildHotel() {
        this.hotelCount++;
    }

}
