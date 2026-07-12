# Évaluation des systèmes RAG et des LLM

## Pourquoi évaluer un système RAG ?

Un système de génération augmentée par récupération (RAG) combine deux composants distincts, chacun susceptible d'introduire des erreurs : le **retriever**, qui sélectionne les documents pertinents, et le **générateur** (le LLM), qui rédige la réponse à partir de ces documents. Une évaluation rigoureuse doit donc mesurer séparément la qualité de la recherche documentaire et la qualité du texte généré, car une mauvaise réponse peut provenir soit d'une recherche qui n'a pas trouvé le bon document, soit d'un modèle qui a mal exploité un document pourtant pertinent.

## Métriques de retrieval

La **precision@k** mesure, parmi les k documents renvoyés par le retriever, la proportion de documents effectivement pertinents. Par exemple, une precision@5 de 0,6 signifie que 3 des 5 documents retournés sont réellement utiles à la question posée. Le **recall@k** mesure, à l'inverse, la proportion de documents pertinents qui figurent parmi les k renvoyés, sur l'ensemble des documents pertinents existants dans la base : un recall@5 de 0,8 signifie que 80 % des documents utiles disponibles ont été retrouvés dans le top 5. Le **MRR** (Mean Reciprocal Rank) évalue la position du premier document pertinent dans la liste des résultats : si le premier document pertinent est en position 2, le rang réciproque vaut 0,5 ; le MRR est la moyenne de ces valeurs sur un ensemble de requêtes de test, et il est particulièrement utile lorsque l'ordre de présentation des résultats compte pour l'utilisateur.

## Métriques de génération

Une fois les documents récupérés, il faut évaluer la réponse produite. La **fidélité** (faithfulness ou groundedness) mesure si chaque affirmation de la réponse est bien étayée par le contenu des documents fournis, sans invention ni extrapolation : c'est la métrique clé pour détecter les hallucinations dans un contexte RAG. L'**answer relevance** évalue si la réponse répond effectivement à la question posée, indépendamment de sa véracité — une réponse peut être fidèle aux documents sans pour autant traiter la question de l'utilisateur si le retriever a fourni de mauvais documents. La **context precision** et le **context recall**, notions proches de celles du retrieval, évaluent quant à elles si le contexte fourni au générateur était bien nécessaire et suffisant.

## LLM-as-a-judge

Ces métriques de génération sont difficiles à calculer par des règles simples car elles portent sur du texte libre. La méthode du **LLM-as-a-judge** consiste à utiliser un grand modèle de langage (souvent un modèle plus puissant que celui évalué, par exemple GPT-4 ou Claude Opus) pour noter automatiquement une réponse selon des critères définis dans un prompt d'évaluation. Cette approche, popularisée notamment par l'article MT-Bench de Zheng et al. (2023), permet d'évaluer à grande échelle sans recourir systématiquement à des annotateurs humains, tout en conservant une corrélation élevée avec le jugement humain sur de nombreuses tâches. Elle comporte cependant des biais connus, comme une tendance à favoriser les réponses plus longues ou similaires en style à celui du juge.

## Frameworks d'évaluation

**RAGAS** (Retrieval Augmented Generation Assessment, publié en 2023) est une bibliothèque open source spécifiquement conçue pour calculer automatiquement les métriques de fidélité, de pertinence de réponse et de précision de contexte, en s'appuyant sur des appels LLM-as-a-judge configurables. **LangSmith**, développé par l'équipe de LangChain, est une plateforme d'observabilité qui permet de tracer chaque étape d'un pipeline agentique ou RAG en production, de constituer des jeux de données de test à partir de cas réels, et de comparer différentes versions d'un pipeline sur un même jeu d'évaluation.

## Benchmarks, latence et coût

Au-delà des métriques qualitatives, des benchmarks publics comme MMLU, HellaSwag ou plus récemment MTEB (Massive Text Embedding Benchmark) permettent de comparer les modèles de manière standardisée. Mais en production, deux facteurs opérationnels sont tout aussi déterminants que la qualité brute : le **temps de réponse** (latence), souvent mesuré en temps jusqu'au premier token puis en tokens par seconde, et le **coût** par requête, calculé en dollars pour mille ou un million de tokens consommés. Un système RAG légèrement moins précis mais deux fois moins coûteux et trois fois plus rapide peut s'avérer préférable pour un cas d'usage à fort volume, ce qui impose d'arbitrer conjointement qualité, latence et coût plutôt que d'optimiser la seule précision.
