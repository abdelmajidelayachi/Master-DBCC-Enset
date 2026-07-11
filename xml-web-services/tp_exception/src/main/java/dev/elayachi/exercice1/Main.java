package dev.elayachi.exercice1;

public class Main {

    public static void main(String[] args) {
        // 1. Création d'un entier naturel valide
        try {
            EntierNaturel n = new EntierNaturel(2);
            System.out.println("Entier naturel créé avec val = " + n.getVal());

            // 2. Modification de la valeur avec setVal
            n.setVal(5);
            System.out.println("Après setVal(5), val = " + n.getVal());

            // 3. Décrémentations successives
            n.setVal(1);
            n.decrementer();
            System.out.println("Après decrementer(), val = " + n.getVal());

            // val = 0, cette décrémentation doit lever une exception
            n.decrementer();
            System.out.println("Cette ligne ne sera jamais exécutée");
        } catch (NombreNegatifException e) {
            System.out.println("Exception capturée : " + e.getMessage());
            System.out.println("Valeur erronée mémorisée : " + e.getValeurErronee());
        }

        // 4. Création d'un entier naturel avec une valeur négative
        try {
            EntierNaturel m = new EntierNaturel(-7);
        } catch (NombreNegatifException e) {
            System.out.println("Exception capturée : " + e.getMessage());
            System.out.println("Valeur erronée mémorisée : " + e.getValeurErronee());
        }

        // 5. setVal avec une valeur négative
        try {
            EntierNaturel p = new EntierNaturel(10);
            p.setVal(-3);
        } catch (NombreNegatifException e) {
            System.out.println("Exception capturée : " + e.getMessage());
            System.out.println("Valeur erronée mémorisée : " + e.getValeurErronee());
        }
    }
}
