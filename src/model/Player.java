package model;

import Cards.*;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    public boolean inGame;
    public boolean inPrison;
    public  int skipNum;
    public int doubleNum;
    public boolean lastDouble;
    public int id;
    public  String name;
    public int colorR;
    public int colorG;
    public int colorB;
    public int position;
    public int money;
    private int asset;
    public ArrayList<UtilityCard> utilities;
    public ArrayList<EstateCard> estates;
    public ArrayList<RailroadCard> railroads;

    public Player(int i){
        this.id =  i;
        this.inGame = true;
        this.inPrison = false;
        skipNum = 0;
        doubleNum = 0;
        switch (i) {
            case 0:
                this.colorR = Color.RED.getRed();
                this.colorG = Color.RED.getGreen();
                this.colorB = Color.RED.getBlue();
                break;
            case 1 :
                this.colorR = Color.YELLOW.getRed();
                this.colorG = Color.YELLOW.getGreen();
                this.colorB = Color.YELLOW.getBlue();
                break;
            case 2 :
                this.colorR = Color.GREEN.getRed();
                this.colorG = Color.GREEN.getGreen();
                this.colorB = Color.GREEN.getBlue();
                break;
            case 3 :
                this.colorR = Color.BLUE.getRed();
                this.colorG = Color.BLUE.getGreen();
                this.colorB = Color.BLUE.getBlue();
                break;
        }
        utilities = new ArrayList<>();
        estates = new ArrayList<>();
        railroads = new ArrayList<>();
        this.position = 0;
        this.money = 1500;
        this.asset = 1500;

    }
    public void buy(PropertyCard card){
        if (card instanceof UtilityCard) {
            utilities.add((UtilityCard) card);
        } else if (card instanceof RailroadCard) {
            railroads.add((RailroadCard) card);
        } else {
            estates.add((EstateCard) card);
        }
        card.soldTo(this);
        pay(card.getPrice());
        asset += card.getPrice() / 2;
    }
    public void buyHouse(EstateCard card){
        if(card.getHouseCount() < card.MAX_HOUSE_COUNT){
            pay(card.getHousePrice());
            asset += card.getHousePrice() / 2;
            card.buildHouse();
        }
        else if(card.getHotelCount() < card.MAX_HOTEL){
            pay(card.getHotelPrice());
            asset += card.getHotelPrice() / 2;
            card.buildHotel();
        }
    }
    public void pay(int amount) {
        money -= amount;
        asset -= amount;
    }
    public boolean move(int dice1, int dice2){
       position = position + dice1 + dice2;
       if (position >= 40){
           getPaid(200);
           position = position % 40;
           return true;
       }
       return false;
    }
    public void goToPrison(){
        doubleNum = 0;
        lastDouble = false;
        inPrison = true;
        skipNum = 2;
        position = 10;
    }
    public boolean getDouble(){
        doubleNum++;
        lastDouble = true;
        if(doubleNum == 3){
            goToPrison();
            return true;
        }
        return false;
    }
    public void skipMove(){
        skipNum--;
        if (skipNum == 0){
            inPrison = false;
        }
    }
    public void getPaid(int amount) {
        money += amount;
        asset += amount;
    }
    public int getUtilityCount() {
        return utilities.size();
    }
    public int getRailroadCount() {
        return railroads.size();
    }
    public int getGroupCount(int groupId){
        int num = 0;
        for (EstateCard est:estates) {
            if (est.groupId == groupId)
                num++;
        }
        return num;
    }
}
