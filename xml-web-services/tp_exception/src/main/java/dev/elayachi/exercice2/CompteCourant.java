package dev.elayachi.exercice2;

public class CompteCourant extends CompteBancaire {

    // le compte courant autorise un découvert
    private double decouvertAutorise;

    public CompteCourant(String numeroCompte, double solde, String nomTitulaire, double decouvertAutorise) {
        super(numeroCompte, solde, nomTitulaire);
        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public void retirer(double montant) throws FondsInsuffisantsException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif");
        }
        // le retrait est autorisé tant que le solde ne descend pas sous -decouvertAutorise
        if (solde - montant < -decouvertAutorise) {
            throw new FondsInsuffisantsException(
                    "Fonds insuffisants sur le compte courant " + numeroCompte
                            + " : solde = " + solde + " DH, découvert autorisé = " + decouvertAutorise
                            + " DH, retrait demandé = " + montant + " DH");
        }
        solde -= montant;
        System.out.println("Retrait de " + montant + " DH du compte courant " + numeroCompte);
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }
}
