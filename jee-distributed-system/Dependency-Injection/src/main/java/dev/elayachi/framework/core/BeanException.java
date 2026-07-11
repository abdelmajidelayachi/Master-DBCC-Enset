package dev.elayachi.framework.core;

/**
 * Exception levée par le mini framework quand la création
 * ou l'injection d'un bean échoue.
 */
public class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
