package dev.elayachi.exercice5;

public abstract class Figure {
    protected String nom;

    public Figure(String nom) {
        this.nom = nom;
    }

    public abstract double calculerAire();

    public abstract double calculerPerimetre();

    public void afficherDetails() {
        System.out.println("Figure : " + nom);
        System.out.println("Aire : " + calculerAire());
        System.out.println("Périmètre : " + calculerPerimetre());
        System.out.println("-----------------------------------");
    }
}
