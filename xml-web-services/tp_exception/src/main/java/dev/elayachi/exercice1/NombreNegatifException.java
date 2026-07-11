package dev.elayachi.exercice1;

public class NombreNegatifException extends Exception {

    // valeur erronée qui a entraîné la génération de l'exception
    private final int valeurErronee;

    public NombreNegatifException(int valeurErronee) {
        super("Nombre négatif non autorisé : " + valeurErronee);
        this.valeurErronee = valeurErronee;
    }

    public int getValeurErronee() {
        return valeurErronee;
    }
}
