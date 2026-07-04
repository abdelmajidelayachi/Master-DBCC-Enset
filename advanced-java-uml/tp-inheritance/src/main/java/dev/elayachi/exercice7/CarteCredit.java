package dev.elayachi.exercice7;

public class CarteCredit extends Paiement {
    private String numeroCarte;

    public CarteCredit(String numeroTransaction, String numeroCarte) {
        super(numeroTransaction);
        this.numeroCarte = numeroCarte;
    }

    @Override
    public void effectuerPaiement(double montant) {
        this.montant = montant;
        System.out.println("[Transaction " + numeroTransaction + "] Paiement de " + montant
                + " DH effectué par carte de crédit n° " + numeroCarte + ".");
    }
}
