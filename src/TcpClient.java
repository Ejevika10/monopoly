import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {
    TcpClient(String ip, int port, String name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ip,port);
                    System.out.println("connected.\n");

                    // получаем потоки для чтения и записи в сокет
                    OutputStream out = socket.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    GameWindow gamewindow = new GameWindow(name,-1,out);
                    gamewindow.pack();
                    gamewindow.setVisible(true);
                    gamewindow.repaint();

                    Gson gson1 = new Gson();
                    Messages.MyNameMsg myName = new Messages.MyNameMsg();
                    myName.name = name;
                    Common.writeBytes(out, gson1.toJson(myName));

                    while(true){
                        String fromServer = Common.readBytes(in);
                        System.out.println(fromServer);

                        JsonObject jsonObject = new JsonParser().parse(fromServer).getAsJsonObject();
                        Messages.MsgType msgType = Messages.MsgType.valueOf(jsonObject.get("type").getAsString());

                        Gson gson = new Gson();

                        switch (msgType){
                            case InitMsg:
                                Messages.InitMsg initMsg = gson.fromJson(fromServer, Messages.InitMsg.class);
                                gamewindow.initPlNames(initMsg);
                                gamewindow.updPlNames();
                                break;
                            case StartMsg:
                                Messages.StartMsg startMsg = gson.fromJson(fromServer, Messages.StartMsg.class);
                                gamewindow.updTurn(startMsg);
                                break;
                            case UpdCardMsg:
                                Messages.UpdCardMsg updCardMsg = gson.fromJson(fromServer, Messages.UpdCardMsg.class);
                                gamewindow.updCard(updCardMsg);
                                break;
                            case UpdPlMsg:
                                Messages.UpdPlMsg updPlMsg = gson.fromJson(fromServer, Messages.UpdPlMsg.class);
                                gamewindow.updPl(updPlMsg);
                                break;
                            case TradeMsg:
                                Messages.TradeMsg tradeMsg = gson.fromJson(fromServer, Messages.TradeMsg.class);
                                if (tradeMsg.response == -1) {
                                    gamewindow.createAnswDialog(tradeMsg, gamewindow, gamewindow.id, true);
                                }
                                else{
                                    gamewindow.getTrade(tradeMsg);
                                }
                                break;
                            case ServerMsg:
                                Messages.ServerMsg userMsg = gson.fromJson(fromServer, Messages.ServerMsg.class);
                                gamewindow.gamePanel.ta.append(userMsg.msg + "\n");
                                break;
                            case ExitMsg :
                                Messages.ExitMsg exitMsg = gson.fromJson(fromServer, Messages.ExitMsg.class);
                                gamewindow.exit(exitMsg.id);
                                break;
                            case WinMsg:
                                Messages.WinMsg winMsg = gson.fromJson(fromServer, Messages.WinMsg.class);
                                gamewindow.win(winMsg.id);
                                break;
                        }
                        gamewindow.updGUI();
                        gamewindow.gamePanel.repaint();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }
}