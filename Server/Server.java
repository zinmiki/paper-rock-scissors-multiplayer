import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static class PlayerHandler {
        Socket socket;
        PrintWriter out;
        BufferedReader in;

        String playerName;

        PlayerHandler(Socket socket, String playerName) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            this.playerName = playerName;
        }

        void send(String msg) {
            out.println(msg);
        }
        String receive() throws IOException {
            return in.readLine();
        }
    }
    // Lists
    public static List<PlayerHandler> players = new ArrayList<>();
    public static Map<PlayerHandler, String> playerItems = new HashMap<>();

    // Main method to start the server
    public static void main(String[] args) {
        String mode = null;
        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <players_count>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int playersCount = Integer.parseInt(args[1]);

        System.out.println("Server starting on port " + port);
        System.out.println("Waiting for " + playersCount + " players to connect...");

        // Accept player connections and create PlayerHandler instances
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            for (int i = 0; i < playersCount; i++) {
                Socket playerSocket = serverSocket.accept();
                String playerName = "Player" + (i + 1);
                // Create a new PlayerHandler for the connected player
                PlayerHandler playerHandler = new PlayerHandler(playerSocket, playerName);
                System.out.println(playerName + " connected");
                // Add the player to the list
                players.add(playerHandler);
            }
        
        
        } catch (IOException e) {
            System.err.println("Error while starting the server: " + e.getMessage());
            return;
        }

        // Waiting for players
        while (playersCount == players.size()) {
            System.out.println("Game starting with " + players.size() + " players.");
            break;
        }

        // Game logic can be added here
        for (PlayerHandler p : players) {
                p.send("ITEM");
                mode = "ITEM";
            }
        while (true) {
            // Check if player is disconnected
            players.removeIf(player -> {
                boolean disconnected = false;
                try {
                    // Try to send a ping message to check connection
                    player.out.println("PING");
                    player.out.flush();
                    if (player.socket.isClosed() || !player.socket.isConnected() || player.out.checkError()) {
                        disconnected = true;
                    }
                } catch (Exception e) {
                    disconnected = true;
                }
                if (disconnected) {
                    System.out.println(player.playerName + " disconnected due to an error.");
                    try {
                        player.socket.close();
                    } catch (IOException e) {
                        System.err.println("Error closing socket for " + player.playerName + ": " + e.getMessage());
                    }
                }
                return disconnected;
            });
            // Read player responses
            if (mode == "ITEM") {
                
                System.out.println("Waiting for players to select items...");
                for (PlayerHandler player : players) {
                    try {
                        String response = player.receive();
                        if (response != null) {
                            System.out.println(player.playerName + " selected: " + response);
                            // Here you can add logic to handle the player's selection
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading from " + player.playerName + ": " + e.getMessage());
                    }
                }
                System.out.println("All players have selected their items.");
                mode = "null"; // Reset mode after processing ITEM responses
                // break;
            }
            try {
                Thread.sleep(1000); // Add a 1-second delay between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
