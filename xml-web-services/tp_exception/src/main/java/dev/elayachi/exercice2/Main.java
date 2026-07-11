package dev.elayachi.exercice2;

import java.util.ArrayList;

public class Main {

    // recherche un compte par son numéro dans la liste,
    // lève CompteInexistantException s'il n'est pas trouvé
    public static CompteBancaire rechercherCompte(ArrayList<CompteBancaire> comptes, String numero)
            throws CompteInexistantException {
        for (CompteBancaire compte : comptes) {
            if (compte.getNumeroCompte().equals(numero)) {
                return compte;
            }
        }
        throw new CompteInexistantException("Le compte " + numero + " n'existe pas");
    }

    public static void main(String[] args) {
        ArrayList<CompteBancaire> comptes = new ArrayList<>();

        // 1. Ajouter des comptes
        System.out.println("===== Ajout des comptes =====");
        comptes.add(new CompteBancaire("C001", 1000, "Ahmed"));
        comptes.add(new CompteCourant("C002", 500, "Fatima", 1000));
        comptes.add(new CompteEpargne("C003", 2000, "Youssef", 0.03));
        for (CompteBancaire compte : comptes) {
            compte.afficherSolde();
        }

        // 2. Dépôt et retrait
        System.out.println("\n===== Dépôt et retrait =====");
        try {
            CompteBancaire c1 = rechercherCompte(comptes, "C001");
            c1.deposer(500);
            c1.retirer(300);
            c1.afficherSolde();

            // retrait supérieur au solde -> FondsInsuffisantsException
            c1.retirer(5000);
        } catch (FondsInsuffisantsException | CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 3. Le compte courant autorise un découvert
        System.out.println("\n===== Compte courant avec découvert =====");
        try {
            CompteBancaire c2 = rechercherCompte(comptes, "C002");
            c2.retirer(1200); // solde passe à -700, autorisé (découvert 1000)
            c2.afficherSolde();

            // dépassement du découvert autorisé
            c2.retirer(500);
        } catch (FondsInsuffisantsException | CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 4. Le compte épargne génère des intérêts
        System.out.println("\n===== Compte épargne avec intérêts =====");
        try {
            CompteEpargne c3 = (CompteEpargne) rechercherCompte(comptes, "C003");
            c3.appliquerInterets();
            c3.afficherSolde();
        } catch (CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 5. Transfert entre deux comptes
        System.out.println("\n===== Transfert entre comptes =====");
        try {
            CompteBancaire c1 = rechercherCompte(comptes, "C001");
            CompteBancaire c3 = rechercherCompte(comptes, "C003");
            c1.transferer(c3, 400);
            c1.afficherSolde();
            c3.afficherSolde();
        } catch (FondsInsuffisantsException | CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 6. Transfert vers un compte inexistant
        System.out.println("\n===== Transfert vers un compte inexistant =====");
        try {
            CompteBancaire c1 = rechercherCompte(comptes, "C001");
            CompteBancaire inconnu = rechercherCompte(comptes, "C999");
            c1.transferer(inconnu, 100);
        } catch (FondsInsuffisantsException | CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 7. Supprimer un compte puis tenter d'y accéder
        System.out.println("\n===== Suppression d'un compte =====");
        comptes.removeIf(compte -> compte.getNumeroCompte().equals("C002"));
        System.out.println("Compte C002 supprimé");
        try {
            rechercherCompte(comptes, "C002").afficherSolde();
        } catch (CompteInexistantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 8. Affichage final
        System.out.println("\n===== État final des comptes =====");
        for (CompteBancaire compte : comptes) {
            compte.afficherSolde();
        }
    }
}
