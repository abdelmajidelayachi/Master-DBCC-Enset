package dev.elayachi.exercice6;

public class DVD implements Empruntable {
    private String titre;
    private String realisateur;
    private boolean emprunte;

    public DVD(String titre, String realisateur) {
        this.titre = titre;
        this.realisateur = realisateur;
        this.emprunte = false;
    }

    @Override
    public void emprunter() {
        if (emprunte) {
            System.out.println("Le DVD \"" + titre + "\" est déjà emprunté.");
        } else {
            emprunte = true;
            System.out.println("Le DVD \"" + titre + "\" de " + realisateur + " a été emprunté.");
        }
    }

    @Override
    public void retourner() {
        if (emprunte) {
            emprunte = false;
            System.out.println("Le DVD \"" + titre + "\" a été retourné.");
        } else {
            System.out.println("Le DVD \"" + titre + "\" n'est pas emprunté.");
        }
    }
}
