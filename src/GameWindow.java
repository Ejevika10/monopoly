import Cards.*;
import com.google.gson.Gson;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class GameWindow extends JFrame {
    String name;
    int id;
    int activeID;

    ArrayList<Player> player;
    Card[] cards;
    private JLabel[] pl_name;
    private JLabel[] pl_money;
    private JLabel[] pl_position;
    public JLabel my_name;
    public JLabel my_money;
    private JLabel my_position;

    private JButton roll_dice;
    private JButton trade;
    private JButton buy;
    private JButton endTurn;
    private JButton exit;

    public BoardDisplay gamePanel;
    OutputStream socketOut;

    public GameWindow(String name,int id, OutputStream socketOut) throws IOException {
        super("MONOPOLY");
        this.name = name;
        this.id = id;
        activeID = -1;
        player = new ArrayList<>();
        for (int i = 0;i < 4; i++){
            player.add(new Player(i));
        }
        cards = new Card[40];
        initGameBoard();
        this.socketOut = socketOut;
        createGUI(socketOut);
    }
    private void createGUI(OutputStream socketOut) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000,700));

        gamePanel = new BoardDisplay(this);

        JPanel game_stat = new JPanel(new GridLayout(4,1,5,5));
        game_stat.setPreferredSize(new Dimension(150,700));
        pl_name = new JLabel[4];
        pl_money = new  JLabel[4];
        pl_position = new JLabel[4];
        for (int i = 0; i < 4; i++){
            JPanel pl_stat = new JPanel(new GridLayout(3,1,5,5));
            int r = player.get(i).colorR;
            int gr = player.get(i).colorG;
            int b = player.get(i).colorB;
            pl_stat.setBackground(new Color(r,gr,b));
            pl_name[i] = new JLabel("Игрок " + player.get(i).name);
            pl_stat.add(pl_name[i]);
            pl_money[i] = new JLabel("Деньги = 1500");
            pl_stat.add(pl_money[i]);
            pl_position[i] = new JLabel("Позиция = 0");
            pl_stat.add(pl_position[i]);
            game_stat.add(pl_stat);
        }
        mainPanel.add(game_stat,BorderLayout.WEST);

        JPanel player_stat = new JPanel(new GridLayout(10,1,5,5));
        player_stat.setPreferredSize(new Dimension(150,700));
        my_name = new JLabel("Игрок " + name);
        player_stat.add(my_name);
        my_money = new JLabel("Деньги = 1500");
        player_stat.add(my_money);
        my_position = new JLabel("Позиция = 0");
        player_stat.add(my_position);
        trade = new JButton("Предложить обмен");
        trade.setEnabled(false);
        trade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    createTradeDialog(GameWindow.this,id,true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        player_stat.add(trade);
        roll_dice = new JButton("Бросить кубики");
        roll_dice.setEnabled(false);
        roll_dice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollBtnPressed();
                endTurn.setEnabled(true);
                roll_dice.setEnabled(false);
            }
        });
        player_stat.add(roll_dice);
        buy = new JButton("Купить");
        buy.setEnabled(false);
        buy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyBtnPressed();
                buy.setEnabled(false);
            }
        });
        player_stat.add(buy);
        endTurn = new JButton("Закончить ход");
        endTurn.setEnabled(false);
        endTurn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                Messages.EndMsg endMessage = new Messages.EndMsg();
                endMessage.your_id = 0;
                Common.writeBytes(socketOut, gson.toJson(endMessage));
            }
        });
        player_stat.add(endTurn);
        exit = new JButton("Сдаться");
        exit.setEnabled(true);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                Messages.ExitMsg exitMsg = new Messages.ExitMsg();
                exitMsg.id = id;
                Common.writeBytes(socketOut, gson.toJson(exitMsg));
                exit.setEnabled(false);
            }
        });
        player_stat.add(exit);

        mainPanel.add(player_stat,BorderLayout.EAST);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setSize(1000, 700);

        gamePanel.ta.append("Ожидайте подключения противника...\n");

    }
    public void updGUI(){
        buy.setEnabled(activeID == id && cards[player.get(id).position] instanceof PropertyCard && ((PropertyCard) cards[player.get(id).position]).isFree && player.get(activeID).money >= ((PropertyCard) cards[player.get(id).position]).getPrice());

        for (int i = 0; i < 4; i++){
            pl_name[i].setText("Игрок " + player.get(i).name);
            pl_money[i].setText("Деньги = "+ player.get(i).money);
            pl_position[i].setText("Позиция = " + player.get(i).position);
        }
        my_money = new JLabel("Деньги = "+ player.get(id).money);
        my_position = new JLabel("Позиция = "+ player.get(id).position);

    }
    public void updTurn(Messages.StartMsg startMsg) {
        activeID = startMsg.activeId;
        if (startMsg.activeId == id) {
            roll_dice.setEnabled(true);
            trade.setEnabled(true);
        } else {
            roll_dice.setEnabled(false);
            trade.setEnabled(false);
            buy.setEnabled(false);
            endTurn.setEnabled(false);
        }
    }
    public void exit(int plId){
        Player pl = player.get(plId);
        pl.inGame = false;
        for (int i = 0 ; i < 40; i++){
            if(cards[i] instanceof PropertyCard && !((PropertyCard)cards[i]).isFree && ((PropertyCard)cards[i]).owner.id == plId){
                ((PropertyCard) cards[i]).soldBack();
            }
        }
        if (id == plId){
            roll_dice.setEnabled(false);
            exit.setEnabled(false);
            buy.setEnabled(false);
            trade.setEnabled(false);
            JDialog dialog = new JDialog(this,"loose",true);
            dialog.setLayout(new BorderLayout());
            JLabel lbl1 = new JLabel("Вы проиграли.");
            dialog.add(lbl1);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }
    public void win(int id){
        JDialog dialog = new JDialog(this,"win",true);
        dialog.setLayout(new BorderLayout());
        JLabel lbl1 = new JLabel();
        if (this.id == id)
            lbl1.setText("Игра закончилась. Поздравляю, Вы стали победителем!");
        else
            lbl1.setText("Игра закончилась. Победу одержал игрок " + id);
        dialog.add(lbl1);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    public void updPlNames(){
        for (int i = 0; i < 4; i++){
            pl_name[i].setText("Игрок " + player.get(i).name);
        }
    }
    public void createTradeDialog(GameWindow board, int id, boolean modal) throws IOException {
        JDialog dialog = new JDialog(board, Integer.toString(id), modal);
        dialog.setLayout(new BorderLayout());

        ArrayList<String> items = new ArrayList<>();
        for (int i = 0;i < 4; i++){
            if (id != i)
                items.add(player.get(i).name);
        }
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                String item = (String)box.getSelectedItem();

                JPanel pnl1 = new JPanel(new BorderLayout());
                pnl1.setPreferredSize(new Dimension(150,400));
                JLabel lbl1 = new JLabel("Игрок " + item);
                JTextArea jm1 = new JTextArea("0",1,1);

                DefaultListModel<String> dlm1 = new DefaultListModel<String>();
                for (int i = 0; i < 40; i++){
                    if (cards[i] instanceof PropertyCard && !((PropertyCard)cards[i]).isFree && ((PropertyCard)cards[i]).owner.name.equals(item))
                        dlm1.addElement(cards[i].getName());
                }
                JList<String> multiple1 = new JList<String>(dlm1);
                multiple1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                int[] select1 = {};
                multiple1.setSelectedIndices(select1);
                pnl1.add(lbl1,BorderLayout.NORTH);
                pnl1.add(new JScrollPane(multiple1),BorderLayout.CENTER);
                pnl1.add(jm1,BorderLayout.SOUTH);

                JPanel pnl2 = new JPanel(new BorderLayout());
                pnl2.setPreferredSize(new Dimension(150,400));
                JLabel lbl2 = new JLabel("Игрок " + player.get(id).name);
                JTextArea jm2 = new JTextArea("0",1,1);
                DefaultListModel<String> dlm2 = new DefaultListModel<String>();
                for (int i = 0; i < 40; i++){
                    if (cards[i] instanceof PropertyCard &&  !((PropertyCard)cards[i]).isFree && ((PropertyCard)cards[i]).owner.id == id)
                        dlm2.addElement(cards[i].getName());
                }
                JList<String> multiple2 = new JList<String>(dlm2);
                multiple2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                int[] select2 = {};
                multiple2.setSelectedIndices(select2);
                pnl2.add(lbl2,BorderLayout.NORTH);
                pnl2.add(new JScrollPane(multiple2),BorderLayout.CENTER);
                pnl2.add(jm2,BorderLayout.SOUTH);
                JPanel pnl = new JPanel();
                pnl.add(pnl1,BorderLayout.WEST);
                pnl.add(pnl2,BorderLayout.EAST);
                dialog.add(pnl,BorderLayout.CENTER);
                JButton btn = new JButton("Предложить обмен");
                dialog.add(btn,BorderLayout.SOUTH);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object[] pl1 = multiple1.getSelectedValues();
                        int[] pl1Cards = new int[pl1.length];
                        for (int i = 0; i < pl1.length;i++){
                            pl1Cards[i] = getCardByName((String) pl1[i]);
                        }
                        Object[] pl2 = multiple2.getSelectedValues();
                        int[] pl2Cards = new int[pl2.length];
                        for (int i = 0; i < pl2.length;i++){
                            pl2Cards[i] = getCardByName((String) pl2[i]);
                        }
                        int pl1Money = Integer.parseInt(jm1.getText());
                        int pl2Money = Integer.parseInt(jm2.getText());
                        int id1 = getPlByName(item);

                        Gson gson = new Gson();
                        Messages.TradeMsg tradeMsg = new Messages.TradeMsg();
                        tradeMsg.idFrom = id;
                        tradeMsg.idTo = id1;
                        tradeMsg.cardsFrom = pl2Cards;
                        tradeMsg.cardsTo = pl1Cards;
                        tradeMsg.moneyFrom = pl2Money;
                        tradeMsg.moneyTo = pl1Money;
                        tradeMsg.response = -1;

                        Common.writeBytes(socketOut, gson.toJson(tradeMsg));
                        dialog.dispose();
                    }
                });
            }
        };

        JComboBox comboBox = new JComboBox(items.toArray());
        comboBox.addActionListener(actionListener);
        dialog.add(comboBox,BorderLayout.NORTH);

        dialog.setPreferredSize(new Dimension(350, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    public void createAnswDialog(Messages.TradeMsg tradeMsg, GameWindow board, int id, boolean modal) throws IOException {
        JDialog dialog = new JDialog(board, Integer.toString(id), modal);
        dialog.setLayout(new BorderLayout());

        JPanel pnl1 = new JPanel(new BorderLayout());
        pnl1.setPreferredSize(new Dimension(150,400));
        JLabel lbl1 = new JLabel("Игрок " + player.get(tradeMsg.idFrom).name);
        JTextArea jm1 = new JTextArea(Integer.toString(tradeMsg.moneyFrom),1,1);

        DefaultListModel<String> dlm1 = new DefaultListModel<String>();
        for (int i = 0; i < tradeMsg.cardsFrom.length; i++){
            dlm1.addElement(cards[tradeMsg.cardsFrom[i]].getName());
        }
        JList<String> multiple1 = new JList<String>(dlm1);
        multiple1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int[] select1 = {};
        multiple1.setSelectedIndices(select1);
        pnl1.add(lbl1,BorderLayout.NORTH);
        pnl1.add(new JScrollPane(multiple1),BorderLayout.CENTER);
        pnl1.add(jm1,BorderLayout.SOUTH);

        JPanel pnl2 = new JPanel(new BorderLayout());
        pnl2.setPreferredSize(new Dimension(150,400));
        JLabel lbl2 = new JLabel("Игрок " + player.get(tradeMsg.idTo).name);
        JTextArea jm2 = new JTextArea(Integer.toString(tradeMsg.moneyTo),1,1);
        DefaultListModel<String> dlm2 = new DefaultListModel<String>();
        for (int i = 0; i < tradeMsg.cardsTo.length; i++){
            dlm2.addElement(cards[tradeMsg.cardsTo[i]].getName());
        }
        JList<String> multiple2 = new JList<String>(dlm2);
        multiple2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int[] select2 = {};
        multiple2.setSelectedIndices(select2);
        pnl2.add(lbl2,BorderLayout.NORTH);
        pnl2.add(new JScrollPane(multiple2),BorderLayout.CENTER);
        pnl2.add(jm2,BorderLayout.SOUTH);
        JPanel pnl = new JPanel();
        pnl.add(pnl1,BorderLayout.WEST);
        pnl.add(pnl2,BorderLayout.EAST);

        dialog.add(pnl,BorderLayout.CENTER);

        JPanel btn_pnl = new JPanel();
        JButton btn1 = new JButton("Согласиться");
        JButton btn2 = new JButton("Отказаться");
        btn_pnl.add(btn1);
        btn_pnl.add(btn2);
        dialog.add(btn_pnl,BorderLayout.SOUTH);

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                Messages.TradeMsg tradeMsg1 = new Messages.TradeMsg();
                tradeMsg1 = tradeMsg;
                tradeMsg1.response = 1;

                Common.writeBytes(socketOut, gson.toJson(tradeMsg));
                dialog.dispose();
            }
        });
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                Messages.TradeMsg tradeMsg1 = new Messages.TradeMsg();
                tradeMsg1 = tradeMsg;
                tradeMsg1.response = 0;

                Common.writeBytes(socketOut, gson.toJson(tradeMsg));
                dialog.dispose();
            }
        });

        dialog.setPreferredSize(new Dimension(350, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    public void getTrade(Messages.TradeMsg tradeMsg){
        player.get(tradeMsg.idFrom).pay(tradeMsg.moneyFrom);
        player.get(tradeMsg.idFrom).getPaid(tradeMsg.moneyTo);

        player.get(tradeMsg.idTo).pay(tradeMsg.moneyTo);
        player.get(tradeMsg.idTo).getPaid(tradeMsg.moneyFrom);

        for(int i = 0; i < tradeMsg.cardsFrom.length; i++){
            ((PropertyCard)cards[tradeMsg.cardsFrom[i]]).owner = player.get(tradeMsg.idTo);
            if (cards[tradeMsg.cardsFrom[i]] instanceof UtilityCard) {
                player.get(tradeMsg.idFrom).utilities.remove((UtilityCard)cards[tradeMsg.cardsFrom[i]]);
                player.get(tradeMsg.idTo).utilities.add((UtilityCard)cards[tradeMsg.cardsFrom[i]]);
            } else if (cards[tradeMsg.cardsFrom[i]] instanceof RailroadCard) {
                player.get(tradeMsg.idFrom).railroads.remove((RailroadCard)cards[tradeMsg.cardsFrom[i]]);
                player.get(tradeMsg.idTo).railroads.add((RailroadCard)cards[tradeMsg.cardsFrom[i]]);
            } else {
                player.get(tradeMsg.idFrom).estates.remove((EstateCard) cards[tradeMsg.cardsFrom[i]]);
                player.get(tradeMsg.idTo).estates.add((EstateCard) cards[tradeMsg.cardsFrom[i]]);
            }
        }
        for(int i = 0; i < tradeMsg.cardsTo.length; i++){
            ((PropertyCard)cards[tradeMsg.cardsTo[i]]).owner = player.get(tradeMsg.idFrom);
            if (cards[tradeMsg.cardsTo[i]] instanceof UtilityCard) {
                player.get(tradeMsg.idTo).utilities.remove((UtilityCard)cards[tradeMsg.cardsTo[i]]);
                player.get(tradeMsg.idFrom).utilities.add((UtilityCard)cards[tradeMsg.cardsTo[i]]);
            } else if (cards[tradeMsg.cardsTo[i]] instanceof RailroadCard) {
                player.get(tradeMsg.idTo).railroads.remove((RailroadCard)cards[tradeMsg.cardsTo[i]]);
                player.get(tradeMsg.idFrom).railroads.add((RailroadCard)cards[tradeMsg.cardsTo[i]]);
            } else {
                player.get(tradeMsg.idTo).estates.remove((EstateCard) cards[tradeMsg.cardsTo[i]]);
                player.get(tradeMsg.idFrom).estates.add((EstateCard) cards[tradeMsg.cardsTo[i]]);
            }
        }
    }
    public void initGameBoard(){
        cards[0] = new FreeParkingCard(0,"Поле вперед",0,0);
        cards[1] = new EstateCard(1,"Старая дорога",90,0,60,1,2,2,10,30,90,160,250,50,50);
        cards[2] = new FreeParkingCard(2,"Казна",148,0);
        cards[3] = new EstateCard(3,"Главное шоссе",206,0,60,1,2,4,20,60,180,320,450,50,50);
        cards[4] = new PayCard(4,"Налог с доходов",264,0, 200);
        cards[5] = new RailroadCard(5,"Западный морской порт",322,0,200);
        cards[6] = new EstateCard(6,"Аквапарк",380,0,100,2,3,6,30,90,270,400,550,50,50);
        cards[7] = new FreeParkingCard(7,"Шанс",438,0);
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
        cards[17] = new FreeParkingCard(17,"Казна",610,438);
        cards[18] = new EstateCard(18,"Проспект мира",610,496,180,4,3,10,50,150,450,625,750,100,100);
        cards[19] = new EstateCard(19,"Проспект Победы",610,554,200,4,3,16,80,220,600,800,1000,100,100);
        cards[20] = new FreeParkingCard(20,"Бесплатная парковка",610,610);
        //
        cards[21] = new EstateCard(21,"Бар",552,610,220,5,3,18,90,250,700,875,1050,150,150);
        cards[22] = new FreeParkingCard(22,"Шанс",494,610);
        cards[23] = new EstateCard(23,"Ночной клуб",436,610,220,5,3,18,90,250,700,875,1050,150,150);
        cards[24] = new EstateCard(24,"Ресторан",378,610,240,5,3,20,100,300,750,925,1100,150,150);
        cards[25] = new RailroadCard(25,"Восточный морской порт",320,610,200);

        cards[26] = new EstateCard(26,"Компьютеры",262,610,260,6,3,22,110,330,800,975,1150,150,150);
        cards[27] = new EstateCard(27,"Интернет",204,610,260,6,3,22,110,330,800,975,1150,150,150);
        cards[28] = new UtilityCard(28,"Водопроводная компания",146,610,150);
        cards[29] = new EstateCard(29,"Сотовая связь",90,610,280,6,3,24,120,360,850,1025,1200,150,150);
        cards[30] = new FreeParkingCard(30,"Отправляйтесь в тюрьму",0,610);
        cards[31] = new EstateCard(31,"Морские перевозки",0,552,300,7,3,26,130,390,900,1100,1275,200,200);
        cards[32] = new EstateCard(32,"Железная дорога",0,494,300,7,3,26,130,390,900,1100,1275,200,200);
        cards[33] = new FreeParkingCard(33,"Казна",0,436);
        cards[34] = new EstateCard(34,"Авиалинии",0,378,320,7,3,28,150,450,1000,1200,1400,200,200);
        cards[35] = new RailroadCard(35,"Южный морской порт",0,320,200);
        cards[36] = new FreeParkingCard(36,"Шанс",0,262);
        cards[37] = new EstateCard(37,"Курортная зона",0,204,350,8,2,35,175,500,1100,1300,1500,200,200);
        cards[38] = new PayCard(38,"Дорогая покупка",0,146,100);
        cards[39] = new EstateCard(39,"Гостиничный комплекс",0,90,400,8,2,50,200,600,1400,1700,2000,200,200);
    }
    public void initPlNames(Messages.InitMsg initMsg){
        player.get(0).name = initMsg.name0;
        player.get(1).name = initMsg.name1;
        player.get(2).name = initMsg.name2;
        player.get(3).name = initMsg.name3;
        for (int i = 0; i<4; i++){
            if (name.equals(player.get(i).name))
                this.id = i;
        }
    }
    public void updPl(Messages.UpdPlMsg plMsg){
        int id = plMsg.id;
        Player pl = player.get(id);
        pl.name = plMsg.name;
        pl.money = plMsg.money;
        pl.position = plMsg.position;
        pl.inPrison = plMsg.inPrison;
        pl.skipNum = plMsg.skipNum;
    }
    public void updCard(Messages.UpdCardMsg updMsg){
        int cardId = updMsg.cardID;
        ((PropertyCard)cards[cardId]).isFree = updMsg.isFree;
        ((PropertyCard)cards[cardId]).inDeposit = updMsg.inDeposit;
        if (! updMsg.isFree) {
            if (((PropertyCard) cards[cardId]).owner != null) {
                if (cards[cardId] instanceof UtilityCard)
                    ((PropertyCard) cards[cardId]).owner.utilities.remove(cards[cardId]);
                else if (cards[cardId] instanceof RailroadCard)
                    ((PropertyCard) cards[cardId]).owner.railroads.remove(cards[cardId]);
                else if (cards[cardId] instanceof EstateCard)
                    ((PropertyCard) cards[cardId]).owner.estates.remove(cards[cardId]);
            }
            ((PropertyCard) cards[cardId]).owner = player.get(updMsg.owner);
            if (cards[cardId] instanceof UtilityCard)
                ((PropertyCard) cards[cardId]).owner.utilities.add((UtilityCard) cards[cardId]);
            else if (cards[cardId] instanceof RailroadCard)
                ((PropertyCard) cards[cardId]).owner.railroads.add((RailroadCard) cards[cardId]);
            else if (cards[cardId] instanceof EstateCard)
                ((PropertyCard) cards[cardId]).owner.estates.add((EstateCard) cards[cardId]);
        }
        if (cards[cardId] instanceof EstateCard) {
            ((EstateCard) cards[cardId]).hotelCount = updMsg.hotelCount;
            ((EstateCard) cards[cardId]).houseCount = updMsg.houseCount;
        }
    }


    public void buyBtnPressed(){
        Gson gson = new Gson();
        Messages.BuyMsg buyMsg = new Messages.BuyMsg();
        buyMsg.idCard = player.get(id).position;
        buyMsg.idPlayer = id;
        Common.writeBytes(socketOut, gson.toJson(buyMsg));
    }
    public void rollBtnPressed(){
        Gson gson = new Gson();
        int dice1 = (int)(Math.random() * 5 + 1);
        int dice2 = (int)(Math.random() * 5 + 1);
        Messages.MoveMsg moveMessage = new Messages.MoveMsg();
        moveMessage.idPlayer = id;
        moveMessage.Dice1 = dice1;
        moveMessage.Dice2 = dice2;
        Common.writeBytes(socketOut, gson.toJson(moveMessage));
    }


    public void depositCardBtnPressed(int cardId){
        Gson gson = new Gson();
        Messages.DepositMsg depositMsg = new Messages.DepositMsg();
        depositMsg.idCard = cardId;
        depositMsg.idPlayer = id;
        Common.writeBytes(socketOut, gson.toJson(depositMsg));
    }
    public void ransomCardBtnPressed(int cardId){
        Gson gson = new Gson();
        Messages.RansomMsg ransomMsg = new Messages.RansomMsg();
        ransomMsg.idCard = cardId;
        ransomMsg.idPlayer = id;
        Common.writeBytes(socketOut, gson.toJson(ransomMsg));
    }
    public void buyHouseBtnPressed(int cardId){
        Gson gson = new Gson();
        Messages.BuildMsg buildMsg = new Messages.BuildMsg();
        buildMsg.idCard = cardId;
        buildMsg.idPlayer = id;
        Common.writeBytes(socketOut, gson.toJson(buildMsg));
    }
    public void sellHouseBtnPressed(int cardId){
        Gson gson = new Gson();
        Messages.SellMsg sellMsg = new Messages.SellMsg();
        sellMsg.idCard = cardId;
        sellMsg.idPlayer = id;
        Common.writeBytes(socketOut, gson.toJson(sellMsg));
    }

    public int getCardByName(String name){
        for (int i = 0; i < 40; i++){
            if(cards[i].getName().equals(name))
                return i;
        }
        return -1;
    }
    public int getPlByName(String name){
        for (Player pl:player) {
            if(pl.name.equals(name))
                return pl.id;
        }
        return -1;
    }

}