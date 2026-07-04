package dev.elayachi.exercice4;

public class GestionSalairesApp {
    public static void main(String[] args) {
        Ingenieur ingenieur = new Ingenieur("Alaoui", "Sara",
                "sara.alaoui@entreprise.ma", "0611223344", 15000, "Développement Web");

        Manager manager = new Manager("Bennani", "Omar",
                "omar.bennani@entreprise.ma", "0655667788", 20000, "Ressources Humaines");

        System.out.println("Ingénieur : " + ingenieur.getNom() + " " + ingenieur.getPrenom()
                + " | Spécialité : " + ingenieur.getSpecialite()
                + " | Salaire : " + ingenieur.calculerSalaire() + " DH");

        System.out.println("Manager   : " + manager.getNom() + " " + manager.getPrenom()
                + " | Service : " + manager.getService()
                + " | Salaire : " + manager.calculerSalaire() + " DH");
    }
}
