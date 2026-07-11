package dev.elayachi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientIp = socket.getInetAddress().getHostAddress();
        System.out.println("Thread " + Thread.currentThread().getName() + " traite le client " + clientIp);

        try (Socket clientSocket = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Bienvenue sur le serveur ! (hello, time, bye)");

            String message;
            while ((message = in.readLine()) != null) {
                message = message.trim();
                System.out.println("Message reçu : " + message + " (client " + clientIp
                        + ", thread " + Thread.currentThread().getName() + ")");

                switch (message.toLowerCase()) {
                    case "hello" -> out.println("Bonjour client !");
                    case "time" -> out.println(LocalDateTime.now().format(FORMATTER));
                    case "bye" -> {
                        out.println("Connexion fermée");
                        System.out.println("Client déconnecté : " + clientIp);
                        return;
                    }
                    default -> out.println("Message reçu : [" + message + "]");
                }
            }
            System.out.println("Client déconnecté : " + clientIp);
        } catch (IOException e) {
            System.err.println("Erreur avec le client " + clientIp + " : " + e.getMessage());
        }
    }
}
