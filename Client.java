import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static String selectItem() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==Please select an item from the list:==");
        System.out.println("1. papier");
        System.out.println("2. kamien");
        System.out.println("3. nozyce");
        System.out.print("Select an item: ");

        String item = scanner.nextLine();

        System.out.println("You selected: " + item);
        return item;
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
            
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Read messages from the server
            while (true) {
                String msg = in.readLine();
                if (msg == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                switch (msg) {
                    case "ITEM" -> {
                        String item = selectItem();
                        out.println(item);
                    }
                    default -> System.out.println("Server direct message: " + msg);
                }
            }
        
        // Catch any IO exceptions that may occur during connection
        } catch (IOException e) {
            System.err.println("Error while connecting to the server: " + e.getMessage());
        }

    }
}