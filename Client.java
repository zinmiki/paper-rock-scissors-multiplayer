import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void printStats(String input) {
        // Rozdzielamy ciąg po "/"
        String[] parts = input.split("/");

        // Sprawdzamy, czy mamy dokładnie 3 elementy
        if (parts.length != 3) {
            System.out.println("[ERROR] Unkown format data!");
            return;
        }

        // Wyświetlamy wynik
        System.out.println("== WINS STATS ==");
        System.out.println("Paper: " + parts[0]);
        System.out.println("Rocks: " + parts[1]);
        System.out.println("Scissors: " + parts[2]);
        System.out.println("=============");
    }

    public static String selectItem() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("==Please select an item from the list:==");
            System.out.println("1. paper");
            System.out.println("2. rock");
            System.out.println("3. scissors");
            System.out.print("Select an item: ");

            String item;
            if (scanner.hasNextLine()) {
                item = scanner.nextLine();
                //Przetwarzanie linii
            } else {
                System.out.println("Brak danych wejściowych.");
                scanner.reset();
                item = scanner.nextLine();
            }

            System.out.println("Waiting for player choises..");
            return item;
        }
    }
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Client <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server at " + host + ":" + port);
            System.out.println("\nWaiting for all players to connect...");
            
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Read messages from the server
            while (true) {
                String msg = in.readLine();
                if (msg == null) {
                    System.out.println("Server disconnected.");
                    break;
                }
                if (Character.isDigit(msg.charAt(0))) {
                    printStats(msg);
                } else {
                    switch (msg) {
                        case "ITEM" -> {
                            String item = selectItem();
                            out.println(item);
                        }
                        case "WIN_p" -> {
                            System.out.println("@ Paper wins!");
                        }
                        case "WIN_k" -> {
                            System.out.println("@ Rocks wins!");
                        }
                        case "WIN_n" -> {
                            System.out.println("@ Scissors wins!");
                        }
                        case "DRAW" -> {
                            System.out.println("@ Draw! No one won");
                        }
                        case "LIMIT" -> {
                            System.out.println("ROUND LIMIT! Thanks for playing. See you soon");
                        }
                        case "KICK" -> {
                            try {
                                System.out.println("\nYou are kicked from the server");
                                socket.close();
                                break;
                            } catch (IOException e) {
                                System.out.println("Failed to close socket "+ e.getMessage());
                            }
                        }
                        case "PING" -> {}
                        default -> System.out.println("Server direct message: " + msg);
                    }
                }
            }
        
        // Catch any IO exceptions that may occur during connection
        } catch (IOException e) {
            System.err.println("Error while connecting to the server: " + e.getMessage());
        }

    }
}