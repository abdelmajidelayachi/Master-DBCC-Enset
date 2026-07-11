package dev.elayachi.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Élément <bean id="..." class="...">
 * Un bean peut recevoir ses dépendances de trois manières :
 * - <constructor-arg ref="..."/>  : injection via le constructeur
 * - <property name="..." ref="..."/> : injection via le setter
 * - <field name="..." ref="..."/>    : injection directe dans l'attribut
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BeanDefinition {

    @XmlAttribute
    private String id;

    @XmlAttribute(name = "class")
    private String className;

    @XmlElement(name = "constructor-arg")
    private List<ArgDefinition> constructorArgs = new ArrayList<>();

    @XmlElement(name = "property")
    private List<PropertyDefinition> properties = new ArrayList<>();

    @XmlElement(name = "field")
    private List<FieldDefinition> fields = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public List<ArgDefinition> getConstructorArgs() {
        return constructorArgs;
    }

    public List<PropertyDefinition> getProperties() {
        return properties;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }
}
