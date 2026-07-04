package dev.elayachi.exercice6;

public class GestionBibliothequeApp {
    public static void main(String[] args) {
        Livre livre1 = new Livre("Le Petit Prince", "Antoine de Saint-Exupéry");
        Livre livre2 = new Livre("Les Misérables", "Victor Hugo");
        DVD dvd1 = new DVD("Inception", "Christopher Nolan");
        DVD dvd2 = new DVD("Interstellar", "Christopher Nolan");

        Utilisateur utilisateur1 = new Utilisateur("Yassine");
        Utilisateur utilisateur2 = new Utilisateur("Salma");

        utilisateur1.emprunterObjet(livre1);
        utilisateur1.emprunterObjet(dvd1);
        utilisateur2.emprunterObjet(livre2);
        utilisateur2.emprunterObjet(dvd2);

        // Tentative d'emprunt d'un objet déjà emprunté
        utilisateur2.emprunterObjet(livre1);

        System.out.println("-----------------------------------");
        livre1.retourner();
        dvd1.retourner();

        // Retour d'un objet non emprunté
        livre1.retourner();
    }
}
