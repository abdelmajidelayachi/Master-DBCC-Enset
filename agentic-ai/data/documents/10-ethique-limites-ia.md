# Éthique et limites de l'intelligence artificielle

## Les biais des données

Les modèles d'IA apprennent à partir de données produites par des humains, et reproduisent donc les biais historiques et sociaux présents dans ces données. Un modèle de recrutement entraîné sur des décisions passées peut par exemple défavoriser certains groupes si les décisions historiques elles-mêmes étaient biaisées, un phénomène documenté dès 2018 avec l'abandon par Amazon d'un outil interne de tri de CV jugé discriminant envers les candidatures féminines. Ces biais peuvent concerner le genre, l'origine, l'âge ou la langue, et se manifestent aussi bien dans les modèles de classification que dans les grands modèles de langage, qui peuvent associer certains métiers à un genre ou reproduire des stéréotypes culturels présents dans leurs corpus d'entraînement. Corriger ces biais nécessite un travail sur les données d'entraînement et des audits réguliers des sorties en production.

## Les hallucinations

On appelle **hallucination** le phénomène par lequel un modèle génère une information fausse ou inventée avec une grande confiance apparente, sans signaler d'incertitude. Ce risque provient de la nature même des grands modèles de langage, entraînés à prédire la suite statistiquement la plus probable d'un texte plutôt qu'à vérifier des faits. Un cas emblématique s'est produit en 2023 lorsqu'un avocat new-yorkais a soumis à un tribunal des références de jurisprudence entièrement inventées par ChatGPT, non vérifiées avant dépôt. Les architectures RAG, en ancrant les réponses sur des documents vérifiés, réduisent ce risque sans l'éliminer complètement, car le modèle peut encore mal interpréter ou extrapoler au-delà du contenu fourni.

## Confidentialité des données

L'utilisation de LLM soulève des enjeux de confidentialité à plusieurs niveaux : les données envoyées à une API tierce (comme celle d'OpenAI ou d'Anthropic) peuvent transiter par des serveurs externes, ce qui pose problème pour des données sensibles (santé, données personnelles, secrets industriels). Certaines entreprises optent pour des modèles auto-hébergés (comme Llama ou Mistral) pour garder le contrôle total sur leurs données. Le RGPD, en vigueur dans l'Union européenne depuis 2018, encadre strictement le traitement des données personnelles par ces systèmes, notamment le droit à l'explication et le droit à l'effacement, qui posent des défis techniques particuliers pour des modèles ayant mémorisé des données dans leurs poids.

## Empreinte énergétique

L'entraînement et l'utilisation des grands modèles de langage consomment une quantité significative d'énergie. Selon une étude de l'université du Massachusetts (Strubell et al., 2019), l'entraînement d'un seul grand modèle de traitement du langage pouvait à l'époque émettre l'équivalent de plusieurs dizaines de tonnes de CO2. Depuis, la taille des modèles a considérablement augmenté, et l'inférence à grande échelle (des milliards de requêtes quotidiennes sur des services comme ChatGPT) représente une part croissante de la consommation énergétique totale de l'IA, ce qui pousse les fournisseurs de cloud à investir dans des centres de données plus efficaces et des sources d'énergie bas carbone.

## Régulation : l'AI Act européen

L'Union européenne a adopté en 2024 l'**AI Act**, premier cadre réglementaire complet au monde dédié à l'intelligence artificielle. Ce règlement classe les systèmes d'IA selon leur niveau de risque : les systèmes à **risque inacceptable** (comme la notation sociale généralisée) sont interdits ; les systèmes à **risque élevé** (recrutement, crédit, santé, justice) sont soumis à des obligations strictes de documentation, de gestion des risques et de supervision humaine ; les systèmes à risque limité doivent respecter des obligations de transparence, comme signaler qu'un contenu a été généré par IA. Les modèles à usage général les plus puissants sont soumis à des obligations spécifiques de transparence sur leurs données d'entraînement. L'application progressive du texte s'étend de 2025 à 2027 selon les catégories de risque concernées.

## Transparence, explicabilité et bonnes pratiques

La **transparence** consiste à informer clairement les utilisateurs qu'ils interagissent avec une IA et sur quelles données elle s'appuie. L'**explicabilité** vise à rendre compréhensibles les décisions d'un modèle, un enjeu difficile pour les réseaux de neurones profonds souvent qualifiés de « boîtes noires ». Parmi les bonnes pratiques de déploiement responsable figurent : la supervision humaine sur les décisions à fort impact, la documentation des limites connues (fiche modèle ou « model card »), des tests réguliers de biais et de robustesse avant et après mise en production, ainsi que des mécanismes clairs de retour utilisateur pour signaler une réponse erronée.
