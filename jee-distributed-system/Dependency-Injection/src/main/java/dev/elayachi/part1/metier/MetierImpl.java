package dev.elayachi.part1.metier;

import dev.elayachi.part1.dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation de IMetier en utilisant le COUPLAGE FAIBLE :
 * la classe dépend de l'interface IDao et non pas d'une implémentation.
 * La dépendance est injectée de l'extérieur (constructeur ou setter),
 * la classe ne fait jamais un "new DaoImpl()".
 */
@Service("metier")
public class MetierImpl implements IMetier {

    // Couplage faible : référence vers l'interface
    private IDao dao;

    public MetierImpl() {
    }

    /**
     * Injection via le constructeur.
     * @Autowired indique à Spring d'injecter le bean IDao (version annotations).
     */
    @Autowired
    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 12;
    }

    /**
     * Injection via le setter (utilisée par la version XML de Spring).
     */
    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
