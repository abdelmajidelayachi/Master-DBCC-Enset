package dev.elayachi.exercice7;

public abstract class Paiement {
    protected double montant;
    protected String numeroTransaction;

    public Paiement(String numeroTransaction) {
        this.numeroTransaction = numeroTransaction;
    }

    public abstract void effectuerPaiement(double montant);
}
