# Les grands modèles de langage (LLM)

Les grands modèles de langage (Large Language Models, ou LLM) sont des réseaux de neurones de type Transformer entraînés sur d'immenses volumes de texte, capables de comprendre et de générer du langage naturel avec une grande fluidité. Depuis 2020, leur taille et leurs capacités ont connu une croissance très rapide, jusqu'à devenir des outils largement utilisés pour la rédaction, la traduction, le résumé ou l'assistance au code.

## Le pré-entraînement sur corpus massifs

La première phase de construction d'un LLM est le **pré-entraînement**, durant lequel le modèle apprend, de façon auto-supervisée, à prédire le prochain mot (ou token) d'une séquence à partir d'un corpus gigantesque de textes : pages web, livres, articles scientifiques, code source, etc. GPT-3, publié par OpenAI en 2020, comptait 175 milliards de paramètres et avait été entraîné sur plusieurs centaines de milliards de tokens. Cette phase, extrêmement coûteuse en calcul (parfois plusieurs dizaines de millions de dollars pour les plus grands modèles), permet au modèle d'acquérir des connaissances générales sur le monde, la grammaire, les faits et un certain raisonnement, sans supervision humaine explicite sur chaque exemple.

## Le fine-tuning et le RLHF

Un modèle pré-entraîné brut prédit simplement la suite statistiquement probable d'un texte, mais ne sait pas nécessairement suivre des instructions ni refuser des requêtes problématiques. Le **fine-tuning** (affinage) consiste à poursuivre l'entraînement sur un jeu de données plus restreint et ciblé, par exemple des paires instruction-réponse rédigées par des humains, pour aligner le comportement du modèle sur l'usage souhaité.

Le **RLHF** (Reinforcement Learning from Human Feedback), popularisé par InstructGPT (OpenAI, 2022) puis par ChatGPT (lancé en novembre 2022), va plus loin : des humains classent plusieurs réponses générées par le modèle selon leur qualité, ce qui sert à entraîner un modèle de récompense ; ce dernier est ensuite utilisé pour ajuster le modèle de langage via un algorithme d'apprentissage par renforcement (souvent PPO), afin de le rendre plus utile, plus honnête et moins nocif.

## Le prompt engineering

Le prompt engineering est l'art de formuler les instructions données à un LLM pour obtenir la meilleure réponse possible. En **zero-shot**, on demande directement au modèle d'effectuer une tâche sans lui fournir d'exemple ("Traduis cette phrase en anglais"). En **few-shot**, on fournit dans le prompt quelques exemples de la tâche avant la question réelle, ce qui améliore souvent nettement la qualité et le format de la réponse. Le **chain-of-thought** (chaîne de pensée), décrit dans un article de Google de 2022, consiste à demander au modèle de détailler son raisonnement étape par étape avant de donner sa réponse finale, ce qui améliore significativement les performances sur des tâches de raisonnement logique ou mathématique.

## Les limites des LLM

Malgré leurs performances impressionnantes, les LLM présentent plusieurs limites importantes. Les **hallucinations** désignent la production, avec assurance, d'informations fausses ou inventées : un modèle peut citer un article scientifique inexistant ou attribuer une fausse date à un événement réel. La **coupure de connaissances** (knowledge cutoff) signifie que le modèle n'a aucune connaissance des événements postérieurs à la date de fin de son corpus d'entraînement, sauf s'il a accès à des outils de recherche externes. Enfin, le **coût** de l'entraînement et de l'inférence reste considérable : entraîner un modèle de plusieurs centaines de milliards de paramètres nécessite des milliers de GPU fonctionnant pendant plusieurs semaines, et chaque requête envoyée au modèle consomme également de l'énergie et des ressources de calcul.

## Panorama des modèles actuels

Plusieurs familles de LLM dominent le paysage actuel. **GPT-4**, publié par OpenAI en mars 2023, a introduit des capacités multimodales (texte et image). **Claude**, développé par Anthropic, met l'accent sur la sécurité et l'alignement du modèle via une méthode appelée "IA constitutionnelle". **Gemini**, développé par Google DeepMind et lancé fin 2023, est conçu nativement multimodal (texte, image, audio, vidéo). **Llama**, la famille de modèles à poids ouverts publiée par Meta depuis 2023, a favorisé l'essor de la recherche et des applications open source en rendant les poids des modèles librement téléchargeables par la communauté.
