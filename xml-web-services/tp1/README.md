# TP1 — Mesures de températures des villes (XML / DTD / XSD / XSLT)

Voir la vidéo : https://www.youtube.com/watch?v=HQ57ZUkWD8w
On considère un document XML qui présente les mesures des températures des
différentes villes à une date donnée. Chaque mesure est qualifiée par une date et formée par une
ensemble de villes. Chaque ville est qualifiée par un nom et une température.
Travail demandé:
- 1-Faire une représentation graphique de l’arbre XML
- 2-Créer une DTD
- 3- Créer un document XML Valide basé sur la DTD
- 4- Créer un schéma XML
- 5- Créer le document XML Valide basé sur le schéma XML
- 6- Créer une feuille de style qui permet de transformer le document XML en un Document HTML
- 7- Créer une feuille de style qui permet de transformer ce document en un document
SVG qui permet qui représente un histogramme SVG animé.
```
<?xml version="1.0" encoding="UTF-8"?>
<meteo>
  <mesure date="2006-1-1">
        <ville nom="Casa" temperature="22"/> 
        <ville nom="Rabat" temperature="20"/>
        <ville nom="Fes" temperature="18"/>
        <ville nom="Oujda" temperature="19"/>
        <ville nom="Tanger"  temperature="25"/>
        <ville nom="Marrakech"  temperature="28"/>
        <ville nom="Ouarzazat"  temperature="29"/>
        <ville nom="Agadir"  temperature="20"/>
  </mesure> 
  <mesure date="2006-1-2">
        <ville nom="Casa" temperature="21"/> 
        <ville nom="Rabat" temperature="23"/>
        <ville nom="Fes" temperature="19"/>
        <ville nom="Oujda" temperature="20"/>
        <ville nom="Tanger"  temperature="23"/>
        <ville nom="Marrakech"  temperature="27"/>
        <ville nom="Ouarzazat"  temperature="25"/>
        <ville nom="Agadir"  temperature="23"/>
  </mesure>
</meteo>
```

Ouvrir ensuite `meteo.html` et `meteo.svg` dans un navigateur.
