package dev.elayachi.framework.core;

/**
 * Contrat du conteneur IOC du mini framework :
 * permet de récupérer les beans créés et injectés par le framework.
 */
public interface ApplicationContext {

    /**
     * Retourne le bean identifié par son nom (id).
     */
    Object getBean(String name);

    /**
     * Retourne le bean compatible avec le type demandé (classe ou interface).
     */
    <T> T getBean(Class<T> type);
}
