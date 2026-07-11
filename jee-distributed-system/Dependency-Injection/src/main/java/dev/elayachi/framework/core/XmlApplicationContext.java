package dev.elayachi.framework.core;

import dev.elayachi.framework.xml.ArgDefinition;
import dev.elayachi.framework.xml.BeanDefinition;
import dev.elayachi.framework.xml.BeansDefinition;
import dev.elayachi.framework.xml.FieldDefinition;
import dev.elayachi.framework.xml.PropertyDefinition;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Conteneur IOC - VERSION XML.
 *
 * Le fichier de configuration est lu avec JAXB (mapping Objet/XML) :
 * le XML est transformé en objets BeansDefinition / BeanDefinition,
 * puis le conteneur crée les beans par réflexion et fait l'injection :
 * - <constructor-arg> : injection via le constructeur
 * - <property>        : injection via le setter
 * - <field>           : injection directe dans l'attribut
 */
public class XmlApplicationContext implements ApplicationContext {

    private final Map<String, Object> beansByName = new LinkedHashMap<>();

    public XmlApplicationContext(String xmlResource) {
        try {
            // 1. Mapping Objet/XML avec JAXB
            JAXBContext jaxbContext = JAXBContext.newInstance(BeansDefinition.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InputStream in = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(xmlResource);
            if (in == null) {
                throw new BeanException("Fichier de configuration introuvable : " + xmlResource);
            }
            BeansDefinition beansDefinition = (BeansDefinition) unmarshaller.unmarshal(in);

            // 2. Création des beans dans l'ordre de déclaration
            for (BeanDefinition definition : beansDefinition.getBeans()) {
                beansByName.put(definition.getId(), createBean(definition));
            }
        } catch (BeanException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanException("Erreur lors du chargement du fichier : " + xmlResource, e);
        }
    }

    private Object createBean(BeanDefinition definition) throws Exception {
        Class<?> clazz = Class.forName(definition.getClassName());

        // a. Injection via le constructeur
        Object instance = instantiate(clazz, definition.getConstructorArgs());

        // b. Injection via les setters
        for (PropertyDefinition property : definition.getProperties()) {
            injectBySetter(instance, property);
        }

        // c. Injection directe dans les attributs (Field)
        for (FieldDefinition fieldDef : definition.getFields()) {
            injectByField(instance, fieldDef);
        }
        return instance;
    }

    private Object instantiate(Class<?> clazz, List<ArgDefinition> argDefs) throws Exception {
        if (argDefs.isEmpty()) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
        // Chercher un constructeur compatible avec les arguments déclarés
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != argDefs.size()) continue;
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[argDefs.size()];
            boolean compatible = true;
            for (int i = 0; i < argDefs.size(); i++) {
                Object arg = resolve(argDefs.get(i).getRef(), argDefs.get(i).getValue(), paramTypes[i]);
                if (arg != null && !wrap(paramTypes[i]).isInstance(arg)) {
                    compatible = false;
                    break;
                }
                args[i] = arg;
            }
            if (compatible) {
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        }
        throw new BeanException("Aucun constructeur compatible trouvé pour : " + clazz.getName());
    }

    private void injectBySetter(Object instance, PropertyDefinition property) throws Exception {
        String setterName = "set" + Character.toUpperCase(property.getName().charAt(0))
                + property.getName().substring(1);
        for (Method method : instance.getClass().getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                Object arg = resolve(property.getRef(), property.getValue(), method.getParameterTypes()[0]);
                method.invoke(instance, arg);
                return;
            }
        }
        throw new BeanException("Setter introuvable : " + setterName
                + " dans " + instance.getClass().getName());
    }

    private void injectByField(Object instance, FieldDefinition fieldDef) throws Exception {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldDef.getName());
                field.setAccessible(true);
                field.set(instance, resolve(fieldDef.getRef(), fieldDef.getValue(), field.getType()));
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new BeanException("Attribut introuvable : " + fieldDef.getName()
                + " dans " + instance.getClass().getName());
    }

    /**
     * Résout une dépendance : soit une référence vers un autre bean (ref),
     * soit une valeur simple (value) convertie vers le type cible.
     */
    private Object resolve(String ref, String value, Class<?> targetType) {
        if (ref != null) return getBean(ref);
        if (value != null) return convert(value, targetType);
        throw new BeanException("Une injection doit avoir un attribut 'ref' ou 'value'");
    }

    private Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        if (targetType == float.class || targetType == Float.class) return Float.parseFloat(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == short.class || targetType == Short.class) return Short.parseShort(value);
        if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(value);
        if (targetType == char.class || targetType == Character.class) return value.charAt(0);
        throw new BeanException("Conversion non supportée vers : " + targetType.getName());
    }

    /**
     * Retourne la classe wrapper d'un type primitif (pour le test isInstance).
     */
    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == boolean.class) return Boolean.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        return Character.class;
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
        for (Object bean : beansByName.values()) {
            if (type.isInstance(bean)) return (T) bean;
        }
        throw new BeanException("Aucun bean trouvé pour le type : " + type.getName());
    }
}
