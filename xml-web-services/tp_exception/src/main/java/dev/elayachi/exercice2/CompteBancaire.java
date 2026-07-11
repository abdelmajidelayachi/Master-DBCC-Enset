package dev.elayachi.exercice2;

public class CompteBancaire {

    protected String numeroCompte;
    protected double solde;
    protected String nomTitulaire;

    public CompteBancaire(String numeroCompte, double solde, String nomTitulaire) {
        this.numeroCompte = numeroCompte;
        this.solde = solde;
        this.nomTitulaire = nomTitulaire;
    }

    public void deposer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif");
        }
        solde += montant;
        System.out.println("Dépôt de " + montant + " DH sur le compte " + numeroCompte);
    }

    public void retirer(double montant) throws FondsInsuffisantsException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif");
        }
        if (montant > solde) {
            throw new FondsInsuffisantsException(
                    "Fonds insuffisants sur le compte " + numeroCompte
                            + " : solde = " + solde + " DH, retrait demandé = " + montant + " DH");
        }
        solde -= montant;
        System.out.println("Retrait de " + montant + " DH du compte " + numeroCompte);
    }

    public void afficherSolde() {
        System.out.println("Compte " + numeroCompte + " (" + nomTitulaire + ") : solde = " + solde + " DH");
    }

    public void transferer(CompteBancaire destination, double montant)
            throws FondsInsuffisantsException, CompteInexistantException {
        if (destination == null) {
            throw new CompteInexistantException("Le compte destinataire n'existe pas");
        }
        this.retirer(montant);
        destination.deposer(montant);
        System.out.println("Transfert de " + montant + " DH de " + numeroCompte
                + " vers " + destination.getNumeroCompte());
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public double getSolde() {
        return solde;
    }

    public String getNomTitulaire() {
        return nomTitulaire;
    }
}
