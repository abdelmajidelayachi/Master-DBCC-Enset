package dev.elayachi.part1.ext;

import dev.elayachi.part1.dao.IDao;

/**
 * Deuxième implémentation de IDao : version capteurs (web service).
 * Grâce au couplage faible, on peut remplacer DaoImpl par cette classe
 * sans toucher au code de la couche métier (fermé à la modification,
 * ouvert à l'extension).
 */
public class DaoImplV2 implements IDao {

    @Override
    public double getData() {
        System.out.println("Version capteurs");
        return 12;
    }
}
