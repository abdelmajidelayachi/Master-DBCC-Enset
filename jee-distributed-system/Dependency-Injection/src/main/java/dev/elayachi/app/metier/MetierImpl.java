package dev.elayachi.app.metier;

import dev.elayachi.app.dao.IDao;
import dev.elayachi.framework.annotations.Autowired;
import dev.elayachi.framework.annotations.Component;

/**
 * Composant Métier géré par le mini framework.
 * Cette classe montre les TROIS modes d'injection supportés :
 * - @Autowired sur le constructeur -> injection via le constructeur
 * - @Autowired sur le setter       -> injection via le setter
 * - @Autowired sur l'attribut      -> injection directe (Field)
 * (En pratique on n'en utilise qu'un seul ; ici les trois sont
 * annotés pour démontrer que le framework gère les trois cas.)
 */
@Component("metier")
public class MetierImpl implements IMetier {

    @Autowired
    private IDao dao; // injection par attribut (Field)

    public MetierImpl() {
    }

    @Autowired
    public MetierImpl(IDao dao) {
        System.out.println("-> Injection via le CONSTRUCTEUR");
        this.dao = dao;
    }

    @Autowired
    public void setDao(IDao dao) {
        System.out.println("-> Injection via le SETTER");
        this.dao = dao;
    }

    @Override
    public double calcul() {
        return dao.getData() * 12;
    }
}
