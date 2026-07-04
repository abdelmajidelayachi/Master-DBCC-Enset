package dev.elayachi.exercice1;

public class Livre {
    private int isbn;
    private String titre;
    private Auteur auteur;

    public Livre(int isbn, String titre, Auteur auteur) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
    }

    @Override
    public String toString() {
        return "Livre [ISBN=" + isbn + ", titre='" + titre + "', auteur=" + auteur + "]";
    }
}
