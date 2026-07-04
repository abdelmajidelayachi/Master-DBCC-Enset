package dev.elayachi.exercice1;

public class BibliothequeApp {
    public static void main(String[] args) {
        Adherent adherent = new Adherent("El Amrani", "Yassine",
                "yassine.elamrani@gmail.com", "0612345678", 22, 1001);

        Auteur auteur = new Auteur("Ben Jelloun", "Tahar",
                "tahar.benjelloun@gmail.com", "0698765432", 78, 501);
        Livre livre = new Livre(978214, "L'Enfant de sable", auteur);

        System.out.println(adherent);
        System.out.println(livre);
    }
}
