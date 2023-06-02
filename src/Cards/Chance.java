package Cards;

public class Chance {
    public  int type;
    private final int tax;
    public int moveTo;
    public String descr;
    Chance(int type,int tax,int moveTo, String descr){
        this.type = type;
        this.tax = tax;
        this.moveTo = moveTo;
        this.descr = descr;
    }
    public int getTax(){
        return tax;
    }
}
