package dev.elayachi.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marque une classe comme composant géré par le mini framework.
 * Équivalent de @Component de Spring.
 * L'attribut value permet de donner un nom au bean
 * (par défaut : le nom de la classe avec la première lettre en minuscule).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    String value() default "";
}
