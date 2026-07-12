"""Evaluation du systeme : qualite des reponses, temps, pertinence des documents.

Pour chaque question :
  - temps de reponse total du graphe
  - route choisie et nombre de reformulations
  - documents retenus apres grading (sources)
  - pertinence des documents (juge LLM, note 1-5)
  - qualite de la reponse (juge LLM, note 1-5 + justification)

Resultats ecrits dans evaluation/results.json et evaluation/results.md.
"""

import json
import statistics
import time
from pathlib import Path

from langchain_core.prompts import ChatPromptTemplate
from pydantic import BaseModel, Field

from evaluation.questions import QUESTIONS_COMPLEXES, QUESTIONS_SIMPLES
from src.graph import ask, build_graph
from src.llm import get_grader_llm

RESULTS_DIR = Path(__file__).resolve().parent
PAUSE_BETWEEN_QUESTIONS = 10  # secondes, pour respecter le quota gratuit Gemini


class JugeReponse(BaseModel):
    """Evaluation d'une reponse par un LLM juge."""

    note: int = Field(description="Note de 1 (mauvaise) a 5 (excellente).")
    justification: str = Field(description="Justification en une phrase.")


class JugePertinence(BaseModel):
    """Evaluation de la pertinence des documents recuperes."""

    note: int = Field(description="Note de 1 (hors sujet) a 5 (tres pertinents).")


def judge_answer(question: str, reponse: str) -> JugeReponse:
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Tu es un juge impartial. Evalue la qualite de la reponse : exactitude, "
         "completude, clarte. Note de 1 a 5."),
        ("human", "Question : {question}\n\nReponse : {reponse}"),
    ])
    juge = prompt | get_grader_llm().with_structured_output(JugeReponse)
    return juge.invoke({"question": question, "reponse": reponse})


def judge_relevance(question: str, documents: list) -> int:
    if not documents:
        return 0
    contenu = "\n\n".join(d.page_content[:500] for d in documents)
    prompt = ChatPromptTemplate.from_messages([
        ("system",
         "Evalue la pertinence globale de ces extraits documentaires par rapport "
         "a la question. Note de 1 a 5."),
        ("human", "Question : {question}\n\nExtraits :\n{contenu}"),
    ])
    juge = prompt | get_grader_llm().with_structured_output(JugePertinence)
    return juge.invoke({"question": question, "contenu": contenu}).note


def run_question(graph, question: str, categorie: str, index: int) -> dict:
    print(f"[{categorie} {index}] {question[:70]}...")
    start = time.perf_counter()
    try:
        result = ask(graph, question, thread_id=f"eval-{categorie}-{index}")
        elapsed = time.perf_counter() - start
    except Exception as exc:
        print(f"  ERREUR : {exc} — nouvelle tentative dans 60 s")
        time.sleep(60)
        start = time.perf_counter()
        result = ask(graph, question, thread_id=f"eval-{categorie}-{index}-retry")
        elapsed = time.perf_counter() - start

    documents = result.get("documents", [])
    juge = judge_answer(question, result["generation"])
    pertinence = judge_relevance(question, documents)

    record = {
        "categorie": categorie,
        "question": question,
        "reponse": result["generation"],
        "temps_s": round(elapsed, 2),
        "route": result.get("datasource", "?"),
        "reformulations": result.get("rewrites", 0),
        "nb_documents": len(documents),
        "sources": sorted({d.metadata.get("source", "?") for d in documents}),
        "pertinence_docs": pertinence,
        "note_reponse": juge.note,
        "justification": juge.justification,
    }
    print(f"  temps={record['temps_s']}s  note={juge.note}/5  "
          f"pertinence={pertinence}/5  route={record['route']}")
    return record


def summarize(records: list, categorie: str) -> dict:
    subset = [r for r in records if r["categorie"] == categorie]
    notes = [r["note_reponse"] for r in subset]
    temps = [r["temps_s"] for r in subset]
    pert = [r["pertinence_docs"] for r in subset if r["pertinence_docs"] > 0]
    return {
        "categorie": categorie,
        "nb": len(subset),
        "note_moyenne": round(statistics.mean(notes), 2) if notes else 0,
        "temps_moyen_s": round(statistics.mean(temps), 2) if temps else 0,
        "temps_min_s": min(temps) if temps else 0,
        "temps_max_s": max(temps) if temps else 0,
        "pertinence_moyenne": round(statistics.mean(pert), 2) if pert else 0,
    }


def write_markdown(records: list, syntheses: list) -> None:
    lines = ["# Resultats de l'evaluation", ""]
    lines += ["## Synthese", "",
              "| Categorie | Questions | Note moyenne /5 | Pertinence docs /5 | "
              "Temps moyen (s) | Temps min-max (s) |",
              "|---|---|---|---|---|---|"]
    for s in syntheses:
        lines.append(
            f"| {s['categorie']} | {s['nb']} | {s['note_moyenne']} | "
            f"{s['pertinence_moyenne']} | {s['temps_moyen_s']} | "
            f"{s['temps_min_s']}–{s['temps_max_s']} |"
        )
    lines += ["", "## Detail par question", ""]
    for i, r in enumerate(records, 1):
        lines += [
            f"### Q{i} ({r['categorie']}) — {r['question']}",
            "",
            f"- **Route** : {r['route']} | **Reformulations** : {r['reformulations']}"
            f" | **Temps** : {r['temps_s']} s",
            f"- **Documents retenus** : {r['nb_documents']} "
            f"({', '.join(r['sources']) or 'aucun'})",
            f"- **Pertinence des documents** : {r['pertinence_docs']}/5",
            f"- **Note de la reponse** : {r['note_reponse']}/5 — {r['justification']}",
            "",
            f"> {r['reponse'][:600]}{'...' if len(r['reponse']) > 600 else ''}",
            "",
        ]
    (RESULTS_DIR / "results.md").write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    graph = build_graph(with_memory=True)
    records = []

    for i, q in enumerate(QUESTIONS_SIMPLES, 1):
        records.append(run_question(graph, q, "simple", i))
        time.sleep(PAUSE_BETWEEN_QUESTIONS)
    for i, q in enumerate(QUESTIONS_COMPLEXES, 1):
        records.append(run_question(graph, q, "complexe", i))
        time.sleep(PAUSE_BETWEEN_QUESTIONS)

    syntheses = [summarize(records, "simple"), summarize(records, "complexe")]

    (RESULTS_DIR / "results.json").write_text(
        json.dumps({"syntheses": syntheses, "details": records},
                   ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    write_markdown(records, syntheses)
    print("\nSynthese :")
    for s in syntheses:
        print(f"  {s['categorie']}: note={s['note_moyenne']}/5, "
              f"pertinence={s['pertinence_moyenne']}/5, "
              f"temps moyen={s['temps_moyen_s']}s")
    print(f"\nResultats ecrits dans {RESULTS_DIR / 'results.md'}")


if __name__ == "__main__":
    main()
