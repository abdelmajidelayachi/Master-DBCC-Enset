package dev.elayachi.part1.presentation;

import dev.elayachi.part1.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Injection des dépendances avec SPRING - VERSION XML :
 * les beans et leurs dépendances sont déclarés dans config.xml,
 * Spring crée les objets et fait l'injection (ici via le setter).
 */
public class PresSpringXml {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        IMetier metier = context.getBean(IMetier.class);
        System.out.println("Résultat = " + metier.calcul());
    }
}
