package dev.elayachi.exercice7;

public class GestionPaiementApp {
    public static void main(String[] args) {
        Commande commande1 = new Commande(1250.50,
                new CarteCredit("TX-001", "4539 1488 0343 6467"));
        Commande commande2 = new Commande(890.00,
                new PayPal("TX-002", "client@gmail.com"));
        Commande commande3 = new Commande(320.75,
                new CarteCredit("TX-003", "5500 0055 5555 5559"));

        commande1.processPayment();
        commande2.processPayment();
        commande3.processPayment();
    }
}
