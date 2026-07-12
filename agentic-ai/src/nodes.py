"""Noeuds du graphe agentic RAG."""

from langchain_core.messages import AIMessage
from langchain_core.prompts import ChatPromptTemplate
from pydantic import BaseModel, Field

from src.config import MAX_REWRITES, TOP_K
from src.ingestion import get_retriever
from src.llm import get_grader_llm, get_llm, text_of
from src.state import AgentState
from src.tools import calculatrice, recherche_web


# ---------------------------------------------------------------------------
# Schemas de sortie structuree
# ---------------------------------------------------------------------------

class RouteDecision(BaseModel):
    """Choix de la source de donnees pour repondre a la question."""

    datasource: str = Field(
        description="Une valeur parmi : 'vectorstore' (question sur l'IA, le ML, "
        "les LLM, le RAG, les agents...), 'web' (actualite ou information externe "
        "recente), 'calcul' (calcul arithmetique), 'direct' (salutation ou "
        "conversation generale sans besoin de documents)."
    )


class GradeDocument(BaseModel):
    """Verdict binaire de pertinence d'un document."""

    pertinent: str = Field(description="'oui' si le document aide a repondre, sinon 'non'.")


class GradeGroundedness(BaseModel):
    """Verdict binaire d'ancrage de la reponse dans les documents."""

    fonde: str = Field(
        description="'oui' si la reponse est appuyee par les documents, sinon 'non'."
    )


# ---------------------------------------------------------------------------
# Noeuds
# ---------------------------------------------------------------------------

def route_question(state: AgentState) -> dict:
    """Routeur : decide de la strategie a adopter pour la question."""
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Tu es un routeur pour un assistant specialise en intelligence artificielle. "
         "La base documentaire couvre : machine learning, deep learning, transformers, "
         "LLM, RAG, embeddings, bases vectorielles, agents IA, LangChain/LangGraph, "
         "evaluation des systemes RAG, ethique de l'IA. "
         "Choisis la source la plus adaptee a la question."),
        ("human", "{question}"),
    ])
    router = prompt | get_grader_llm().with_structured_output(RouteDecision)
    decision = router.invoke({"question": state["question"]})
    datasource = decision.datasource if decision.datasource in {
        "vectorstore", "web", "calcul", "direct"} else "vectorstore"
    return {
        "datasource": datasource,
        "original_question": state["question"],
        "rewrites": 0,
    }


def retrieve(state: AgentState) -> dict:
    """Recupere les TOP_K chunks les plus proches de la question."""
    documents = get_retriever().invoke(state["question"])
    return {"documents": documents}


def grade_documents(state: AgentState) -> dict:
    """Filtre les documents recuperes : seuls les pertinents sont conserves."""
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Evalue si le document suivant contient des informations utiles pour "
         "repondre a la question. Reponds 'oui' ou 'non'."),
        ("human", "Document :\n{document}\n\nQuestion : {question}"),
    ])
    grader = prompt | get_grader_llm().with_structured_output(GradeDocument)

    pertinents = []
    for doc in state["documents"]:
        verdict = grader.invoke(
            {"document": doc.page_content, "question": state["question"]}
        )
        if verdict.pertinent.strip().lower() == "oui":
            pertinents.append(doc)

    grade = "oui" if pertinents else "non"
    return {"documents": pertinents, "retrieval_grade": grade}


def rewrite_query(state: AgentState) -> dict:
    """Reformule la question pour ameliorer le retrieval."""
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Reformule la question pour maximiser les chances de retrouver les bons "
         "passages dans une base documentaire sur l'IA. Rends-la plus explicite et "
         "utilise le vocabulaire technique adapte. Reponds uniquement par la "
         "question reformulee."),
        ("human", "Question initiale : {original}\nQuestion actuelle : {question}"),
    ])
    rewriter = prompt | get_llm(temperature=0.3)
    nouvelle = rewriter.invoke({
        "original": state["original_question"],
        "question": state["question"],
    })
    return {"question": text_of(nouvelle).strip(), "rewrites": state["rewrites"] + 1}


def web_search(state: AgentState) -> dict:
    """Fallback : recherche web quand la base documentaire ne suffit pas."""
    documents = recherche_web.invoke({"question": state["question"]})
    return {"documents": documents}


def calculate(state: AgentState) -> dict:
    """Extrait l'expression arithmetique puis utilise l'outil calculatrice."""
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Extrais de la question l'expression arithmetique a calculer. Reponds "
         "uniquement par l'expression (chiffres et operateurs + - * / ( ) . %)."),
        ("human", "{question}"),
    ])
    extracted = (prompt | get_grader_llm()).invoke({"question": state["question"]})
    expression = text_of(extracted).strip()
    result = calculatrice.invoke({"expression": expression})
    generation = f"Le resultat de {expression} est {result}."
    return {"generation": generation, "messages": [AIMessage(content=generation)]}


def generate(state: AgentState) -> dict:
    """Genere la reponse finale a partir des documents et de l'historique."""
    contexte = "\n\n---\n\n".join(
        f"[Source : {doc.metadata.get('source', 'inconnue')}]\n{doc.page_content}"
        for doc in state["documents"]
    )
    # Memoire court terme : on injecte les derniers echanges de la conversation
    historique = "\n".join(
        f"{m.type}: {m.content}" for m in state["messages"][-6:-1]
    )
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Tu es un assistant pedagogique specialise en intelligence artificielle. "
         "Reponds a la question en francais en t'appuyant STRICTEMENT sur les "
         "documents fournis. Si l'information n'y figure pas, dis-le honnetement. "
         "Cite les sources utilisees a la fin de la reponse.\n\n"
         "Historique de conversation :\n{historique}\n\n"
         "Documents :\n{contexte}"),
        ("human", "{question}"),
    ])
    reponse = (prompt | get_llm(temperature=0.2)).invoke({
        "historique": historique or "(debut de conversation)",
        "contexte": contexte or "(aucun document)",
        "question": state["original_question"],
    })
    texte = text_of(reponse)
    return {
        "generation": texte,
        "messages": [AIMessage(content=texte)],
    }


def direct_answer(state: AgentState) -> dict:
    """Repond directement (salutations, conversation generale), avec memoire."""
    historique = "\n".join(
        f"{m.type}: {m.content}" for m in state["messages"][-6:-1]
    )
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Tu es un assistant specialise en intelligence artificielle. Reponds "
         "brievement et cordialement en francais.\n\n"
         "Historique de conversation :\n{historique}"),
        ("human", "{question}"),
    ])
    reponse = (prompt | get_llm(temperature=0.5)).invoke({
        "historique": historique or "(debut de conversation)",
        "question": state["question"],
    })
    texte = text_of(reponse)
    return {
        "generation": texte,
        "documents": [],
        "messages": [AIMessage(content=texte)],
    }


# ---------------------------------------------------------------------------
# Arêtes conditionnelles
# ---------------------------------------------------------------------------

def decide_route(state: AgentState) -> str:
    """Oriente vers le bon noeud apres le routeur."""
    return state["datasource"]


def decide_after_grading(state: AgentState) -> str:
    """Apres l'evaluation des documents : generer, reformuler ou passer au web."""
    if state["retrieval_grade"] == "oui":
        return "generate"
    if state["rewrites"] < MAX_REWRITES:
        return "rewrite_query"
    return "web_search"


def check_groundedness(state: AgentState) -> str:
    """Verifie que la reponse est ancree dans les documents (anti-hallucination)."""
    if not state["documents"]:
        return "end"
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Verifie si la reponse est appuyee par les documents fournis. "
         "Reponds 'oui' ou 'non'."),
        ("human", "Documents :\n{documents}\n\nReponse :\n{generation}"),
    ])
    grader = prompt | get_grader_llm().with_structured_output(GradeGroundedness)
    verdict = grader.invoke({
        "documents": "\n\n".join(d.page_content for d in state["documents"]),
        "generation": state["generation"],
    })
    if verdict.fonde.strip().lower() == "oui" or state["rewrites"] >= MAX_REWRITES:
        return "end"
    return "rewrite_query"
