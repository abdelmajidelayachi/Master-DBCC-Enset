package dev.elayachi.app2;

import java.time.LocalDate;

public class Program {

    public static void main(String[] args) {
        Client client = new Client("AB123456", "El Amrani", "Youssef", "0612345678");

        Compte compte = new Compte("C-0001", LocalDate.of(2026, 1, 15), client);

        compte.ajouterOperation(new Operation(LocalDate.of(2026, 3, 10), 2000, Operation.VERSEMENT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 2, 1), 5000, Operation.VERSEMENT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 4, 5), 1500, Operation.RETRAIT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 3, 20), 800, Operation.RETRAIT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 5, 12), 3000, Operation.VERSEMENT));

        compte.afficherDetail();
    }
}
