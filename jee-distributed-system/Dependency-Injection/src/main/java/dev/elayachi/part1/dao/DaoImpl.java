package dev.elayachi.part1.dao;

import org.springframework.stereotype.Repository;

/**
 * Implémentation de IDao : version base de données.
 * L'annotation @Repository("dao") permet à Spring de créer le bean
 * lors du scan des composants (version annotations).
 */
@Repository("dao")
public class DaoImpl implements IDao {

    @Override
    public double getData() {
        System.out.println("Version base de données");
        return 34;
    }
}
