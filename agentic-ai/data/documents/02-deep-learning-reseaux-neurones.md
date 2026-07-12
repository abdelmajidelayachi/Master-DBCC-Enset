# Deep Learning et Réseaux de Neurones

Le deep learning (apprentissage profond) est une famille de méthodes de machine learning fondée sur des réseaux de neurones artificiels comportant plusieurs couches successives. Ces architectures profondes ont permis des progrès spectaculaires depuis le début des années 2010, notamment en vision par ordinateur et en traitement du langage naturel.

## Le perceptron, brique élémentaire

Le perceptron, introduit par Frank Rosenblatt en 1958, est l'unité de base d'un réseau de neurones. Il reçoit plusieurs entrées numériques, les multiplie chacune par un poids, additionne le résultat avec un biais, puis applique une fonction d'activation pour produire une sortie. Un réseau de neurones "profond" empile plusieurs couches de tels neurones : une couche d'entrée, une ou plusieurs couches cachées, et une couche de sortie. C'est cet empilement de couches non linéaires qui donne au réseau sa capacité à approximer des fonctions complexes.

## Les fonctions d'activation

Sans fonction d'activation non linéaire, un empilement de couches se réduirait mathématiquement à une simple transformation linéaire. La **sigmoïde** compresse toute valeur réelle dans l'intervalle [0, 1], ce qui la rend utile pour des sorties de probabilité, mais elle souffre du problème de "vanishing gradient" (gradient qui s'annule) sur les réseaux profonds. La **tanh** (tangente hyperbolique) est similaire mais centrée sur zéro, avec des valeurs entre -1 et 1. La **ReLU** (Rectified Linear Unit, définie par max(0, x)) est devenue la fonction la plus utilisée dans les couches cachées depuis les années 2010 car elle est simple à calculer et limite le problème du gradient qui s'annule. La **softmax** transforme un vecteur de scores bruts en une distribution de probabilités sommant à 1, et s'utilise typiquement en sortie d'un réseau de classification multi-classes.

## Rétropropagation et descente de gradient

L'entraînement d'un réseau de neurones repose sur l'algorithme de **rétropropagation du gradient** (backpropagation), popularisé en 1986 par Rumelhart, Hinton et Williams. Il calcule, couche par couche en remontant de la sortie vers l'entrée, la contribution de chaque poids à l'erreur globale, grâce à la règle de dérivation en chaîne. Ces gradients sont ensuite utilisés par un algorithme de **descente de gradient** pour ajuster les poids dans la direction qui réduit l'erreur. En pratique, on utilise souvent une variante appelée descente de gradient stochastique (SGD) ou des optimiseurs plus élaborés comme Adam, qui adaptent le taux d'apprentissage au cours de l'entraînement.

## Les architectures spécialisées

Les **CNN** (réseaux de neurones convolutifs) sont conçus pour traiter des données structurées en grille, comme des images. Ils appliquent des filtres convolutifs qui détectent des motifs locaux (contours, textures) puis les combinent progressivement en concepts plus abstraits (yeux, visages, objets). Le réseau AlexNet, qui a remporté le concours ImageNet en 2012 avec un taux d'erreur top-5 de 15,3 %, a marqué le véritable décollage du deep learning moderne.

Les **RNN** (réseaux de neurones récurrents) sont adaptés aux données séquentielles comme le texte ou les séries temporelles : ils maintiennent un état caché qui se met à jour à chaque pas de temps, ce qui leur permet de tenir compte du contexte passé. Cependant, les RNN classiques peinent à mémoriser des dépendances sur de longues séquences. Les **LSTM** (Long Short-Term Memory), introduits par Hochreiter et Schmidhuber en 1997, résolvent ce problème grâce à des portes (d'entrée, d'oubli, de sortie) qui régulent le flux d'information dans une cellule de mémoire.

## La régularisation

Pour limiter le surapprentissage sur des réseaux comportant parfois des millions de paramètres, plusieurs techniques de régularisation sont utilisées. Le **dropout**, proposé par Geoffrey Hinton et son équipe en 2014, désactive aléatoirement une fraction des neurones (souvent 20 à 50 %) à chaque itération d'entraînement, ce qui force le réseau à ne pas trop dépendre de neurones particuliers. La **batch normalization**, introduite en 2015, normalise les activations de chaque couche sur un mini-lot de données, ce qui stabilise et accélère considérablement l'entraînement des réseaux profonds.
