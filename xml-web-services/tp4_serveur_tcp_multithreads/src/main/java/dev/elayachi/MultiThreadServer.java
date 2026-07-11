package dev.elayachi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {

    private static final int PORT = 5000;
    private static final int POOL_SIZE = 5;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getInetAddress().getHostAddress());
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
