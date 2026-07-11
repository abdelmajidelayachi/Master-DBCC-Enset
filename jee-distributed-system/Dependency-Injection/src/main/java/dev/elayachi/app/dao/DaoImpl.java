package dev.elayachi.app.dao;

import dev.elayachi.framework.annotations.Component;

/**
 * Composant DAO géré par le mini framework grâce à @Component.
 */
@Component("dao")
public class DaoImpl implements IDao {

    @Override
    public double getData() {
        System.out.println("Version base de données (mini framework)");
        return 34;
    }
}
