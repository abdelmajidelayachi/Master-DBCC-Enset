package dev.elayachi.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Racine du fichier XML de configuration : <beans> ... </beans>
 * Le mapping Objet/XML est fait avec JAXB (Jakarta XML Binding).
 */
@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeansDefinition {

    @XmlElement(name = "bean")
    private List<BeanDefinition> beans = new ArrayList<>();

    public List<BeanDefinition> getBeans() {
        return beans;
    }
}
