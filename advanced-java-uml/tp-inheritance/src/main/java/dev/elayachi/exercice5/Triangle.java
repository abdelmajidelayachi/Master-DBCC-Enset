package dev.elayachi.exercice5;

public class Triangle extends Figure {
    private double coteA;
    private double coteB;
    private double coteC;

    public Triangle(String nom, double coteA, double coteB, double coteC) {
        super(nom);
        this.coteA = coteA;
        this.coteB = coteB;
        this.coteC = coteC;
    }

    @Override
    public double calculerAire() {
        // Formule de Héron
        double s = calculerPerimetre() / 2;
        return Math.sqrt(s * (s - coteA) * (s - coteB) * (s - coteC));
    }

    @Override
    public double calculerPerimetre() {
        return coteA + coteB + coteC;
    }
}
