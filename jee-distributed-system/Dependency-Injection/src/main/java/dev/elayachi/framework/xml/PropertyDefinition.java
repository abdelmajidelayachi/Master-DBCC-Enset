package dev.elayachi.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Élément <property name="..." ref="..."/> ou <property name="..." value="..."/>
 * Injection via le setter : le framework appellera setName(...).
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyDefinition {

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
