package dev.elayachi.app.presentation;

import dev.elayachi.app.metier.IMetier;
import dev.elayachi.framework.core.ApplicationContext;
import dev.elayachi.framework.core.XmlApplicationContext;

/**
 * Démonstration du mini framework - VERSION XML (JAXB) :
 * les beans sont déclarés dans beans.xml avec les trois modes
 * d'injection (constructeur, setter, field).
 */
public class PresMiniFrameworkXml {
    public static void main(String[] args) {
        ApplicationContext context = new XmlApplicationContext("beans.xml");

        System.out.println("=== Bean avec injection via le constructeur ===");
        IMetier metier1 = (IMetier) context.getBean("metierConstructeur");
        System.out.println("Résultat = " + metier1.calcul());

        System.out.println("=== Bean avec injection via le setter ===");
        IMetier metier2 = (IMetier) context.getBean("metierSetter");
        System.out.println("Résultat = " + metier2.calcul());

        System.out.println("=== Bean avec injection via l'attribut (Field) ===");
        IMetier metier3 = (IMetier) context.getBean("metierField");
        System.out.println("Résultat = " + metier3.calcul());
    }
}
