"""Etat du graphe : donnees partagees entre les noeuds."""

from typing import Annotated, TypedDict

from langchain_core.documents import Document
from langgraph.graph.message import add_messages


class AgentState(TypedDict):
    """Etat transporte le long du graphe agentic RAG.

    - messages : historique de la conversation (memoire court terme,
      persistee entre les tours grace au checkpointer)
    - question : question courante, eventuellement reformulee
    - original_question : question initiale de l'utilisateur
    - documents : documents juges pertinents pour la question
    - generation : reponse produite par le LLM
    - datasource : decision du routeur (vectorstore | web | direct | calcul)
    - rewrites : nombre de reformulations deja effectuees
    - retrieval_grade : verdict du grader sur les documents (oui/non)
    """

    messages: Annotated[list, add_messages]
    question: str
    original_question: str
    documents: list[Document]
    generation: str
    datasource: str
    rewrites: int
    retrieval_grade: str
