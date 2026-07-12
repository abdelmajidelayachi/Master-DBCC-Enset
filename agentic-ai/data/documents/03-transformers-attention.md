# L'architecture Transformer

L'architecture Transformer, présentée en 2017 par une équipe de chercheurs de Google dans l'article fondateur "Attention Is All You Need" (Vaswani et al.), a bouleversé le traitement automatique du langage naturel. Elle abandonne la récurrence des RNN au profit d'un mécanisme d'attention permettant de traiter toute une séquence en parallèle, ce qui a ouvert la voie aux grands modèles de langage actuels.

## Le mécanisme d'attention

L'idée centrale du Transformer est le mécanisme d'**attention**, qui permet à chaque élément d'une séquence de "regarder" les autres éléments et de pondérer leur importance pour construire sa propre représentation. Concrètement, chaque mot est projeté en trois vecteurs : une requête (query), une clé (key) et une valeur (value). Le score d'attention entre deux mots est calculé par le produit scalaire entre la requête de l'un et la clé de l'autre, puis normalisé par une fonction softmax pour obtenir des poids d'attention qui déterminent combien chaque valeur contribue à la sortie.

La **self-attention** (auto-attention) applique ce mécanisme au sein d'une même séquence : chaque mot compare sa requête aux clés de tous les autres mots de la phrase, y compris lui-même, ce qui permet de capturer des relations à longue distance (par exemple entre un pronom et le nom auquel il se réfère, même séparés de plusieurs dizaines de mots).

La **multi-head attention** (attention multi-têtes) exécute plusieurs mécanismes d'attention en parallèle (typiquement 8 têtes dans l'article original), chacun apprenant à se concentrer sur des types de relations différents (syntaxiques, sémantiques, etc.), avant de concaténer et de combiner leurs résultats. Cela donne au modèle une capacité de représentation beaucoup plus riche qu'une seule attention.

## L'architecture encodeur-décodeur

Le Transformer original est composé de deux blocs empilés. L'**encodeur** lit la séquence d'entrée dans son intégralité et produit une représentation contextualisée de chaque token, en s'appuyant sur des couches de self-attention et de réseaux "feed-forward" entièrement connectés, avec des connexions résiduelles et de la normalisation de couche. Le **décodeur** génère la séquence de sortie token par token, en s'appuyant à la fois sur une self-attention masquée (qui empêche de regarder les tokens futurs) et sur une attention croisée vers les représentations produites par l'encodeur.

## Les embeddings positionnels

Contrairement aux RNN, le Transformer traite tous les tokens d'une séquence simultanément et n'a donc, par construction, aucune notion native de l'ordre des mots. Pour pallier ce manque, on ajoute à chaque embedding de token un **encodage positionnel** (positional encoding), généralement construit à partir de fonctions sinus et cosinus de fréquences différentes selon la position dans la séquence. Cela permet au modèle de distinguer "le chat mange la souris" de "la souris mange le chat" alors même que les tokens sont traités en parallèle.

## Les avantages sur les RNN

Les RNN traitent les séquences de façon strictement séquentielle, un token après l'autre, ce qui rend l'entraînement lent et limite fortement la parallélisation sur GPU. Les Transformers, au contraire, traitent tous les tokens d'une séquence en parallèle, ce qui accélère considérablement l'entraînement sur de grands volumes de données. De plus, l'attention capture directement les dépendances à longue distance en une seule étape de calcul, alors qu'un RNN doit propager l'information pas à pas, avec un risque de perte d'information sur les longues séquences.

## Les modèles dérivés : BERT et GPT

Deux grandes familles de modèles ont émergé à partir de l'architecture Transformer. **BERT** (Bidirectional Encoder Representations from Transformers), publié par Google en 2018, n'utilise que la partie encodeur et est entraîné à prédire des mots masqués en tenant compte du contexte à la fois à gauche et à droite, ce qui en fait un excellent modèle de compréhension du texte (classification, extraction de réponses). **GPT** (Generative Pre-trained Transformer), développé par OpenAI à partir de 2018, n'utilise que la partie décodeur et est entraîné à prédire le mot suivant à partir des mots précédents uniquement, ce qui en fait un modèle naturellement adapté à la génération de texte.
