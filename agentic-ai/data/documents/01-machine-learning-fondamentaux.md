# Fondamentaux du Machine Learning

Le machine learning (apprentissage automatique) est une branche de l'intelligence artificielle qui permet à un système d'apprendre à partir de données, sans être explicitement programmé pour chaque règle. Plutôt que d'écrire des instructions fixes, on entraîne un modèle mathématique à repérer des régularités dans des exemples afin qu'il puisse généraliser à de nouvelles situations.

## Les trois grands paradigmes d'apprentissage

**L'apprentissage supervisé** consiste à entraîner un modèle sur des données étiquetées, c'est-à-dire des exemples pour lesquels on connaît déjà la bonne réponse. Par exemple, pour prédire le prix d'un appartement, on dispose d'un historique de biens avec leur surface, leur localisation et leur prix de vente réel. Le modèle apprend la relation entre les variables d'entrée (les features) et la sortie attendue (le label). On distingue la régression (prédire une valeur continue, comme un prix en euros) et la classification (prédire une catégorie, comme "spam" ou "non-spam").

**L'apprentissage non supervisé** travaille sur des données non étiquetées : le modèle doit découvrir seul une structure sous-jacente. Le clustering (par exemple l'algorithme K-means) regroupe des clients aux comportements d'achat similaires sans savoir à l'avance combien de groupes existent ni ce qu'ils représentent. La réduction de dimensionnalité, comme l'ACP (analyse en composantes principales), permet de compresser des données à forte dimension en conservant l'essentiel de l'information.

**L'apprentissage par renforcement** repose sur un agent qui interagit avec un environnement, prend des actions et reçoit des récompenses ou des pénalités. L'agent apprend progressivement une stratégie (une politique) qui maximise la récompense cumulée. C'est ce paradigme qui a permis à AlphaGo, développé par DeepMind, de battre le champion du monde de go Lee Sedol en 2016 après des millions de parties jouées contre lui-même.

## Les features : la matière première du modèle

Les "features" (variables explicatives) sont les caractéristiques numériques ou catégorielles utilisées en entrée du modèle. Le "feature engineering", c'est-à-dire la sélection et la transformation de ces variables (normalisation, encodage des catégories, création de nouvelles variables dérivées), reste une étape déterminante : un modèle sophistiqué avec des features de mauvaise qualité produira de moins bons résultats qu'un modèle simple avec des features pertinentes.

## Overfitting et underfitting

Le **surapprentissage (overfitting)** survient lorsqu'un modèle apprend trop précisément les particularités et le bruit des données d'entraînement, au point de perdre sa capacité à généraliser sur de nouvelles données. Le **sous-apprentissage (underfitting)** est le problème inverse : le modèle est trop simple pour capturer les régularités présentes dans les données, et il échoue aussi bien à l'entraînement qu'en généralisation. L'objectif est de trouver un compromis biais-variance équilibré.

## Évaluer un modèle : train/test split et validation croisée

Pour estimer la capacité de généralisation d'un modèle, on divise généralement les données en un ensemble d'entraînement (souvent 70 à 80 %) et un ensemble de test (20 à 30 %), ce dernier n'étant utilisé qu'à la toute fin pour mesurer la performance réelle. La **validation croisée** (cross-validation), en particulier la k-fold cross-validation (souvent avec k=5 ou k=10), consiste à découper les données d'entraînement en k blocs et à entraîner k fois le modèle en utilisant à chaque tour un bloc différent comme validation. Cela donne une estimation plus robuste de la performance qu'une simple division unique.

## Les métriques d'évaluation

Pour la classification, l'**accuracy** (exactitude) mesure la proportion de prédictions correctes, mais elle devient trompeuse sur des jeux de données déséquilibrés. La **précision** indique, parmi les exemples prédits positifs, la proportion réellement positive ; le **rappel** (recall) indique, parmi les exemples réellement positifs, la proportion correctement détectée. Le **score F1** est la moyenne harmonique entre précision et rappel, utile quand on veut un compromis entre les deux. Pour la régression, la **RMSE** (racine de l'erreur quadratique moyenne) mesure l'écart moyen entre les valeurs prédites et réelles, en pénalisant davantage les grandes erreurs.
