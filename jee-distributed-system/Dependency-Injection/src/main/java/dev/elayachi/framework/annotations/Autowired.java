package dev.elayachi.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Demande l'injection automatique d'une dépendance.
 * Équivalent de @Autowired de Spring. Peut être placée sur :
 * - un CONSTRUCTEUR : injection via le constructeur
 * - une MÉTHODE (setter) : injection via le setter
 * - un ATTRIBUT : injection directe dans le champ (Field)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Autowired {
}
