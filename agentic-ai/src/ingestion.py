"""Construction de la base documentaire : chargement, decoupage, indexation."""

import shutil
from pathlib import Path

from langchain_chroma import Chroma
from langchain_community.document_loaders import DirectoryLoader, TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

from src.config import CHROMA_DIR, CHUNK_OVERLAP, CHUNK_SIZE, DOCUMENTS_DIR, TOP_K
from src.llm import get_embeddings

COLLECTION_NAME = "base_ia"


def build_vectorstore() -> Chroma:
    """Charge les documents, les decoupe en chunks et les indexe dans Chroma."""
    loader = DirectoryLoader(
        str(DOCUMENTS_DIR),
        glob="*.md",
        loader_cls=TextLoader,
        loader_kwargs={"encoding": "utf-8"},
    )
    documents = loader.load()
    for doc in documents:
        # Source lisible : nom du fichier plutot que chemin absolu
        doc.metadata["source"] = Path(doc.metadata["source"]).name
    print(f"{len(documents)} documents charges depuis {DOCUMENTS_DIR}")

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=CHUNK_SIZE,
        chunk_overlap=CHUNK_OVERLAP,
        separators=["\n## ", "\n\n", "\n", " ", ""],
    )
    chunks = splitter.split_documents(documents)
    print(f"{len(chunks)} chunks generes (taille={CHUNK_SIZE}, overlap={CHUNK_OVERLAP})")

    # On repart d'une base propre a chaque ingestion
    if CHROMA_DIR.exists():
        shutil.rmtree(CHROMA_DIR)

    vectorstore = Chroma.from_documents(
        documents=chunks,
        embedding=get_embeddings(),
        collection_name=COLLECTION_NAME,
        persist_directory=str(CHROMA_DIR),
    )
    print(f"Base vectorielle persistee dans {CHROMA_DIR}")
    return vectorstore


def load_vectorstore() -> Chroma:
    """Recharge la base vectorielle persistee."""
    if not CHROMA_DIR.exists():
        raise FileNotFoundError(
            "Base vectorielle introuvable. Lancez d'abord : python -m src.ingestion"
        )
    return Chroma(
        collection_name=COLLECTION_NAME,
        embedding_function=get_embeddings(),
        persist_directory=str(CHROMA_DIR),
    )


def get_retriever():
    """Retriever par similarite cosinus sur la base persistee."""
    return load_vectorstore().as_retriever(search_kwargs={"k": TOP_K})


if __name__ == "__main__":
    build_vectorstore()
