package dev.elayachi.exercice2;

public class CompteEpargne extends CompteBancaire {

    // taux d'intérêt annuel (exemple : 0.03 pour 3%)
    private double tauxInteret;

    public CompteEpargne(String numeroCompte, double solde, String nomTitulaire, double tauxInteret) {
        super(numeroCompte, solde, nomTitulaire);
        this.tauxInteret = tauxInteret;
    }

    public void appliquerInterets() {
        double interets = solde * tauxInteret;
        solde += interets;
        System.out.println("Intérêts de " + interets + " DH appliqués au compte épargne " + numeroCompte);
    }

    public double getTauxInteret() {
        return tauxInteret;
    }
}
