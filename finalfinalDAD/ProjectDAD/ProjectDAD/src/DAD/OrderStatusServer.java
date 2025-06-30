package DAD;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class OrderStatusServer {
    private static final int PORT = 12345;

    // Store all client writers to broadcast messages
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("✅ OrderStatusServer started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            clientWriters.add(writer); // Add this writer to broadcast list

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📦 Received update: " + line);
                broadcast(line); // Send to all other clients
            }

        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}

            // Remove closed clients
            clientWriters.removeIf(PrintWriter::checkError);
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message); // Send message to all connected clients
        }
    }
}


