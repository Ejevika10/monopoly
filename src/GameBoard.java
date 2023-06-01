import Cards.*;
import model.Player;

import java.util.ArrayList;

public class GameBoard {
    private int activePlayer;
    public int dice1;
    public int dice2;
    public Card[] cards;
    public ArrayList<Player> players;
    public int plCount;
    public TcpServer server;

    GameBoard(TcpServer server){
        activePlayer = 0;
        cards = new Card[40];
        initGameBoard();
        players = new ArrayList<>();
        plCount = 0;
        this.server = server;
    }

    public void changeActivePlayer() {
        activePlayer++;
        activePlayer = activePlayer % 4;
        if (!players.get(activePlayer).inGame)
            changeActivePlayer();
    }
    public int getActivePlayer() {
        return activePlayer;
    }
    public void move(Messages.MoveMsg move) {
        dice1 = move.Dice1;
        dice2 = move.Dice2;
        Player curPl = players.get(activePlayer);
        curPl.move(dice1,dice2);
        server.sendPl(activePlayer);
        int pos = curPl.position;

        if(cards[pos] instanceof PropertyCard && !((PropertyCard)cards[pos]).isFree) {
            int rent = ((PropertyCard)cards[pos]).getRentPrice();
            if (cards[pos]  instanceof UtilityCard) {
                rent *= (dice1 + dice2);
            }
            if (curPl.money >= rent) {
                curPl.pay(rent);
                ((PropertyCard) cards[pos]).owner.getPaid(rent);
                server.sendPl(((PropertyCard) cards[pos]).owner.id);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                server.sendServerMsg("Игрок " + curPl.name + " платит аренду игроку " + ((PropertyCard) cards[pos]).owner.name + " в размере " + rent );
            }
            else {
                server.sendServerMsg("Игрок " + curPl.name + " не может заплатить аренду.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                server.exit(activePlayer);
            }
        }
        else if(cards[pos] instanceof PayCard){
            if (cards[pos] instanceof ChanceCard){
                ((ChanceCard) cards[pos]).setChance();
            }
            else if (cards[pos] instanceof TreasuryCard){
                ((TreasuryCard) cards[pos]).setTreasury();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            server.sendServerMsg(((PayCard)cards[pos]).getDescr());
            int tax = ((PayCard) cards[pos]).getTax();
            if (curPl.money >= tax) {
                curPl.pay(tax);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                server.sendPl(activePlayer);
            }
            else {
                server.sendServerMsg("Игрок " + curPl.name + " не может заплатить банку.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                server.exit(activePlayer);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void getTrade(Messages.TradeMsg tradeMsg){
        Player from = players.get(tradeMsg.idFrom);
        Player to = players.get(tradeMsg.idTo);

        from.pay(tradeMsg.moneyFrom);
        from.getPaid(tradeMsg.moneyTo);
        to.pay(tradeMsg.moneyTo);
        to.getPaid(tradeMsg.moneyFrom);

        for (int i = 0; i < tradeMsg.cardsFrom.length; i++) {
            if(cards[tradeMsg.cardsFrom[i]] instanceof EstateCard) {
                from.estates.remove(cards[tradeMsg.cardsFrom[i]]);
                to.estates.add((EstateCard) cards[tradeMsg.cardsFrom[i]]);
            }
            else if(cards[tradeMsg.cardsFrom[i]] instanceof UtilityCard) {
                from.utilities.remove(cards[tradeMsg.cardsFrom[i]]);
                to.utilities.add((UtilityCard) cards[tradeMsg.cardsFrom[i]]);
            }
            else if(cards[tradeMsg.cardsFrom[i]] instanceof RailroadCard) {
                from.railroads.remove(cards[tradeMsg.cardsFrom[i]]);
                to.railroads.add((RailroadCard) cards[tradeMsg.cardsFrom[i]]);
            }

            ((PropertyCard) cards[tradeMsg.cardsFrom[i]]).owner = to;
        }
        for (int i = 0; i < tradeMsg.cardsTo.length; i++) {
            if(cards[tradeMsg.cardsTo[i]] instanceof EstateCard) {
                to.estates.remove(cards[tradeMsg.cardsTo[i]]);
                from.estates.add((EstateCard) cards[tradeMsg.cardsTo[i]]);
            }
            else if(cards[tradeMsg.cardsTo[i]] instanceof UtilityCard) {
                to.utilities.remove(cards[tradeMsg.cardsTo[i]]);
                from.utilities.add((UtilityCard) cards[tradeMsg.cardsTo[i]]);
            }
            else if(cards[tradeMsg.cardsTo[i]] instanceof RailroadCard) {
                to.railroads.remove(cards[tradeMsg.cardsTo[i]]);
                from.railroads.add((RailroadCard) cards[tradeMsg.cardsTo[i]]);
            }

            ((PropertyCard) cards[tradeMsg.cardsTo[i]]).owner = from;
        }
    }
    public void initGameBoard(){
        cards[0] = new FreeParkingCard(0,"Поле вперед",0,0);
        cards[1] = new EstateCard(1,"Старая дорога",90,0,60,1,2,2,10,30,90,160,250,50,50);
        cards[2] = new TreasuryCard(2,"Казна",148,0,0);
        cards[3] = new EstateCard(3,"Главное шоссе",206,0,60,1,2,4,20,60,180,320,450,50,50);
        cards[4] = new PayCard(4,"Налог с доходов",264,0, 200);
        cards[5] = new RailroadCard(5,"Западный морской порт",322,0,200);
        cards[6] = new EstateCard(6,"Аквапарк",380,0,100,2,3,6,30,90,270,400,550,50,50);
        cards[7] = new ChanceCard(7,"Шанс",438,0,0);
        cards[8] = new EstateCard(8,"Городской парк",496,0,100,2,3,6,30,90,270,400,550,50,50);
        cards[9] = new EstateCard(9,"Горнолыжный курорт",554,0,120,2,3,8,40,100,300,450,600,50,50);
        cards[10] = new FreeParkingCard(10,"Тюрьма",610,0);
        //
        cards[11] = new EstateCard(11,"Спальный район",610,90,140,3,3,10,50,150,450,625,750,100,100);
        cards[12] = new UtilityCard(12,"Электрическая компания",610,148,150);
        cards[13] = new EstateCard(13,"Деловой квартал",610,206,140,3,3,10,50,150,450,625,750,100,100);
        cards[14] = new EstateCard(14,"Торговая площадь",610,264,160,3,3,12,60,180,500,700,900,100,100);
        cards[15] = new RailroadCard(15,"Северный морской порт",610,322,200);
        cards[16] = new EstateCard(16,"Улица Пушкина",610,380,180,4,3,14,70,200,550,750,950,100,100);
        cards[17] = new TreasuryCard(17,"Казна",610,438,0);
        cards[18] = new EstateCard(18,"Проспект мира",610,496,180,4,3,10,50,150,450,625,750,100,100);
        cards[19] = new EstateCard(19,"Проспект Победы",610,554,200,4,3,16,80,220,600,800,1000,100,100);
        cards[20] = new FreeParkingCard(20,"Бесплатная парковка",610,610);
        //
        cards[21] = new EstateCard(21,"Бар",552,610,220,5,3,18,90,250,700,875,1050,150,150);
        cards[22] = new ChanceCard(22,"Шанс",494,610,0);
        cards[23] = new EstateCard(23,"Ночной клуб",436,610,220,5,3,18,90,250,700,875,1050,150,150);
        cards[24] = new EstateCard(24,"Ресторан",378,610,240,5,3,20,100,300,750,925,1100,150,150);
        cards[25] = new RailroadCard(25,"Восточный морской порт",320,610,200);
        cards[26] = new EstateCard(26,"Компьютеры",262,610,260,6,3,22,110,330,800,975,1150,150,150);
        cards[27] = new EstateCard(27,"Интернет",204,610,260,6,3,22,110,330,800,975,1150,150,150);
        cards[28] = new UtilityCard(28,"Водопроводная компания",146,610,150);
        cards[29] = new EstateCard(29,"Сотовая связь",90,610,280,6,3,24,120,360,850,1025,1200,150,150);
        cards[30] = new FreeParkingCard(30,"Отправляйтесь в тюрьму",0,610);
        //
        cards[31] = new EstateCard(31,"Морские перевозки",0,552,300,7,3,26,130,390,900,1100,1275,200,200);
        cards[32] = new EstateCard(32,"Железная дорога",0,494,300,7,3,26,130,390,900,1100,1275,200,200);
        cards[33] = new TreasuryCard(33,"Казна",0,436,0);
        cards[34] = new EstateCard(34,"Авиалинии",0,378,320,7,3,28,150,450,1000,1200,1400,200,200);
        cards[35] = new RailroadCard(35,"Южный морской порт",0,320,200);
        cards[36] = new ChanceCard(36,"Шанс",0,262,0);
        cards[37] = new EstateCard(37,"Курортная зона",0,204,350,8,2,35,175,500,1100,1300,1500,200,200);
        cards[38] = new PayCard(38,"Дорогая покупка",0,146,100);
        cards[39] = new EstateCard(39,"Гостиничный комплекс",0,90,400,8,2,50,200,600,1400,1700,2000,200,200);
    }
}

