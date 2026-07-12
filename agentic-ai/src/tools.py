"""Outils mis a la disposition de l'agent."""

from langchain_core.documents import Document
from langchain_core.tools import tool

from src.ingestion import get_retriever


@tool
def rechercher_documents(question: str) -> list[Document]:
    """Recherche les passages les plus pertinents dans la base documentaire IA/ML."""
    retriever = get_retriever()
    return retriever.invoke(question)


@tool
def recherche_web(question: str) -> list[Document]:
    """Recherche sur le web (DuckDuckGo) quand la base documentaire ne suffit pas."""
    from ddgs import DDGS

    results = []
    try:
        with DDGS() as ddgs:
            for r in ddgs.text(question, max_results=3):
                results.append(
                    Document(
                        page_content=r.get("body", ""),
                        metadata={"source": r.get("href", "web"), "title": r.get("title", "")},
                    )
                )
    except Exception as exc:  # le web peut echouer sans bloquer l'agent
        results.append(
            Document(
                page_content=f"Recherche web indisponible : {exc}",
                metadata={"source": "web-error"},
            )
        )
    return results


@tool
def calculatrice(expression: str) -> str:
    """Evalue une expression arithmetique simple (ex: '3 * (2 + 5)')."""
    autorises = set("0123456789+-*/(). %")
    if not set(expression) <= autorises:
        return "Expression refusee : seuls les caracteres arithmetiques sont autorises."
    try:
        return str(eval(expression, {"__builtins__": {}}, {}))
    except Exception as exc:
        return f"Erreur de calcul : {exc}"
