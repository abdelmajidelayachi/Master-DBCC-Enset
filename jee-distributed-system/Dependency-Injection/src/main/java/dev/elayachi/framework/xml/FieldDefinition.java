package dev.elayachi.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Élément <field name="..." ref="..."/> ou <field name="..." value="..."/>
 * Injection directe dans l'attribut (Field), sans passer par un setter.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldDefinition {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String ref;

    @XmlAttribute
    private String value;

    public String getName() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    public String getValue() {
        return value;
    }
}
