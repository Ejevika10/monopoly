package Cards;

import java.util.ArrayList;

public class TreasuryCard extends PayCard{
    private String descr;
    ArrayList<Chance> list;

    public TreasuryCard(int id, String name, int x, int y, int tax) {
        super(id, name, x, y, tax);
        list = new ArrayList<>();
        initList();
    }
    private void initList(){
        list.add(new Chance(-25,"Выгодная продажа акций. Получите 25"));
        list.add(new Chance(50,"Оплата страховки. Заплатите 50"));
        list.add(new Chance(50,"Оплата услуг доктора. Заплатите 50"));
        list.add(new Chance(100,"Оплата лечения. Заплатите 100"));
        list.add(new Chance(-25,"Возмещение налога. Получите 25"));
        list.add(new Chance(50,"Выгодная продажа облигаций. Получите 50"));
        list.add(new Chance(-200,"Банковская ошибка в вашу пользу. Получите 200"));
        list.add(new Chance(-100,"Вы получили наследство. Получите 100"));
        list.add(new Chance(-10,"Вы заняли второе место на конкурсе красоты. Получите 10"));
        list.add(new Chance(-100,"Сбор ренты. Получите 100"));
    }
    public void setTreasury(){
        int i = (int)(Math.random() * (list.size()-1));
        this.descr = list.get(i).descr;
        this.tax = list.get(i).tax;
    }
    public String getDescr(){
        return this.descr;
    }
}
