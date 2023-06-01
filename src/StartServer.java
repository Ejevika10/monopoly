import java.io.IOException;
import java.sql.SQLException;

public class StartServer {
    private final static int MAX_PLAYERS = 4;
    public static void main(String[] args) throws IOException, SQLException {
        TcpServer server = new TcpServer(8989);

        int playerCount = 0;

        while (playerCount != MAX_PLAYERS) {
            server.connect();
            playerCount += 1;
        }
        System.out.println("start game");
        server.start();
    }
}
