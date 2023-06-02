import java.io.*;
import java.net.Socket;

import Cards.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Player;

public class PlayerHandler extends Thread{
    private final OutputStream m_out;
    private final BufferedReader m_in;
    public final int id;
    public TcpServer server;
    public Socket socket;
    PlayerHandler(Socket socket, TcpServer server, int id) throws IOException {
        this.server = server;
        this.socket = socket;
        this.m_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.m_out = socket.getOutputStream();
        this.id = id;
        String Buf = readMsg();
        System.out.println(Buf);
        Messages.MsgType msgType = chekMsgType(Buf);
        if (msgType == Messages.MsgType.MyNameMsg){
            Gson gson = new Gson();
            Messages.MyNameMsg myName = gson.fromJson(Buf, Messages.MyNameMsg.class);
            server.gameboard.players.get(id).name = myName.name;
        }
    }
    public void sendMsg(Object msg) {
        Gson gson = new Gson();
        Common.writeBytes(m_out, gson.toJson(msg));
        System.out.println(gson.toJson(msg) + "   " + id);
    }
    String readMsg(){
        try{
            return Common.readBytes(m_in);
        } catch (IOException e) {
            server.totalExit(id);
            return "stop";
        }
    }

    public void run(){
        try{
            while(true){
                String Buf = readMsg();
                if (Buf.equals("stop"))
                    break;
                Messages.MsgType msgType = chekMsgType(Buf);
                Gson gson = new Gson();
                switch (msgType) {
                    case UserMsg -> {
                        Messages.UserMsg msg = gson.fromJson(Buf, Messages.UserMsg.class);
                        server.sendServerMsg(msg.userName + ": " + msg.msg);
                    }
                    case MoveMsg -> {
                        Messages.MoveMsg currentMove = gson.fromJson(Buf, Messages.MoveMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(currentMove.idPlayer).name + " выбросил " + currentMove.Dice1 + "   " + currentMove.Dice2);
                        if(currentMove.Dice1 == currentMove.Dice2 && server.gameboard.players.get(currentMove.idPlayer).getDouble()){
                            server.sendServerMsg("Игрок " + server.gameboard.players.get(currentMove.idPlayer).name + " отправляется в тюрьму");
                            server.sendPl(currentMove.idPlayer);
                        }
                        else {
                            server.gameboard.move(currentMove);
                        }
                        if(currentMove.Dice1 != currentMove.Dice2)
                            server.gameboard.players.get(currentMove.idPlayer).lastDouble = false;
                    }
                    case BuyMsg -> {
                        Messages.BuyMsg buyMsg = gson.fromJson(Buf, Messages.BuyMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(buyMsg.idPlayer).name + " покупает поле " + server.gameboard.cards[buyMsg.idCard].getName());
                        server.gameboard.players.get(buyMsg.idPlayer).buy((PropertyCard) server.gameboard.cards[buyMsg.idCard]);
                        server.sendPl(buyMsg.idPlayer);
                        server.sendCard(buyMsg.idCard);
                    }
                    case DepositMsg -> {
                        Messages.DepositMsg depositMsg = gson.fromJson(Buf, Messages.DepositMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(depositMsg.idPlayer).name + " закладывает поле " + server.gameboard.cards[depositMsg.idCard].getName());
                        server.gameboard.players.get(depositMsg.idPlayer).depositCard((PropertyCard) server.gameboard.cards[depositMsg.idCard]);
                        server.sendPl(depositMsg.idPlayer);
                        server.sendCard(depositMsg.idCard);
                    }
                    case RansomMsg -> {
                        Messages.RansomMsg ransomMsg = gson.fromJson(Buf, Messages.RansomMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(ransomMsg.idPlayer).name + " выкупает поле " + server.gameboard.cards[ransomMsg.idCard].getName());
                        server.gameboard.players.get(ransomMsg.idPlayer).ransomCard((PropertyCard) server.gameboard.cards[ransomMsg.idCard]);
                        server.sendPl(ransomMsg.idPlayer);
                        server.sendCard(ransomMsg.idCard);
                    }
                    case BuildMsg -> {
                        Messages.BuildMsg buildMsg = gson.fromJson(Buf, Messages.BuildMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(buildMsg.idPlayer).name + " строит дом на поле " + server.gameboard.cards[buildMsg.idCard].getName());
                        server.gameboard.players.get(buildMsg.idPlayer).buyHouse((EstateCard) server.gameboard.cards[buildMsg.idCard]);
                        server.sendPl(buildMsg.idPlayer);
                        server.sendCard(buildMsg.idCard);
                    }
                    case SellMsg -> {
                        Messages.SellMsg sellMsg = gson.fromJson(Buf, Messages.SellMsg.class);
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(sellMsg.idPlayer).name + " продает здание на поле " + server.gameboard.cards[sellMsg.idCard].getName());
                        server.gameboard.players.get(sellMsg.idPlayer).sellHouse((EstateCard) server.gameboard.cards[sellMsg.idCard]);
                        server.sendPl(sellMsg.idPlayer);
                        server.sendCard(sellMsg.idCard);
                    }
                    case TradeMsg -> {
                        Messages.TradeMsg tradeMsg = gson.fromJson(Buf, Messages.TradeMsg.class);
                        switch (tradeMsg.response) {
                            case 0 -> {
                                server.sendServerMsg("Игрок " + server.gameboard.players.get(tradeMsg.idTo).name + " отклоняет предложение");
                            }
                            case 1 -> {
                                server.sendServerMsg("Игрок " + server.gameboard.players.get(tradeMsg.idTo).name + " принимает предложение");
                                server.gameboard.getTrade(tradeMsg);
                                server.sendToAll(tradeMsg);
                            }
                            case -1 -> {
                                server.clients.get(tradeMsg.idTo).sendMsg(tradeMsg);
                                server.sendServerMsg("Игрок " + server.gameboard.players.get(tradeMsg.idFrom).name + " предлагает обмен игроку " + server.gameboard.players.get(tradeMsg.idTo).name);
                            }
                        }
                    }
                    case EndMsg -> {
                        if(!server.gameboard.players.get(id).lastDouble)
                            server.gameboard.changeActivePlayer();
                        server.sendServerMsg("Игрок " + server.gameboard.players.get(server.gameboard.getActivePlayer()).name + " начинает ход ");
                        server.sendStart();
                    }
                    case ExitMsg -> {
                        Messages.ExitMsg exitMsg = gson.fromJson(Buf, Messages.ExitMsg.class);
                        server.exit(exitMsg.id);
                    }
                }
            }
        }catch (Exception e){
            try {
                e.printStackTrace();
                System.out.println("error occured");
                m_in.close();
                m_out.close();
                socket.close();
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    static Messages.MsgType chekMsgType(String buffer){
        JsonObject jsonObject = new JsonParser().parse(buffer).getAsJsonObject();
        return Messages.MsgType.valueOf(jsonObject.get("type").getAsString());
    }

}
