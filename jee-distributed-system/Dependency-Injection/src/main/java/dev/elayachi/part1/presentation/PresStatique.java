package dev.elayachi.part1.presentation;

import dev.elayachi.part1.dao.DaoImpl;
import dev.elayachi.part1.metier.MetierImpl;

/**
 * Injection des dépendances par INSTANCIATION STATIQUE :
 * on utilise le mot clé "new" pour créer les objets.
 * Inconvénient : pour changer d'implémentation il faut modifier
 * le code source et recompiler l'application.
 */
public class PresStatique {
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao); // injection via le constructeur
        // Autre possibilité : injection via le setter
        // metier.setDao(dao);
        System.out.println("Résultat = " + metier.calcul());
    }
}
