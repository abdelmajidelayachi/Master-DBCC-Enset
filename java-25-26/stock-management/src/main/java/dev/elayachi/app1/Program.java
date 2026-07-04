package dev.elayachi.app1;

public class Program {

    public static void main(String[] args) {
        Departement informatique = new Departement(1, "Informatique");
        Departement ressourcesHumaines = new Departement(2, "Ressources Humaines");

        Salarie s1 = new Salarie(101, "El Amrani", "Youssef", 12000);
        Salarie s2 = new Salarie(102, "Benali", "Fatima", 9500);
        Salarie s3 = new Salarie(103, "Alaoui", "Karim", 15000);
        Salarie s4 = new Salarie(104, "Tazi", "Salma", 8000);
        Salarie s5 = new Salarie(105, "Idrissi", "Mohamed", 11000);

        informatique.ajouterSalarie(s1);
        informatique.ajouterSalarie(s2);
        informatique.ajouterSalarie(s3);

        ressourcesHumaines.ajouterSalarie(s4);
        ressourcesHumaines.ajouterSalarie(s5);

        informatique.afficherDetail();
        System.out.println();
        ressourcesHumaines.afficherDetail();
    }
}
