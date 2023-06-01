import Cards.*;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import static javax.imageio.ImageIO.read;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class BoardDisplay extends JPanel {
    GameWindow board;
    JTextField tf;
    JTextArea ta;
    Image field = new ImageIcon("img/board.png").getImage();
    BoardDisplay(GameWindow board){
        setLayout( new GridBagLayout());
        this.board = board;
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setPreferredSize(new Dimension(520,520));
        GridBagConstraints c = new GridBagConstraints();

        JPanel panel = new JPanel(); // панель не видна при выводе
        JLabel label = new JLabel("Введите текст");
        tf = new JTextField(30); // принимает до 10 символов
        JButton send = new JButton("Отправить");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                Messages.UserMsg userMsg = new Messages.UserMsg();
                userMsg.userName = board.name;
                userMsg.msg = tf.getText();
                Common.writeBytes(board.socketOut, gson.toJson(userMsg));
            }
        });
        panel.add(label); // Компоненты, добавленные с помощью макета Flow Layout
        panel.add(tf);
        panel.add(send);
        // Текстовая область по центру
        ta = new JTextArea(20,1);
        ta.setMaximumSize(new Dimension(520,400));
        ta.setEditable(false);
        ta.setFocusable(false);
        JScrollPane tPanel = new JScrollPane(ta);

        // Добавление компонентов в рамку.
        pnl.add(BorderLayout.CENTER, tPanel);
        pnl.add(BorderLayout.SOUTH, panel);
        add(pnl, c);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int id = isClickedIn(e.getX(),e.getY());
                if (id != -1 && board.cards[id] instanceof PropertyCard){
                    try {
                        JDialog dialog = createCardDialog(board,id,true);
                        dialog.pack();
                        dialog.setVisible(true);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

    public void paintComponent(Graphics g) {
        g.drawImage(field, 0, 0,700, 700, null);
        g.setColor(Color.WHITE);
        g.fillRect(90,90,520,520);
        g.drawRect(90,90,520,520);

        for (int i = 0; i < 40; i++){
            if (board.cards[i] instanceof PropertyCard && !((PropertyCard) board.cards[i]).isFree){
                int r =0; int gr=0; int b=0;
                switch (((PropertyCard) board.cards[i]).owner.id){
                    case 0:
                        r= board.player.get(0).colorR;
                        gr= board.player.get(0).colorG;
                        b= board.player.get(0).colorB;
                        break;
                    case 1:
                        r= board.player.get(1).colorR;
                        gr= board.player.get(1).colorG;
                        b= board.player.get(1).colorB;
                        break;
                    case 2:
                        r= board.player.get(2).colorR;
                        gr= board.player.get(2).colorG;
                        b= board.player.get(2).colorB;
                        break;
                    case 3:
                        r= board.player.get(3).colorR;
                        gr= board.player.get(3).colorG;
                        b= board.player.get(3).colorB;
                        break;

                }
                g.setColor(new Color(r,gr,b));
                g.fillRect(board.cards[i].posX + 5,board.cards[i].posY + 5,20,20);
                g.drawRect(board.cards[i].posX + 5,board.cards[i].posY + 5,20,20);

                if(board.cards[i] instanceof EstateCard && ((EstateCard) board.cards[i]).houseCount > 0){
                    Font font = new Font("Serif", Font.PLAIN, 24);
                    g.setFont(font);
                    g.setColor(Color.BLACK);
                    g.drawString(Integer.toString(((EstateCard) board.cards[i]).houseCount),board.cards[i].posX + 5,board.cards[i].posY + 5 + 20);
                }
            }
        }
        int r; int gr; int b;
        if(board.player.get(0).inGame){
            r= board.player.get(0).colorR;
            gr= board.player.get(0).colorG;
            b= board.player.get(0).colorB;
            g.setColor(new Color(r,gr,b));
            g.fillOval(board.cards[board.player.get(0).position].posX + 5,board.cards[board.player.get(0).position].posY + 5,20,20);
            g.drawOval(board.cards[board.player.get(0).position].posX + 5,board.cards[board.player.get(0).position].posY + 5,20,20);
        }
        if(board.player.get(1).inGame) {
            r = board.player.get(1).colorR;
            gr = board.player.get(1).colorG;
            b = board.player.get(1).colorB;
            g.setColor(new Color(r, gr, b));
            g.fillOval(board.cards[board.player.get(1).position].posX + 33, board.cards[board.player.get(1).position].posY + 5, 20, 20);
            g.drawOval(board.cards[board.player.get(1).position].posX + 33, board.cards[board.player.get(1).position].posY + 5, 20, 20);
        }
        if(board.player.get(2).inGame) {
            r = board.player.get(2).colorR;
            gr = board.player.get(2).colorG;
            b = board.player.get(2).colorB;
            g.setColor(new Color(r, gr, b));
            g.fillOval(board.cards[board.player.get(2).position].posX + 5, board.cards[board.player.get(2).position].posY + 33, 20, 20);
            g.drawOval(board.cards[board.player.get(2).position].posX + 5, board.cards[board.player.get(2).position].posY + 33, 20, 20);
        }
        if(board.player.get(3).inGame) {
            r = board.player.get(3).colorR;
            gr = board.player.get(3).colorG;
            b = board.player.get(3).colorB;
            g.setColor(new Color(r, gr, b));
            g.fillOval(board.cards[board.player.get(3).position].posX + 33, board.cards[board.player.get(3).position].posY + 33, 20, 20);
            g.drawOval(board.cards[board.player.get(3).position].posX + 33, board.cards[board.player.get(3).position].posY + 33, 20, 20);
        }
    }
    private JDialog createCardDialog(GameWindow board, int id, boolean modal) throws IOException {
        JDialog dialog = new JDialog(board, Integer.toString(id), modal);
        dialog.setLayout(new FlowLayout());
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300,590));
        String pathName = "img/cards/card" + id + ".png";
        JLabel jl = new JLabel(new ImageIcon(read(new File(pathName))));

        JButton btn_build = new JButton("Построить");
        btn_build.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.buyHouseBtnPressed(id);
            }
        });
        JButton btn_deposit = new JButton("Заложить");

        if(board.cards[id] instanceof PropertyCard&& ((PropertyCard)board.cards[id]).owner != null && ((PropertyCard)board.cards[id]).owner.id == board.id)
        {
            btn_deposit.setEnabled(true);
            if (board.cards[id] instanceof EstateCard &&
                    board.player.get(board.id).getGroupCount(((EstateCard) board.cards[id]).groupId) == ((EstateCard) board.cards[id]).groupMaxCount)
                btn_build.setEnabled(true);
            else
                btn_build.setEnabled(false);
        }
        else{
            btn_deposit.setEnabled(false);
            btn_build.setEnabled(false);
        }
        dialog.add(jl);
        dialog.add(btn_build);
        dialog.add(btn_deposit);
        return dialog;
    }
    public int isClickedIn(int x, int y){
        for (int i = 0; i < 9;i++){
            if(x >= 90 + 58*i && x <= 90 + 58*(i+1) && y < 90)
                return i+1;
        }
        for (int i = 0; i < 9;i++){
            if(x >= 610 && y > 90 + 58*i && y < 90 + 58*(i+1))
                return i+11;
        }
        for (int i = 0; i < 9;i++){
            if(x >= 610 - 58*(i+1) && x <= 610 - 58*i && y > 610)
                return i+21;
        }
        for (int i = 0; i < 9;i++){
            if(x <= 90 && y >= 610 - 58*(i+1) && y <= 610 - 58*i)
                return i+31;
        }
        return -1;
    }
}
