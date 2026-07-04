package dev.elayachi.exercice6;

public class Livre implements Empruntable {
    private String titre;
    private String auteur;
    private boolean emprunte;

    public Livre(String titre, String auteur) {
        this.titre = titre;
        this.auteur = auteur;
        this.emprunte = false;
    }

    @Override
    public void emprunter() {
        if (emprunte) {
            System.out.println("Le livre \"" + titre + "\" est déjà emprunté.");
        } else {
            emprunte = true;
            System.out.println("Le livre \"" + titre + "\" de " + auteur + " a été emprunté.");
        }
    }

    @Override
    public void retourner() {
        if (emprunte) {
            emprunte = false;
            System.out.println("Le livre \"" + titre + "\" a été retourné.");
        } else {
            System.out.println("Le livre \"" + titre + "\" n'est pas emprunté.");
        }
    }
}
