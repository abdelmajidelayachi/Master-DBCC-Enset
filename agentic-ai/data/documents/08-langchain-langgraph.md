# LangChain et LangGraph

## LangChain : un framework d'orchestration pour LLM

LangChain, publié en open source en octobre 2022 par Harrison Chase, est un framework Python (et JavaScript) qui facilite la construction d'applications s'appuyant sur des grands modèles de langage. Son concept central est la **chaîne** (chain) : une séquence d'étapes reliant un prompt, un appel modèle et un traitement de sortie. LangChain fournit également des abstractions réutilisables pour les briques les plus courantes d'une application LLM : les **prompts** (templates paramétrables), les **retrievers** (composants chargés de récupérer des documents pertinents depuis une base vectorielle ou une autre source), et les **loaders** (connecteurs permettant d'ingérer des données depuis des PDF, des pages web, des bases SQL ou des API tierces). Grâce à ces abstractions, un développeur peut assembler rapidement un pipeline de RAG (retrieval-augmented generation) sans réécrire la logique de bas niveau à chaque projet.

## Les limites des chaînes linéaires

Le modèle de chaîne linéaire de LangChain, bien qu'efficace pour des cas d'usage simples, montre ses limites dès qu'une application nécessite des décisions conditionnelles, des boucles ou une reprise en cas d'échec. Une chaîne classique exécute ses étapes dans un ordre fixe, de A vers B vers C, sans possibilité native de revenir en arrière si le résultat de B est insatisfaisant, ni de choisir dynamiquement entre plusieurs chemins selon le contexte. Or les agents modernes ont justement besoin de cycles : reformuler une requête si la recherche documentaire ne renvoie rien de pertinent, relancer un outil après une erreur, ou faire intervenir un humain avant de continuer. C'est pour répondre à ce besoin que l'équipe de LangChain a lancé **LangGraph** en janvier 2024.

## LangGraph : modéliser un agent comme un graphe d'états

LangGraph représente une application agentique sous la forme d'un **graphe d'états** plutôt que d'une chaîne linéaire. Chaque **nœud** du graphe est une fonction ou un appel LLM qui reçoit l'état courant et retourne une mise à jour de cet état. Les **arêtes** relient les nœuds entre eux, et peuvent être **conditionnelles** : une fonction de routage examine l'état après un nœud et décide dynamiquement vers quel nœud suivant se diriger (par exemple, continuer à chercher des informations ou passer à la génération de la réponse finale). L'état lui-même est **typé**, généralement défini via une classe `TypedDict` ou un modèle Pydantic, ce qui garantit que chaque nœud manipule des données à la structure prévisible (liste de messages, documents récupérés, nombre d'itérations, etc.).

## Checkpointer, persistance et cycles

LangGraph introduit la notion de **checkpointer** : un mécanisme qui sauvegarde l'état du graphe à chaque étape dans une base (mémoire, SQLite, Postgres). Cela permet de mettre en pause une exécution, de la reprendre plus tard exactement où elle s'est arrêtée, ou de conserver une mémoire conversationnelle persistante entre plusieurs sessions d'un même utilisateur. Contrairement à une chaîne, un graphe LangGraph autorise nativement les **cycles** : un nœud peut renvoyer l'exécution vers un nœud précédent, ce qui est indispensable pour implémenter une boucle ReAct où l'agent réitère ses appels d'outils jusqu'à ce qu'il juge disposer d'assez d'informations.

## Différence avec create_agent et AgentExecutor

LangChain propose aussi des abstractions prêtes à l'emploi comme l'ancien `AgentExecutor` ou, plus récemment, `create_agent`, qui permettent de créer un agent ReAct fonctionnel en quelques lignes de code, avec une boucle d'outils déjà implémentée en interne. Ces abstractions conviennent aux cas standards mais offrent moins de contrôle : on ne voit pas explicitement l'état ni la logique de routage. LangGraph, à l'inverse, expose chaque étape du graphe, ce qui le rend plus verbeux à écrire mais beaucoup plus flexible pour des workflows métier complexes nécessitant une supervision fine, des points de contrôle humains, ou des branches conditionnelles multiples.

## Cas d'usage : RAG agentique

Le **RAG agentique** (agentic RAG) illustre bien l'intérêt de LangGraph : au lieu d'exécuter systématiquement une recherche documentaire suivie d'une génération, l'agent évalue d'abord si une recherche est nécessaire, peut reformuler la requête si les premiers résultats sont peu pertinents, interroger plusieurs sources différentes, puis vérifier la cohérence de sa réponse avant de la renvoyer à l'utilisateur — un enchaînement dynamique impossible à exprimer proprement avec une simple chaîne linéaire.
