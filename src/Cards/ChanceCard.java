package Cards;

import java.util.ArrayList;

public class ChanceCard extends PayCard{
    private String descr;
    ArrayList<Chance> list;
    public ChanceCard(int id, String name, int x, int y, int tax) {
        super(id, name, x, y, tax);
        list = new ArrayList<>();
        initList();
    }
    private void initList(){
        list.add(new Chance(-150, "Возврат займа. Получите 150"));
        list.add(new Chance(-50, "Банковские дивиденды. Получите 50"));
        list.add(new Chance(150, "Оплата курсов водителей. Заплатите 150"));
        list.add(new Chance(15, "Штраф за превышение скорости. Заплатите 15"));
        list.add(new Chance(20, "Вождение в нетрезвом виде. Штраф 20"));
        list.add(new Chance(-100, "Вы выиграли чемпионат по шахматам. Получите 100"));
    }
    public void setChance(){
        int i = (int)(Math.random() * (list.size()-1));
        this.descr = list.get(i).descr;
        this.tax = list.get(i).tax;
    }
    public String getDescr(){
        return this.descr;
    }
}
