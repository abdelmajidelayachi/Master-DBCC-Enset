# Compte Rendu — Injection des Dépendances & Mini Framework IOC

**Réalisé par :** Abdelmajid El Ayachi
**Module :** JEE / Systèmes Distribués — Master DBCC
**Ressource vidéo :** [Inversion de contrôle et Injection des dépendances — Mohamed Youssfi](https://www.youtube.com/watch?v=vOLqabN-n2k)

---

## Sommaire

1. [Introduction](#introduction)
2. [Partie 1 : Injection des dépendances](#partie-1--injection-des-dépendances)
   - [Couplage fort vs couplage faible](#couplage-fort-vs-couplage-faible)
   - [Structure du projet](#structure-du-projet)
   - [Injection par instanciation statique](#a-injection-par-instanciation-statique)
   - [Injection par instanciation dynamique](#b-injection-par-instanciation-dynamique)
   - [Injection avec Spring — version XML](#c1-injection-avec-spring--version-xml)
   - [Injection avec Spring — version annotations](#c2-injection-avec-spring--version-annotations)
3. [Partie 2 : Mini Framework d'injection des dépendances](#partie-2--mini-framework-dinjection-des-dépendances)
   - [Conception](#conception)
   - [Version annotations](#version-annotations)
   - [Version XML avec JAXB](#version-xml-avec-jaxb)
   - [Les trois modes d'injection](#les-trois-modes-dinjection)
   - [Exécution](#exécution)
4. [Conclusion](#conclusion)

---

## Introduction

L'**inversion de contrôle (IOC)** est un patron d'architecture dans lequel ce n'est plus la classe qui crée elle-même ses dépendances : c'est un conteneur externe qui les crée et les lui fournit. L'**injection des dépendances (DI)** est le mécanisme qui réalise cette inversion : les objets dont une classe a besoin lui sont *injectés* de l'extérieur (par constructeur, setter ou attribut).

Objectifs de cette activité :

- **Partie 1** : mettre en œuvre l'injection des dépendances de manière statique, dynamique, puis avec le framework **Spring** (XML et annotations) ;
- **Partie 2** : concevoir un **mini framework IOC** similaire à Spring, supportant la configuration **XML (via JAXB)** et par **annotations**, avec injection via **constructeur**, **setter** et **attribut (field)**.

---

## Partie 1 : Injection des dépendances

### Couplage fort vs couplage faible

- **Couplage fort** : la classe `MetierImpl` créerait elle-même sa dépendance avec `new DaoImpl()`. Tout changement d'implémentation impose de **modifier et recompiler** la couche métier — cela viole le principe *ouvert/fermé* (SOLID).
- **Couplage faible** : `MetierImpl` ne connaît que l'**interface** `IDao`. L'implémentation concrète lui est fournie de l'extérieur. On peut alors remplacer la version *base de données* par la version *capteurs* sans toucher au code métier.

### Structure du projet

```
src/main/java/dev/elayachi/
├── part1/
│   ├── dao/            IDao (interface), DaoImpl (version base de données)
│   ├── ext/            DaoImplV2 (version capteurs — extension sans modification)
│   ├── metier/         IMetier (interface), MetierImpl (couplage faible)
│   └── presentation/   PresStatique, PresDynamique, PresSpringXml, PresSpringAnnotation
├── framework/          ← Partie 2 : le mini framework IOC
│   ├── annotations/    @Component, @Autowired
│   ├── core/           ApplicationContext, AnnotationApplicationContext,
│   │                   XmlApplicationContext, ClassScanner, BeanException
│   └── xml/            BeansDefinition, BeanDefinition, ArgDefinition,
│                       PropertyDefinition, FieldDefinition (mapping JAXB)
└── app/                ← application de démonstration du mini framework
    ├── dao/  metier/  presentation/
```

#### 1. L'interface `IDao`

```java
public interface IDao {
    double getData();
}
```

#### 2. Une implémentation de `IDao`

```java
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Version base de données");
        return 34;
    }
}
```

#### 3. L'interface `IMetier`

```java
public interface IMetier {
    double calcul();
}
```

#### 4. Implémentation de `IMetier` avec couplage faible

```java
public class MetierImpl implements IMetier {
    private IDao dao; // couplage faible : référence vers l'INTERFACE

    public MetierImpl(IDao dao) { this.dao = dao; } // injection par constructeur

    public void setDao(IDao dao) { this.dao = dao; } // injection par setter

    @Override
    public double calcul() {
        return dao.getData() * 12;
    }
}
```

> La classe ne fait **jamais** `new DaoImpl()` : elle reçoit sa dépendance de l'extérieur.

### a. Injection par instanciation statique

Classe `PresStatique` : les objets sont créés avec `new` et la dépendance est passée au constructeur.

```java
DaoImpl dao = new DaoImpl();
MetierImpl metier = new MetierImpl(dao);
System.out.println("Résultat = " + metier.calcul());
```

**Sortie :**
```
Version base de données
Résultat = 408.0
```

**Limite** : changer d'implémentation (`DaoImplV2`) exige de modifier le code source et de recompiler.

### b. Injection par instanciation dynamique

Classe `PresDynamique` : les noms des classes sont lus depuis `config.txt` et les objets créés par **réflexion**.

```
dev.elayachi.part1.ext.DaoImplV2
dev.elayachi.part1.metier.MetierImpl
```

```java
Scanner scanner = new Scanner(new File("config.txt"));
Class<?> cDao = Class.forName(scanner.nextLine());
IDao dao = (IDao) cDao.getConstructor().newInstance();

Class<?> cMetier = Class.forName(scanner.nextLine());
IMetier metier = (IMetier) cMetier.getConstructor(IDao.class).newInstance(dao);
```

**Sortie :**
```
Version capteurs
Résultat = 144.0
```

**Avantage** : pour passer de la version capteurs à la version base de données, il suffit d'éditer `config.txt` — **aucune recompilation**. C'est exactement le principe sur lequel reposent les frameworks IOC.

### c.1. Injection avec Spring — version XML

Les beans sont déclarés dans `config.xml` ; Spring les crée et fait l'injection via le **setter** (`<property>`).

```xml
<beans ...>
    <bean id="dao" class="dev.elayachi.part1.dao.DaoImpl"/>
    <bean id="metier" class="dev.elayachi.part1.metier.MetierImpl">
        <property name="dao" ref="dao"/>
    </bean>
</beans>
```

```java
ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
IMetier metier = context.getBean(IMetier.class);
System.out.println("Résultat = " + metier.calcul());
```

### c.2. Injection avec Spring — version annotations

Les classes sont annotées ; Spring scanne le package et fait le câblage automatiquement.

```java
@Repository("dao")
public class DaoImpl implements IDao { ... }

@Service("metier")
public class MetierImpl implements IMetier {
    private IDao dao;

    @Autowired
    public MetierImpl(IDao dao) { this.dao = dao; }
    ...
}
```

```java
ApplicationContext context = new AnnotationConfigApplicationContext("dev.elayachi.part1");
IMetier metier = context.getBean(IMetier.class);
```

**Sortie (XML et annotations) :**
```
Version base de données
Résultat = 408.0
```

---

## Partie 2 : Mini Framework d'injection des dépendances

### Conception

Le mini framework reproduit le cœur de Spring IOC. Il expose une interface `ApplicationContext` avec deux implémentations :

| Classe | Rôle |
|---|---|
| `ApplicationContext` | Contrat du conteneur : `getBean(String)`, `getBean(Class<T>)` |
| `AnnotationApplicationContext` | Conteneur configuré par **annotations** (`@Component`, `@Autowired`) |
| `XmlApplicationContext` | Conteneur configuré par **fichier XML**, lu avec **JAXB** (mapping Objet/XML) |
| `ClassScanner` | Parcourt un package du classpath et charge ses classes (équivalent du *component scan*) |
| `BeanException` | Exception du framework (bean introuvable, dépendance circulaire...) |

Le framework gère : la résolution des dépendances **par type** (interface → implémentation), les beans **singletons**, l'ordre de création récursif des dépendances et la **détection des dépendances circulaires**.

### Version annotations

Deux annotations sont fournies par le framework :

```java
@Retention(RUNTIME) @Target(TYPE)
public @interface Component { String value() default ""; }

@Retention(RUNTIME) @Target({FIELD, CONSTRUCTOR, METHOD})
public @interface Autowired { }
```

Fonctionnement de `AnnotationApplicationContext` :

1. **Scan** : `ClassScanner` parcourt le package de base et charge toutes les classes ; celles annotées `@Component` sont retenues comme composants.
2. **Instanciation** : si un constructeur est annoté `@Autowired`, ses paramètres sont résolus par type puis le constructeur est invoqué (*injection par constructeur*) ; sinon le constructeur par défaut est utilisé.
3. **Injection des attributs** : chaque champ annoté `@Autowired` est rempli par réflexion (`field.set(...)`) (*injection par field*).
4. **Injection des setters** : chaque méthode annotée `@Autowired` est invoquée avec sa dépendance résolue (*injection par setter*).

Utilisation côté programmeur :

```java
@Component("dao")
public class DaoImpl implements IDao { ... }

@Component("metier")
public class MetierImpl implements IMetier {
    @Autowired
    private IDao dao;                                  // injection par attribut

    @Autowired
    public MetierImpl(IDao dao) { this.dao = dao; }    // injection par constructeur

    @Autowired
    public void setDao(IDao dao) { this.dao = dao; }   // injection par setter
    ...
}
```

```java
ApplicationContext context = new AnnotationApplicationContext("dev.elayachi.app");
IMetier metier = context.getBean(IMetier.class);
System.out.println("Résultat = " + metier.calcul());
```

### Version XML avec JAXB

Le fichier de configuration est transformé en objets Java grâce à **JAXB** (OXM : mapping Objet/XML). Les classes de mapping sont annotées `@XmlRootElement`, `@XmlElement`, `@XmlAttribute` :

```java
@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeansDefinition {
    @XmlElement(name = "bean")
    private List<BeanDefinition> beans;
}

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanDefinition {
    @XmlAttribute private String id;
    @XmlAttribute(name = "class") private String className;
    @XmlElement(name = "constructor-arg") private List<ArgDefinition> constructorArgs;
    @XmlElement(name = "property")        private List<PropertyDefinition> properties;
    @XmlElement(name = "field")           private List<FieldDefinition> fields;
}
```

Lecture du fichier :

```java
JAXBContext jaxbContext = JAXBContext.newInstance(BeansDefinition.class);
BeansDefinition defs = (BeansDefinition) jaxbContext.createUnmarshaller().unmarshal(in);
```

Le conteneur crée ensuite chaque bean par réflexion, dans l'ordre de déclaration, en appliquant les injections décrites dans le XML. Les attributs `ref` (référence vers un autre bean) et `value` (valeur simple convertie : `int`, `double`, `boolean`, `String`...) sont supportés.

### Les trois modes d'injection

Fichier `beans.xml` de l'application de démonstration :

```xml
<beans>
    <bean id="dao" class="dev.elayachi.app.dao.DaoImpl"/>

    <!-- 1. Injection via le CONSTRUCTEUR -->
    <bean id="metierConstructeur" class="dev.elayachi.app.metier.MetierImpl">
        <constructor-arg ref="dao"/>
    </bean>

    <!-- 2. Injection via le SETTER -->
    <bean id="metierSetter" class="dev.elayachi.app.metier.MetierImpl">
        <property name="dao" ref="dao"/>
    </bean>

    <!-- 3. Injection directe dans l'ATTRIBUT (Field) -->
    <bean id="metierField" class="dev.elayachi.app.metier.MetierImpl">
        <field name="dao" ref="dao"/>
    </bean>
</beans>
```

| Mode | XML | Annotations | Mécanisme (réflexion) |
|---|---|---|---|
| Constructeur | `<constructor-arg ref="..."/>` | `@Autowired` sur le constructeur | `constructor.newInstance(args)` |
| Setter | `<property name="..." ref="..."/>` | `@Autowired` sur le setter | `method.invoke(bean, dep)` |
| Attribut (Field) | `<field name="..." ref="..."/>` | `@Autowired` sur l'attribut | `field.set(bean, dep)` |

### Exécution

```bash
mvn compile

# Partie 1
mvn exec:java -Dexec.mainClass=dev.elayachi.part1.presentation.PresStatique
mvn exec:java -Dexec.mainClass=dev.elayachi.part1.presentation.PresDynamique
mvn exec:java -Dexec.mainClass=dev.elayachi.part1.presentation.PresSpringXml
mvn exec:java -Dexec.mainClass=dev.elayachi.part1.presentation.PresSpringAnnotation

# Partie 2 : mini framework
mvn exec:java -Dexec.mainClass=dev.elayachi.app.presentation.PresMiniFrameworkXml
mvn exec:java -Dexec.mainClass=dev.elayachi.app.presentation.PresMiniFrameworkAnnotation
```

Sortie de la version XML du mini framework :

```
-> Injection via le CONSTRUCTEUR
-> Injection via le SETTER
=== Bean avec injection via le constructeur ===
Version base de données (mini framework)
Résultat = 408.0
=== Bean avec injection via le setter ===
Version base de données (mini framework)
Résultat = 408.0
=== Bean avec injection via l'attribut (Field) ===
Version base de données (mini framework)
Résultat = 408.0
```

Sortie de la version annotations :

```
-> Injection via le CONSTRUCTEUR
-> Injection via le SETTER
Version base de données (mini framework)
Résultat = 408.0
Résultat (bean par nom) = 408.0
```

---

## Conclusion

Cette activité a permis de suivre toute la progression qui mène aux frameworks IOC :

1. l'**instanciation statique** montre la limite du `new` : tout changement impose une recompilation ;
2. l'**instanciation dynamique** (réflexion + fichier de configuration) résout ce problème et constitue le mécanisme de base des conteneurs IOC ;
3. **Spring** industrialise ce mécanisme avec ses versions XML et annotations ;
4. le **mini framework** développé en Partie 2 reproduit ce fonctionnement : un conteneur `ApplicationContext` qui découvre les beans (scan d'annotations ou fichier XML mappé avec JAXB), les instancie par réflexion et injecte leurs dépendances via **constructeur**, **setter** ou **attribut**.

L'injection des dépendances garantit un **couplage faible** entre les couches d'une application, la rend **fermée à la modification et ouverte à l'extension**, et facilite les tests unitaires (remplacement d'une dépendance par un mock).
