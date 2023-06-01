package Cards;

public class PayCard extends Card{
    public int tax;

    public PayCard(int id, String name, int x, int y,int tax) {
        super(id, name, x, y);
        this.tax = tax;
    }
    public int getTax(){
        return tax;
    }

    public String getDescr() {
        return null;
    }
}
