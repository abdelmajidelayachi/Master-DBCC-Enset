package dev.elayachi.part1.presentation;

import dev.elayachi.part1.dao.IDao;
import dev.elayachi.part1.metier.IMetier;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Injection des dépendances par INSTANCIATION DYNAMIQUE :
 * les noms des classes sont lus depuis un fichier de configuration
 * (config.txt) et les objets sont créés par réflexion.
 * Avantage : on peut changer d'implémentation sans recompiler,
 * il suffit de modifier le fichier config.txt.
 */
public class PresDynamique {
    public static void main(String[] args) throws Exception {
        Scanner scanner = openConfig();

        // 1. Instancier dynamiquement le DAO
        String daoClassName = scanner.nextLine();
        Class<?> cDao = Class.forName(daoClassName);
        IDao dao = (IDao) cDao.getConstructor().newInstance();

        // 2. Instancier dynamiquement le Métier et injecter le DAO
        String metierClassName = scanner.nextLine();
        Class<?> cMetier = Class.forName(metierClassName);
        // Injection via le constructeur
        IMetier metier = (IMetier) cMetier.getConstructor(IDao.class).newInstance(dao);
        // Variante : injection via le setter
        // IMetier metier = (IMetier) cMetier.getConstructor().newInstance();
        // cMetier.getMethod("setDao", IDao.class).invoke(metier, dao);

        System.out.println("Résultat = " + metier.calcul());
        scanner.close();
    }

    /**
     * Cherche config.txt d'abord dans le répertoire courant (modifiable
     * sans recompiler), sinon dans le classpath (src/main/resources).
     * Le répertoire courant dépend de l'endroit d'où on lance le programme
     * (IDE ou terminal), d'où ce double emplacement.
     */
    private static Scanner openConfig() throws Exception {
        File file = new File("config.txt");
        if (file.exists()) {
            return new Scanner(file);
        }
        InputStream in = PresDynamique.class.getClassLoader().getResourceAsStream("config.txt");
        return new Scanner(Objects.requireNonNull(in, "config.txt introuvable"));
    }
}
