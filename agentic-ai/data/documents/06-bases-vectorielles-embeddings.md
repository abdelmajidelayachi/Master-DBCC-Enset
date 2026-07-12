# Embeddings et bases de données vectorielles

## Qu'est-ce qu'un embedding ?

Un embedding est une représentation numérique d'un objet (mot, phrase, paragraphe, image) sous la forme d'un vecteur de nombres réels, généralement de quelques centaines à quelques milliers de dimensions. L'idée centrale est que des contenus sémantiquement proches doivent produire des vecteurs proches dans cet espace mathématique. Par exemple, les phrases « le chat dort sur le canapé » et « le félin se repose sur le sofa » auront des embeddings très voisins, bien qu'elles ne partagent presque aucun mot commun. Ces vecteurs sont produits par des réseaux de neurones entraînés sur d'immenses corpus de texte, capables de capturer le sens contextuel plutôt qu'une simple correspondance lexicale.

## Mesurer la similarité : cosinus vs distance euclidienne

Pour comparer deux embeddings, deux mesures sont couramment utilisées. La **similarité cosinus** calcule l'angle entre deux vecteurs, indépendamment de leur norme (longueur) ; elle varie entre -1 et 1, et une valeur proche de 1 indique une forte similarité sémantique. C'est la mesure la plus utilisée en recherche documentaire car elle est peu sensible à la longueur du texte encodé. La **distance euclidienne**, elle, mesure la distance géométrique réelle entre deux points dans l'espace vectoriel ; elle est plus sensible à la magnitude des vecteurs. En pratique, lorsque les embeddings sont normalisés (norme égale à 1), les deux mesures deviennent équivalentes en termes de classement des résultats, ce qui explique pourquoi de nombreux modèles normalisent systématiquement leurs sorties.

## Les principaux modèles d'embeddings

Plusieurs familles de modèles dominent le marché en 2024-2025. **text-embedding-004** de Google (disponible via l'API Gemini) produit des vecteurs de 768 dimensions et propose un mode « Matryoshka » permettant de tronquer le vecteur à des tailles plus petites sans trop dégrader la qualité. **text-embedding-3** d'OpenAI existe en deux versions : `small` (1536 dimensions) et `large` (3072 dimensions), avec un excellent rapport qualité-prix pour la recherche sémantique en anglais et en français. Enfin, la bibliothèque **sentence-transformers**, issue des travaux de l'université de Darmstadt (Reimers et Gurevych, 2019), propose des modèles open source comme `all-MiniLM-L6-v2` (384 dimensions, très rapide) que l'on peut exécuter localement sans dépendre d'une API payante.

## Les bases de données vectorielles

Une base vectorielle stocke des embeddings et permet une recherche par similarité à très grande échelle. **Chroma** est une base légère, open source, pensée pour le prototypage rapide dans des projets Python. **FAISS** (Facebook AI Similarity Search), publié par Meta en 2017, est une bibliothèque C++ très performante plutôt qu'une base complète, souvent utilisée comme moteur d'indexation sous-jacent. **Pinecone** est un service managé dans le cloud, pensé pour la production à grande échelle avec une latence garantie. **Qdrant**, écrit en Rust, se distingue par ses filtres de métadonnées avancés. **Weaviate** propose un modèle de données orienté objet avec un support natif du RAG hybride (recherche vectorielle + mots-clés).

## Index ANN et algorithme HNSW

Comparer un vecteur requête à des millions d'autres un par un serait trop lent. Les bases vectorielles utilisent donc des index de recherche approximative du plus proche voisin (**ANN**, Approximate Nearest Neighbor). L'algorithme le plus répandu est **HNSW** (Hierarchical Navigable Small World, proposé par Malkov et Yashunin en 2016), qui construit un graphe hiérarchique à plusieurs niveaux : les niveaux supérieurs contiennent peu de points et permettent de « sauter » rapidement vers la bonne région de l'espace, tandis que le niveau le plus bas contient tous les points pour une recherche fine. HNSW offre un excellent compromis entre vitesse de recherche (souvent sous la milliseconde) et rappel (proportion de vrais voisins retrouvés), au prix d'une consommation mémoire plus importante que des index exacts.

## Stratégies de chunking

Avant de générer des embeddings, un document long doit être découpé en fragments (« chunks »). La taille de chunk influence directement la qualité du RAG : trop petite (100 tokens), elle perd le contexte ; trop grande (plus de 1000 tokens), elle dilue le signal sémantique et augmente le bruit. Une taille de 300 à 500 tokens avec un **overlap** (chevauchement) de 10 à 20 % entre chunks consécutifs est une pratique courante, car elle évite de couper une idée importante en deux fragments qui perdraient chacun le contexte de l'autre.
