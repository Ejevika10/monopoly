package Cards;

import java.util.ArrayList;

public class ChanceCard extends Card{
    public Chance chance;
    ArrayList<Chance> list;
    public ChanceCard(int id, String name, int x, int y) {
        super(id, name, x, y);
        list = new ArrayList<>();
        initList();
    }
    private void initList(){
        list.add(new Chance(1,-150,0, "Возврат займа. Получите 150"));
        list.add(new Chance(1,-50, 0,"Банковские дивиденды. Получите 50"));
        list.add(new Chance(1,150, 0,"Оплата курсов водителей. Заплатите 150"));
        list.add(new Chance(1,15, 0,"Штраф за превышение скорости. Заплатите 15"));
        list.add(new Chance(1,20, 0,"Вождение в нетрезвом виде. Штраф 20"));
        list.add(new Chance(1,-100, 0,"Вы выиграли чемпионат по шахматам. Получите 100"));
        list.add(new Chance(1,-25,0,"Выгодная продажа акций. Получите 25"));
        list.add(new Chance(1,50,0,"Оплата страховки. Заплатите 50"));
        list.add(new Chance(1,50,0,"Оплата услуг доктора. Заплатите 50"));
        list.add(new Chance(1,100,0,"Оплата лечения. Заплатите 100"));
        list.add(new Chance(1,-25,0,"Возмещение налога. Получите 25"));
        list.add(new Chance(1,50,0,"Выгодная продажа облигаций. Получите 50"));
        list.add(new Chance(1,-200,0,"Банковская ошибка в вашу пользу. Получите 200"));
        list.add(new Chance(1,-100,0,"Вы получили наследство. Получите 100"));
        list.add(new Chance(1,-10,0,"Вы заняли второе место на конкурсе красоты. Получите 10"));
        list.add(new Chance(1,-100,0,"Сбор ренты. Получите 100"));
        list.add(new Chance(2,0,30,"Вас арестовали.Отправляйтесь в тюрьму"));
        list.add(new Chance(2,0,6,"Отправляйтесь в Аквапарк."));
        list.add(new Chance(2,0,1,"Вернитесь на Старую дорогу."));
        list.add(new Chance(2,0,15,"Отправляйтесь в Северный морской порт."));
        list.add(new Chance(2,0,0,"Пройдите на Старт."));
        list.add(new Chance(2,0,39,"Отправляйтесь в Гостиничный комплекс."));
        list.add(new Chance(2,0,24,"Отправляйтесь в Ресторан."));
    }
    public void setChance(){
        int i = (int)(Math.random() * (list.size()-1));
        this.chance = list.get(i);
    }
    public String getDescr(){
        return this.chance.descr;
    }
    public int getTax(){
        return chance.getTax();
    }
}
