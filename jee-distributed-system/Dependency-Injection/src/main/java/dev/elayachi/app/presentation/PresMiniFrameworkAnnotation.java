package dev.elayachi.app.presentation;

import dev.elayachi.app.metier.IMetier;
import dev.elayachi.framework.core.AnnotationApplicationContext;
import dev.elayachi.framework.core.ApplicationContext;

/**
 * Démonstration du mini framework - VERSION ANNOTATIONS :
 * le framework scanne le package "dev.elayachi.app", détecte les
 * classes annotées @Component et fait l'injection des dépendances
 * annotées @Autowired (constructeur, setter et field).
 */
public class PresMiniFrameworkAnnotation {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContext("dev.elayachi.app");

        IMetier metier = context.getBean(IMetier.class);
        System.out.println("Résultat = " + metier.calcul());

        // Récupération par nom (valeur de @Component)
        IMetier metierParNom = (IMetier) context.getBean("metier");
        System.out.println("Résultat (bean par nom) = " + metierParNom.calcul());
    }
}
