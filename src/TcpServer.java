import Cards.EstateCard;
import Cards.PropertyCard;
import model.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TcpServer {
    public ServerSocket serverSocket;
    public List<PlayerHandler> clients;
    public GameBoard gameboard;

    TcpServer(int port){
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Порт занят: " + port);
        }
        clients =  Collections.synchronizedList(new ArrayList<>());
        gameboard = new GameBoard(this);
    }
    public void connect() throws IOException {
        try {
            System.out.println("waiting for client");
            Socket socket = serverSocket.accept();
            System.out.println("connected");
            int id = gameboard.players.size();
            Player player = new Player(id);
            gameboard.players.add(player);
            gameboard.plCount++;

            PlayerHandler plHandler = new PlayerHandler(socket, this, id);
            clients.add(plHandler);
            plHandler.start();
        } catch (Exception e) {
            serverSocket.close();
        }
    }
    public void start(){
        System.out.println("Game started...\n");
        sendInit();
        sendStart();
        for (PlayerHandler client : clients) {
            client.start();
        }
    }
    public void exit(int id){
        Player player = gameboard.players.get(id);
        player.inGame = false;
        gameboard.plCount--;
        for (int i = 0 ; i < 40;i++){
            if(gameboard.cards[i] instanceof PropertyCard && !((PropertyCard)gameboard.cards[i]).isFree && ((PropertyCard)gameboard.cards[i]).owner.id == id){
                ((PropertyCard) gameboard.cards[i]).soldBack();
            }
        }
        Messages.ExitMsg exitMsg = new Messages.ExitMsg();
        exitMsg.id = id;
        sendToAll(exitMsg);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendServerMsg("Игрок " + player.name + " вышел из игры ");
        if (gameboard.plCount == 1){
            Messages.WinMsg winMsg = new Messages.WinMsg();
            for (Player pl : gameboard.players) {
                if(pl.inGame) {
                    winMsg.id = pl.id;
                    break;
                }
            }
            sendToAll(winMsg);
            totalExit(winMsg.id);
        }else if (gameboard.getActivePlayer() == id){
            gameboard.changeActivePlayer();
            sendServerMsg("Игрок " + gameboard.players.get(gameboard.getActivePlayer()).name + " начинает ход ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendStart();
        }
    }
    public void totalExit(int id){
        if (gameboard.players.get(id).inGame){
            exit(id);
        }
        clients.removeIf(client -> client.id == id);
    }
    void sendInit(){
        Messages.InitMsg initMsg = new Messages.InitMsg();
        initMsg.name0 = gameboard.players.get(0).name;
        initMsg.name1 = gameboard.players.get(1).name;
        initMsg.name2 = gameboard.players.get(2).name;
        initMsg.name3 = gameboard.players.get(3).name;
        sendToAll(initMsg);
    }
    public void sendServerMsg(String msg) {
        Messages.ServerMsg serverMsg = new Messages.ServerMsg();
        serverMsg.msg = msg;
        sendToAll(serverMsg);
    }
    public void sendPl(int id){
        Messages.UpdPlMsg updPlMsg = new Messages.UpdPlMsg();
        Player pl = gameboard.players.get(id);
        updPlMsg.id = pl.id;
        updPlMsg.name = pl.name;
        updPlMsg.money = pl.money;
        updPlMsg.position = pl.position;
        updPlMsg.inPrison = pl.inPrison;
        updPlMsg.skipNum = pl.skipNum;
        sendToAll(updPlMsg);
    }
    public void sendCard(int cardId){
        Messages.UpdCardMsg updCardMsg = new Messages.UpdCardMsg();
        updCardMsg.cardID = cardId;
        updCardMsg.isFree = ((PropertyCard)gameboard.cards[cardId]).isFree;
        if (!updCardMsg.isFree)
        {
            updCardMsg.owner = ((PropertyCard)gameboard.cards[cardId]).owner.id;
        }
        else
            updCardMsg.owner = -1;
        if (gameboard.cards[cardId] instanceof EstateCard) {
            updCardMsg.hotelCount = ((EstateCard) gameboard.cards[cardId]).hotelCount;
            updCardMsg.houseCount = ((EstateCard) gameboard.cards[cardId]).houseCount;
        }
        sendToAll(updCardMsg);
    }
    public void sendStart(){
        Messages.StartMsg startMsg = new Messages.StartMsg();
        startMsg.activeId = gameboard.getActivePlayer();
        sendToAll(startMsg);
    }
    public void sendToAll(Object msg){
        for (PlayerHandler client : clients) {
            client.sendMsg(msg);
        }
    }

}
