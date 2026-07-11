package dev.elayachi.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Élément <constructor-arg ref="..."/> ou <constructor-arg value="..."/>
 * - ref   : référence vers un autre bean
 * - value : valeur simple (String, int, double, boolean...)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ArgDefinition {

    @XmlAttribute
    private String ref;

    @XmlAttribute
    private String value;

    public String getRef() {
        return ref;
    }

    public String getValue() {
        return value;
    }
}
