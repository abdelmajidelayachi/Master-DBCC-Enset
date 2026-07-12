# Le RAG (Retrieval-Augmented Generation)

Le RAG, ou génération augmentée par récupération, est une architecture qui combine un modèle de langage génératif avec un système de recherche documentaire externe. Popularisée par un article de recherche de Meta AI publié en 2020 (Lewis et al.), cette approche permet à un LLM de fonder ses réponses sur des documents précis plutôt que sur ses seules connaissances internes acquises pendant l'entraînement.

## Pourquoi le RAG : motivations

Les grands modèles de langage souffrent de deux limites majeures que le RAG cherche à résoudre. D'une part, les **hallucinations** : un modèle peut générer avec assurance une information fausse ou inventée, faute de connaître réellement la réponse. D'autre part, la **coupure de connaissances** : un modèle entraîné jusqu'à une certaine date ignore les événements ou documents plus récents, ainsi que les données privées d'une entreprise, absentes de son corpus d'entraînement. En donnant au modèle accès, au moment de la génération, à des documents pertinents et à jour, le RAG ancre ses réponses sur des sources vérifiables et permet de mettre à jour les connaissances disponibles sans réentraîner le modèle, ce qui serait extrêmement coûteux.

## Le pipeline complet du RAG

Un système RAG suit généralement une chaîne de traitement en plusieurs étapes. Le **chargement** (loading) consiste à importer les documents sources dans des formats variés (PDF, HTML, Markdown, bases de données) et à en extraire le texte brut. Le **chunking** (découpage) divise ensuite ces documents en fragments de taille raisonnable, souvent entre 200 et 1000 tokens, avec un léger chevauchement entre fragments afin de ne pas couper une idée importante en deux morceaux séparés.

Chaque fragment est ensuite transformé en un **embedding**, c'est-à-dire un vecteur numérique de dimension fixe (souvent entre 384 et 1536 dimensions) qui capture le sens sémantique du texte, grâce à un modèle d'embedding spécialisé. Ces vecteurs sont stockés dans une **base de données vectorielle** (comme FAISS, Chroma, Pinecone ou Weaviate), optimisée pour la recherche rapide de similarité parmi des millions de vecteurs.

Lorsqu'un utilisateur pose une question, celle-ci est à son tour transformée en embedding, puis comparée à tous les vecteurs de la base grâce à la **similarité cosinus**, une mesure qui évalue l'angle entre deux vecteurs plutôt que leur distance brute : plus l'angle est petit, plus les textes sont sémantiquement proches. Les k fragments les plus proches (souvent k=3 à k=10) sont récupérés lors de cette étape de **retrieval** (récupération).

Enfin, ces fragments récupérés sont insérés dans le prompt envoyé au LLM, aux côtés de la question originale, lors de l'étape de **génération augmentée** : le modèle rédige sa réponse en s'appuyant explicitement sur le contenu fourni, ce qui réduit fortement le risque d'invention et permet souvent de citer les sources utilisées.

## Avantages et limites

Le RAG présente plusieurs avantages notables : il met à jour la base de connaissances sans réentraîner le modèle, il réduit les hallucinations en ancrant les réponses sur des documents réels, et il permet de traiter des données privées ou spécialisées (documentation interne, articles scientifiques récents) sans les exposer lors d'un entraînement.

Cependant, le RAG a aussi ses limites. La qualité de la réponse dépend fortement de celle du retrieval : si les documents pertinents ne sont pas récupérés, ou si le chunking a coupé une information au mauvais endroit, le modèle produira une réponse incomplète ou erronée. Le système ajoute aussi une latence supplémentaire (recherche vectorielle avant génération) et une complexité d'infrastructure (maintenance de la base vectorielle, mise à jour des embeddings).

## RAG naïf versus RAG avancé

Le **RAG naïf** se limite au pipeline de base décrit ci-dessus : découpage, embedding, recherche par similarité, puis génération, sans étape d'optimisation intermédiaire. Le **RAG avancé** ajoute des raffinements pour améliorer la pertinence des réponses. Le **re-ranking** récupère d'abord un nombre plus large de candidats (par exemple 50 fragments), puis utilise un second modèle, plus précis mais plus coûteux, pour réordonner ces candidats et ne conserver que les meilleurs avant de les transmettre au LLM. La **query rewriting** (reformulation de requête) consiste à faire reformuler la question par un modèle de langage avant la recherche, par exemple pour la décomposer en sous-questions plus précises ou la rapprocher du vocabulaire des documents source, ce qui améliore sensiblement le retrieval.
