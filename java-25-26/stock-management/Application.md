Travail de fin de module
Les énoncés des exercices et des applications à réaliser se trouvent dans le document
du cours Java.
Le travail de fin de module comprend deux parties :
Partie 1 : Exercices et Partie 2 : Applications.
Chaque étudiant doit remettre :
• un document PDF intitulé exercices.pdf contenant les réponses de la Partie 1 ;

• un document PDF intitulé applications.pdf contenant les réalisations de la
Partie 2.

Partie 2 : Applications
Les applications suivantes doivent être développées et présentées dans le document
applications.pdf.
Chaque application doit contenir les classes nécessaires, un scénario d’exécution et le
code source complet.
• Application 1 : Gestion des Salariés
Se référer au chapitre 5.3 (page 58) du cours Java.
Exercice 1 : gestion des salariés
Énoncé
On souhaite modéliser une relation ManyToOne entre les classes Salarie et Departement.
Un département peut regrouper plusieurs salariés.
Les deux classes Departement et Salarie sont définies de la manière suivante :
— Departement : id, nom
— Salarie : matricule, nom, prenom, salaire
Travail demandé
1. Reprendre la classe salarié du cours
2. implémenter la classe Departement
3. Créer une classe Program exécutable qui teste l’ensemble des éléments de la manière
   suivante :
   — Créer deux départements et plusieurs salariés, puis affecter chaque salarié à un
   département.
   — Afficher le détail d’un département : nom du département, liste des salariés
   et total des salaires.
   — Trier la liste des salariés d’un département par salaire croissant avant
   l’affichage.
• Application 2 : Gestion des comptes bancaires
Se référer au chapitre 5.3 (pages 58–59) du cours Java.
4. Les classes à modéliser sont les suivantes :
   — Client : cin, nom, prenom, telephone
   — Operation : date, montant, type (“VERS” ou “RETR”)
   — Compte : numero, dateCreation, solde, client, operations
   La classe Compte doit contenir une méthode supplémentaire :
   — double getSolde() : retourne le solde actuel du compte.
   Travail demandé
1. Implémenter la classe Client.
2. Implémenter la classe Operation.
3. Implémenter la classe Compte en respectant les contraintes suivantes :
   — Une liste d’opérations doit être maintenue dans chaque compte.
   — La méthode getSolde() doit recalculer le solde à partir de l’ensemble des
   opérations :
   — “VERS” augmente le solde.
   — “RETR” diminue le solde.
4. Créer une classe Program exécutable contenant le scénario suivant :
   — Créer un client.
   — Créer un compte bancaire associé à ce client.
   — Ajouter plusieurs opérations (versements et retraits).
   — Afficher le détail complet du compte :
   — numéro du compte,
   — informations du client,
   — liste des opérations,
   — solde final du compte.
   — Trier les opérations par date croissante avant l’affichage.
