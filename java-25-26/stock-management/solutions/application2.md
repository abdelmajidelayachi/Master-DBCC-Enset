# Application 2 : Gestion des comptes bancaires

## Énoncé

Les classes à modéliser sont les suivantes :

- **Client** : cin, nom, prenom, telephone
- **Operation** : date, montant, type (« VERS » ou « RETR »)
- **Compte** : numero, dateCreation, solde, client, operations

La classe `Compte` doit contenir une méthode supplémentaire :

- `double getSolde()` : retourne le solde actuel du compte.

Travail demandé :

1. Implémenter la classe `Client`.
2. Implémenter la classe `Operation`.
3. Implémenter la classe `Compte` en respectant les contraintes suivantes :
   - Une liste d'opérations doit être maintenue dans chaque compte.
   - La méthode `getSolde()` doit recalculer le solde à partir de l'ensemble des opérations :
     « VERS » augmente le solde, « RETR » diminue le solde.
4. Créer une classe `Program` exécutable contenant le scénario suivant :
   - Créer un client.
   - Créer un compte bancaire associé à ce client.
   - Ajouter plusieurs opérations (versements et retraits).
   - Afficher le détail complet du compte : numéro, informations du client,
     liste des opérations, solde final.
   - Trier les opérations par date croissante avant l'affichage.

## Code source

### Client.java

```java
package dev.elayachi.app2;

public class Client {

    private String cin;
    private String nom;
    private String prenom;
    private String telephone;

    public Client(String cin, String nom, String prenom, String telephone) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Client{cin='" + cin + '\''
                + ", nom='" + nom + '\''
                + ", prenom='" + prenom + '\''
                + ", telephone='" + telephone + '\'' + '}';
    }
}
```

### Operation.java

```java
package dev.elayachi.app2;

import java.time.LocalDate;

public class Operation {

    public static final String VERSEMENT = "VERS";
    public static final String RETRAIT = "RETR";

    private LocalDate date;
    private double montant;
    private String type;

    public Operation(LocalDate date, double montant, String type) {
        this.date = date;
        this.montant = montant;
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Operation{date=" + date
                + ", montant=" + montant
                + ", type='" + type + '\'' + '}';
    }
}
```

### Compte.java

```java
package dev.elayachi.app2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Compte {

    private String numero;
    private LocalDate dateCreation;
    private double solde;
    private Client client;
    private List<Operation> operations = new ArrayList<>();

    public Compte(String numero, LocalDate dateCreation, Client client) {
        this.numero = numero;
        this.dateCreation = dateCreation;
        this.client = client;
        this.solde = 0;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void ajouterOperation(Operation operation) {
        operations.add(operation);
    }

    public double getSolde() {
        solde = 0;
        for (Operation op : operations) {
            if (Operation.VERSEMENT.equals(op.getType())) {
                solde += op.getMontant();
            } else if (Operation.RETRAIT.equals(op.getType())) {
                solde -= op.getMontant();
            }
        }
        return solde;
    }

    public void trierOperationsParDate() {
        operations.sort(Comparator.comparing(Operation::getDate));
    }

    public void afficherDetail() {
        trierOperationsParDate();
        System.out.println("Numero du compte : " + numero);
        System.out.println("Date de creation : " + dateCreation);
        System.out.println("Client : " + client);
        System.out.println("Liste des operations :");
        for (Operation op : operations) {
            System.out.println("  " + op);
        }
        System.out.println("Solde final : " + getSolde());
    }
}
```

### Program.java

```java
package dev.elayachi.app2;

import java.time.LocalDate;

public class Program {

    public static void main(String[] args) {
        Client client = new Client("AB123456", "El Amrani", "Youssef", "0612345678");

        Compte compte = new Compte("C-0001", LocalDate.of(2026, 1, 15), client);

        compte.ajouterOperation(new Operation(LocalDate.of(2026, 3, 10), 2000, Operation.VERSEMENT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 2, 1), 5000, Operation.VERSEMENT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 4, 5), 1500, Operation.RETRAIT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 3, 20), 800, Operation.RETRAIT));
        compte.ajouterOperation(new Operation(LocalDate.of(2026, 5, 12), 3000, Operation.VERSEMENT));

        compte.afficherDetail();
    }
}
```

## Scénario d'exécution

```
Numero du compte : C-0001
Date de creation : 2026-01-15
Client : Client{cin='AB123456', nom='El Amrani', prenom='Youssef', telephone='0612345678'}
Liste des operations :
  Operation{date=2026-02-01, montant=5000.0, type='VERS'}
  Operation{date=2026-03-10, montant=2000.0, type='VERS'}
  Operation{date=2026-03-20, montant=800.0, type='RETR'}
  Operation{date=2026-04-05, montant=1500.0, type='RETR'}
  Operation{date=2026-05-12, montant=3000.0, type='VERS'}
Solde final : 7700.0
```

Le solde final se vérifie : 5000 + 2000 − 800 − 1500 + 3000 = **7700**.
