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

    public static int winsP = 0;
    public static int winsK = 0;
    public static int winsN = 0;

    // Main method to start the server
    public static void main(String[] args) {
        String mode = "NEW";
        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <players_count>");
            return;
        } 
        int limRounds = -1;
        if (args.length >= 3) {
            limRounds = Integer.parseInt(args[2]);
            System.out.println("Limited rounds enabled ("+ limRounds +")");
        }
        int port = Integer.parseInt(args[0]);
        int playersCount = Integer.parseInt(args[1]);
        // int port = 12345;
        // int playersCount = 4;

        System.out.println("Server starting on port " + port);
        System.out.println("Waiting for " + playersCount + " players to connect...");

        // Accept player connections and create PlayerHandler instances
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            for (int i = 0; i < playersCount; i++) {
                Socket playerSocket = serverSocket.accept();
                String playerName = "Player" + (i + 1);
                // Create a new PlayerHandler for the connected player
                PlayerHandler playerHandler = new PlayerHandler(playerSocket, playerName);
                System.out.println(playerName + " connected ( ID=" + playerHandler.hashCode() + ")");
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
            if (mode == "NEW") {
                limRounds--;
                for (PlayerHandler p : players) {
                    if (limRounds == -1) {
                        System.out.println("Round limit! Kicking all players.");
                        p.send("LIMIT");
                        p.send("KICK");
                        mode = "null";
                    } else {
                        p.send("ITEM");
                        mode = "ITEM";
                    }
                }
            }
            if (mode == "null") {
                System.out.println("Succesfully kicked players");
                System.out.print("Closing server.. ");
                break;
            }
            // Read player responses
            if (mode == "ITEM") {
                playerItems.clear();
                System.out.println("Waiting for players to select items...");
                for (PlayerHandler player : players) {
                    try {
                        String response = player.receive();
                        if (response != null) {
                            System.out.println(player.playerName + " selected: " + response);
                            playerItems.put(player, response);
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading from " + player.playerName + ": " + e.getMessage());
                    }
                }
                System.out.println("All players have selected their items.");
                mode = "CH_WINNER"; // Reset mode after processing ITEM responses
                // break;
            }
            if (mode == "CH_WINNER") {
                String winner = checkMatchWinner(playerItems);
                if (winner != null) {
                    switch (winner) {
                        case "p" -> winsP++;
                        case "r" -> winsK++;
                        case "s" -> winsN++;
                    }
                }
                for (PlayerHandler p : players) {
                    if (winner != null) {
                        p.send("WIN_"+winner);
                    } else {
                        p.send("DRAW");
                    }
                    p.send(winsP+"/"+winsK+"/"+winsN);
                    mode = "NEW";
                }
            }
            try {
                if (mode == "NEW") {
                    System.out.println("Starting new round!");
                    Thread.sleep(3000); // Add a 3-second delay between round
                }
                Thread.sleep(1000); // Add a 1-second delay between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    public static String checkMatchWinner(Map<PlayerHandler, String> itemMap) {
        int countP = 0, countK = 0, countN = 0;
        int winP = 0, winK = 0, winN =0;

        for (String value : itemMap.values()) {
            if ("p".equals(value)) countP++;
            else if ("r".equals(value)) countK++;
            else if ("s".equals(value)) countN++;
        }

        // Match
        while (countP > 0) {
            if (countK > 0) {
                winP++;
                countP--;
                countK--;
            } else break;
        }
        while (countK > 0) {
            if (countN > 0) {
                winK++;
                countK--;
                countN--;
            } else break;
        }
        while (countN > 0) {
            if (countP > 0) {
                winN++;
                countN--;
                countP--;
            } else break;
        }

        String winner;

        if (winP > winK && winP > winN) {
            winner = "p";
        } else if (winK > winP && winK > winN) {
            winner = "r";
        } else if (winN > winP && winN > winK) {
            winner = "s";
        } else {
            winner = null;
            System.out.println("Draw");
        }

        System.out.println("The winners are " + winner);

        return winner;
    }
}
