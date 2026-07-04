package dev.elayachi.exercice1;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        IMetier<Produit> metier = new MetierProduitImpl();
        Scanner scanner = new Scanner(System.in);
        boolean quitter = false;

        while (!quitter) {
            System.out.println("\n===== Gestion des produits =====");
            System.out.println("1. Afficher la liste des produits");
            System.out.println("2. Rechercher un produit par son id");
            System.out.println("3. Ajouter un nouveau produit dans la liste");
            System.out.println("4. Supprimer un produit par id");
            System.out.println("5. Quitter ce programme");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    if (metier.getAll().isEmpty()) {
                        System.out.println("Aucun produit dans la liste.");
                    } else {
                        for (Produit produit : metier.getAll()) {
                            System.out.println(produit);
                        }
                    }
                    break;
                case 2:
                    System.out.print("Id du produit : ");
                    long idRecherche = scanner.nextLong();
                    scanner.nextLine();
                    Produit trouve = metier.findById(idRecherche);
                    if (trouve != null) {
                        System.out.println(trouve);
                    } else {
                        System.out.println("Aucun produit avec l'id " + idRecherche);
                    }
                    break;
                case 3:
                    System.out.print("Id : ");
                    long id = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Nom : ");
                    String nom = scanner.nextLine();
                    System.out.print("Marque : ");
                    String marque = scanner.nextLine();
                    System.out.print("Prix : ");
                    double prix = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Description : ");
                    String description = scanner.nextLine();
                    System.out.print("Nombre en stock : ");
                    int nombreEnStock = scanner.nextInt();
                    scanner.nextLine();
                    metier.add(new Produit(id, nom, marque, prix, description, nombreEnStock));
                    System.out.println("Produit ajoute avec succes.");
                    break;
                case 4:
                    System.out.print("Id du produit a supprimer : ");
                    long idSuppression = scanner.nextLong();
                    scanner.nextLine();
                    if (metier.findById(idSuppression) != null) {
                        metier.delete(idSuppression);
                        System.out.println("Produit supprime avec succes.");
                    } else {
                        System.out.println("Aucun produit avec l'id " + idSuppression);
                    }
                    break;
                case 5:
                    quitter = true;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez reessayer.");
            }
        }
        scanner.close();
    }
}
