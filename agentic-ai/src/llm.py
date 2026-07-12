"""Modeles LLM et embeddings (Google Gemini)."""

from langchain_core.rate_limiters import InMemoryRateLimiter
from langchain_google_genai import ChatGoogleGenerativeAI, GoogleGenerativeAIEmbeddings

from src.config import EMBEDDING_MODEL, LLM_MODEL

# Limiteur partage par tous les appels LLM : respecte le quota gratuit Gemini
_rate_limiter = InMemoryRateLimiter(requests_per_second=0.3, max_bucket_size=3)


def get_llm(temperature: float = 0.0) -> ChatGoogleGenerativeAI:
    """LLM principal pour la generation des reponses."""
    return ChatGoogleGenerativeAI(
        model=LLM_MODEL, temperature=temperature, rate_limiter=_rate_limiter
    )


def get_grader_llm() -> ChatGoogleGenerativeAI:
    """LLM deterministe pour le routage et l'evaluation des documents."""
    return ChatGoogleGenerativeAI(
        model=LLM_MODEL, temperature=0.0, rate_limiter=_rate_limiter
    )


def get_embeddings() -> GoogleGenerativeAIEmbeddings:
    """Modele d'embeddings pour la base vectorielle."""
    return GoogleGenerativeAIEmbeddings(model=EMBEDDING_MODEL)


def text_of(message) -> str:
    """Extrait le texte d'une reponse LLM.

    Les modeles avec raisonnement (thinking) renvoient une liste de blocs de
    contenu au lieu d'une simple chaine ; on ne garde que les blocs de texte.
    """
    content = message.content if hasattr(message, "content") else message
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        parts = []
        for block in content:
            if isinstance(block, str):
                parts.append(block)
            elif isinstance(block, dict) and block.get("type") == "text":
                parts.append(block.get("text", ""))
        return "\n".join(parts)
    return str(content)
