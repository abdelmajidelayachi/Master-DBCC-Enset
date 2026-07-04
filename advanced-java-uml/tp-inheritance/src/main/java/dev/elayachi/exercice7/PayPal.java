package dev.elayachi.exercice7;

public class PayPal extends Paiement {
    private String email;

    public PayPal(String numeroTransaction, String email) {
        super(numeroTransaction);
        this.email = email;
    }

    @Override
    public void effectuerPaiement(double montant) {
        this.montant = montant;
        System.out.println("[Transaction " + numeroTransaction + "] Paiement de " + montant
                + " DH effectué via PayPal (compte : " + email + ").");
    }
}
