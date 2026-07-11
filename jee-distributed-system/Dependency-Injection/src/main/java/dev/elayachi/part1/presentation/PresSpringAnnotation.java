package dev.elayachi.part1.presentation;

import dev.elayachi.part1.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Injection des dépendances avec SPRING - VERSION ANNOTATIONS :
 * Spring scanne le package "dev.elayachi.part1", détecte les classes
 * annotées (@Repository, @Service) et fait l'injection grâce à @Autowired.
 */
public class PresSpringAnnotation {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext("dev.elayachi.part1");
        IMetier metier = context.getBean(IMetier.class);
        System.out.println("Résultat = " + metier.calcul());
    }
}
