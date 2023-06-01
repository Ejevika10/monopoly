import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class StartClient extends JFrame{
    public StartClient(){
        JDialog jd = new JDialog(this);
        jd.setLayout(new GridBagLayout());
        jd.setBounds(500, 300, 400, 300);

        JLabel ipName = new JLabel("Введите ip: ");
        JTextArea jip = new JTextArea("127.0.0.1",1,1);
        jip.setSize(75,10);
        JLabel portName = new JLabel("Введите порт: ");
        JTextArea jport = new JTextArea("8989",1,1);
        jport.setSize(75,10);
        JLabel Name = new JLabel("Введите имя: ");
        JTextArea jname = new JTextArea("    ",1,1);
        jname.setSize(75,10);
        JButton btn_start = new JButton("Подключиться");
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = jip.getText();
                int port = Integer.parseInt(jport.getText());
                String name = jname.getText().trim();
                dispose();
                TcpClient client = new TcpClient(ip,port,name);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);
        c.gridx = 0;
        c.gridy = 0;
        jd.add(ipName,c);
        c.gridx = 1;
        c.gridy = 0;
        jd.add(jip,c);

        c.gridx = 0;
        c.gridy = 1;
        jd.add(portName,c);
        c.gridx = 1;
        c.gridy = 1;
        jd.add(jport,c);

        c.gridx = 0;
        c.gridy = 2;
        jd.add(Name,c);
        c.gridx = 1;
        c.gridy = 2;
        jd.add(jname,c);

        c.gridx = 0;
        c.gridy = 3;
        jd.add(btn_start,c);

        jd.setVisible(true);
    }
    public static void main(String[] args) {
        StartClient StartClient = new StartClient();
    }
}

