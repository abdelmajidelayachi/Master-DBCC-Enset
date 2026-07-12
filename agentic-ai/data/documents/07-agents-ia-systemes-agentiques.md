# Les agents IA et les systèmes agentiques

## Définition d'un agent IA

Un agent IA est un système qui combine un grand modèle de langage (LLM) avec une boucle de raisonnement et la capacité d'agir sur son environnement via des outils externes. Contrairement à un simple appel LLM qui produit une réponse unique à partir d'un prompt, un agent observe le résultat de ses actions, ajuste son plan et répète ce cycle jusqu'à atteindre un objectif donné. Cette boucle se résume souvent en trois étapes : **percevoir** (lire le contexte et les résultats précédents), **raisonner** (décider de la prochaine action) et **agir** (exécuter un outil ou produire une réponse finale). Un agent peut ainsi accomplir des tâches complexes en plusieurs étapes, comme réserver un voyage, déboguer du code ou répondre à une question nécessitant plusieurs recherches successives.

## Le paradigme ReAct

Le paradigme **ReAct** (Reasoning + Acting), proposé par Yao et al. dans un article de 2022 (Google Research et Princeton), formalise cette boucle en alternant explicitement des étapes de raisonnement textuel (« Thought ») et des actions concrètes (« Action »), suivies d'une observation du résultat (« Observation »). Par exemple, pour répondre à « Quel est le PDG de l'entreprise qui a créé ChatGPT ? », l'agent va raisonner qu'il doit d'abord identifier l'entreprise, effectuer une recherche, observer que la réponse est OpenAI, puis raisonner qu'il doit maintenant chercher qui en est le PDG, effectuer une seconde recherche, et enfin formuler la réponse finale. Cette approche a démontré une réduction significative des erreurs de raisonnement (hallucinations) par rapport à un raisonnement en chaîne (chain-of-thought) sans accès à des outils externes.

## Function calling et tool use

Le **function calling** (ou tool use) est le mécanisme technique qui permet à un LLM d'invoquer des fonctions externes. Le modèle reçoit la description des outils disponibles (nom, paramètres, description) dans son prompt système, et génère en sortie un appel structuré (généralement au format JSON) indiquant quel outil appeler et avec quels arguments. Le code applicatif exécute alors réellement cette fonction (par exemple une requête météo, une recherche dans une base de données, un calcul) et renvoie le résultat au modèle pour qu'il poursuive son raisonnement. Anthropic (avec Claude), OpenAI (avec GPT-4) et Google (avec Gemini) proposent tous une API native de function calling depuis 2023-2024, rendant ce mécanisme standard dans l'industrie.

## Planification

Pour des tâches complexes, un agent doit souvent élaborer un plan avant d'agir plutôt que de réagir étape par étape. Des techniques comme **Plan-and-Solve** ou **Tree of Thoughts** consistent à décomposer un objectif en sous-tâches ordonnées, éventuellement en explorant plusieurs chemins de résolution en parallèle et en évaluant lequel est le plus prometteur avant de s'y engager. Cette planification réduit le risque de blocage sur une approche sous-optimale choisie trop tôt.

## Mémoire à court terme et à long terme

La **mémoire à court terme** correspond à la fenêtre de contexte du LLM : l'ensemble des messages, observations et résultats d'outils inclus dans le prompt courant, limitée par la taille maximale de contexte du modèle (par exemple 200 000 tokens pour Claude 3.5 Sonnet). La **mémoire à long terme**, elle, persiste au-delà d'une seule session : elle est généralement implémentée via une base vectorielle qui stocke des résumés, des faits ou des préférences utilisateur, récupérés par recherche sémantique lors des sessions futures. Cette distinction est essentielle pour construire des assistants capables d'apprendre progressivement des interactions passées sans réinjecter tout l'historique brut dans chaque prompt.

## Systèmes multi-agents

Un système multi-agents répartit une tâche complexe entre plusieurs agents spécialisés qui collaborent, chacun avec un rôle défini (par exemple un agent « chercheur », un agent « rédacteur », un agent « critique »). Des frameworks comme AutoGen (Microsoft) ou CrewAI structurent ces échanges via des messages ou un orchestrateur central. Cette approche améliore la qualité sur des tâches comme la revue de code automatisée, la génération de rapports d'analyse financière, ou le support client à plusieurs niveaux d'escalade, en imitant une organisation où chaque expert se concentre sur sa spécialité plutôt que de tout confier à un modèle généraliste unique.
