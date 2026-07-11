package dev.elayachi.framework.core;

import dev.elayachi.framework.annotations.Autowired;
import dev.elayachi.framework.annotations.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Conteneur IOC - VERSION ANNOTATIONS.
 *
 * Fonctionnement :
 * 1. Scanne le package de base et détecte les classes annotées @Component
 * 2. Instancie chaque composant :
 *    - via le constructeur annoté @Autowired (injection par constructeur)
 *    - sinon via le constructeur sans paramètres
 * 3. Injecte les attributs annotés @Autowired (injection par Field)
 * 4. Invoque les setters annotés @Autowired (injection par Setter)
 */
public class AnnotationApplicationContext implements ApplicationContext {

    private final List<Class<?>> componentClasses = new ArrayList<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<String, Object> beansByName = new HashMap<>();
    private final Set<Class<?>> beansEnCreation = new HashSet<>();

    public AnnotationApplicationContext(String basePackage) {
        // 1. Détection des composants
        for (Class<?> clazz : ClassScanner.scan(basePackage)) {
            if (clazz.isAnnotationPresent(Component.class)) {
                componentClasses.add(clazz);
            }
        }
        // 2. Création et injection de tous les beans
        for (Class<?> clazz : componentClasses) {
            createBean(clazz);
        }
    }

    private Object createBean(Class<?> clazz) {
        if (singletons.containsKey(clazz)) return singletons.get(clazz);
        if (!beansEnCreation.add(clazz)) {
            throw new BeanException("Dépendance circulaire détectée sur : " + clazz.getName());
        }
        try {
            // a. Injection via le constructeur
            Object instance = instantiate(clazz);
            singletons.put(clazz, instance);
            beansByName.put(beanName(clazz), instance);
            // b. Injection via les attributs (Field)
            injectFields(instance);
            // c. Injection via les setters
            injectSetters(instance);
            return instance;
        } catch (BeanException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanException("Impossible de créer le bean : " + clazz.getName(), e);
        } finally {
            beansEnCreation.remove(clazz);
        }
    }

    private Object instantiate(Class<?> clazz) throws Exception {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = resolveByType(paramTypes[i]);
                }
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        }
        // Pas de constructeur @Autowired : constructeur par défaut
        Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        return defaultConstructor.newInstance();
    }

    private void injectFields(Object instance) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                field.set(instance, resolveByType(field.getType()));
            }
        }
    }

    private void injectSetters(Object instance) throws Exception {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                if (method.getParameterCount() != 1) {
                    throw new BeanException("Le setter @Autowired doit avoir un seul paramètre : "
                            + method.getName());
                }
                method.setAccessible(true);
                method.invoke(instance, resolveByType(method.getParameterTypes()[0]));
            }
        }
    }

    /**
     * Résout une dépendance par type : cherche parmi les composants détectés
     * la classe compatible avec le type demandé (interface ou classe),
     * et la crée si elle n'existe pas encore.
     */
    private Object resolveByType(Class<?> type) {
        List<Class<?>> candidates = new ArrayList<>();
        for (Class<?> clazz : componentClasses) {
            if (type.isAssignableFrom(clazz)) {
                candidates.add(clazz);
            }
        }
        if (candidates.isEmpty()) {
            throw new BeanException("Aucun composant trouvé pour le type : " + type.getName());
        }
        if (candidates.size() > 1) {
            throw new BeanException("Plusieurs composants trouvés pour le type : " + type.getName()
                    + " -> " + candidates);
        }
        return createBean(candidates.getFirst());
    }

    private String beanName(Class<?> clazz) {
        String name = clazz.getAnnotation(Component.class).value();
        if (name.isEmpty()) {
            String simpleName = clazz.getSimpleName();
            name = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }
        return name;
    }

    @Override
    public Object getBean(String name) {
        Object bean = beansByName.get(name);
        if (bean == null) throw new BeanException("Aucun bean nommé : " + name);
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        return (T) resolveByType(type);
    }
}
