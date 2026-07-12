"""Configuration centrale du projet."""

import os
from pathlib import Path

from dotenv import load_dotenv

load_dotenv()

ROOT_DIR = Path(__file__).resolve().parent.parent
DOCUMENTS_DIR = ROOT_DIR / "data" / "documents"
CHROMA_DIR = ROOT_DIR / "chroma_db"

GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY", "")
LLM_MODEL = os.getenv("LLM_MODEL", "gemini-3.1-flash-lite")
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "models/gemini-embedding-001")

# Parametres du decoupage des documents
CHUNK_SIZE = 800
CHUNK_OVERLAP = 120

# Parametres du retrieval
TOP_K = 4

# Nombre maximum de reformulations de la question avant fallback web
MAX_REWRITES = 2
