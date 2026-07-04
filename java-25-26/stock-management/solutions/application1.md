# Application 1 : Gestion des Salariés

## Énoncé

On souhaite modéliser une relation **ManyToOne** entre les classes `Salarie` et `Departement`.
Un département peut regrouper plusieurs salariés.

- **Departement** : id, nom
- **Salarie** : matricule, nom, prenom, salaire

Travail demandé :

1. Reprendre la classe salarié du cours.
2. Implémenter la classe `Departement`.
3. Créer une classe `Program` exécutable qui teste l'ensemble :
   - Créer deux départements et plusieurs salariés, puis affecter chaque salarié à un département.
   - Afficher le détail d'un département : nom du département, liste des salariés et total des salaires.
   - Trier la liste des salariés d'un département par salaire croissant avant l'affichage.

## Code source

### Salarie.java

```java
package dev.elayachi.app1;

public class Salarie {

    private int matricule;
    private String nom;
    private String prenom;
    private double salaire;
    private Departement departement;

    public Salarie(int matricule, String nom, String prenom, double salaire) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.salaire = salaire;
    }

    public int getMatricule() {
        return matricule;
    }

    public void setMatricule(int matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return "Salarie{matricule=" + matricule
                + ", nom='" + nom + '\''
                + ", prenom='" + prenom + '\''
                + ", salaire=" + salaire + '}';
    }
}
```

### Departement.java

```java
package dev.elayachi.app1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Departement {

    private int id;
    private String nom;
    private List<Salarie> salaries = new ArrayList<>();

    public Departement(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Salarie> getSalaries() {
        return salaries;
    }

    public void ajouterSalarie(Salarie salarie) {
        salaries.add(salarie);
        salarie.setDepartement(this);
    }

    public double totalSalaires() {
        double total = 0;
        for (Salarie s : salaries) {
            total += s.getSalaire();
        }
        return total;
    }

    public void trierParSalaire() {
        salaries.sort(Comparator.comparingDouble(Salarie::getSalaire));
    }

    public void afficherDetail() {
        trierParSalaire();
        System.out.println("Departement : " + nom + " (id=" + id + ")");
        System.out.println("Liste des salaries :");
        for (Salarie s : salaries) {
            System.out.println("  " + s);
        }
        System.out.println("Total des salaires : " + totalSalaires());
    }
}
```

### Program.java

```java
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
```

## Scénario d'exécution

```
Departement : Informatique (id=1)
Liste des salaries :
  Salarie{matricule=102, nom='Benali', prenom='Fatima', salaire=9500.0}
  Salarie{matricule=101, nom='El Amrani', prenom='Youssef', salaire=12000.0}
  Salarie{matricule=103, nom='Alaoui', prenom='Karim', salaire=15000.0}
Total des salaires : 36500.0

Departement : Ressources Humaines (id=2)
Liste des salaries :
  Salarie{matricule=104, nom='Tazi', prenom='Salma', salaire=8000.0}
  Salarie{matricule=105, nom='Idrissi', prenom='Mohamed', salaire=11000.0}
Total des salaires : 19000.0
```
