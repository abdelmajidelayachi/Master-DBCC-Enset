"""Jeu de questions pour l'evaluation : 10 simples + 10 complexes."""

QUESTIONS_SIMPLES = [
    "Qu'est-ce que l'apprentissage supervise ?",
    "Qu'est-ce que le surapprentissage (overfitting) ?",
    "Qu'est-ce qu'un embedding ?",
    "Que signifie l'acronyme RAG ?",
    "Qu'est-ce que la fonction d'activation ReLU ?",
    "Qu'est-ce que le mecanisme d'attention dans un Transformer ?",
    "Qu'est-ce qu'un agent IA ?",
    "Qu'est-ce que LangGraph ?",
    "Qu'est-ce qu'une hallucination d'un LLM ?",
    "Qu'est-ce que la similarite cosinus ?",
]

QUESTIONS_COMPLEXES = [
    "Compare l'architecture Transformer aux RNN : pourquoi les Transformers "
    "les ont-ils remplaces pour le traitement du langage ?",
    "Explique le pipeline complet d'un systeme RAG, du chargement des documents "
    "jusqu'a la generation de la reponse, en precisant le role du chunking.",
    "Quelles sont les differences entre BERT et GPT en termes d'architecture "
    "et de cas d'usage ?",
    "Pourquoi un systeme agentic RAG avec LangGraph est-il preferable a une "
    "chaine RAG lineaire classique ? Donne des exemples de capacites ajoutees.",
    "Comment evaluer la qualite d'un systeme RAG ? Presente les metriques de "
    "retrieval et de generation, et le principe du LLM-as-a-judge.",
    "Explique le paradigme ReAct et son lien avec le function calling dans "
    "les agents IA.",
    "Quels sont les compromis a considerer lors du choix de la taille des "
    "chunks et de l'overlap dans un systeme RAG ?",
    "En quoi le RLHF ameliore-t-il les LLM et quelles sont ses limites ?",
    "Quels risques ethiques pose le deploiement de LLM en production et "
    "quelles bonnes pratiques permettent de les attenuer ?",
    "Compare les bases vectorielles Chroma, FAISS et Pinecone : quand "
    "choisir l'une plutot que l'autre ?",
]
