"""Interface en ligne de commande : conversation avec l'agent RAG."""

import uuid

from src.graph import ask, build_graph


def main() -> None:
    graph = build_graph(with_memory=True)
    thread_id = str(uuid.uuid4())[:8]
    print("Agent RAG agentique (domaine : intelligence artificielle)")
    print(f"Fil de conversation : {thread_id} — tapez 'quit' pour sortir.\n")

    while True:
        try:
            question = input("Vous > ").strip()
        except (EOFError, KeyboardInterrupt):
            break
        if not question:
            continue
        if question.lower() in {"quit", "exit", "q"}:
            break

        result = ask(graph, question, thread_id=thread_id)
        sources = {
            d.metadata.get("source", "inconnue") for d in result.get("documents", [])
        }
        print(f"\nAgent > {result['generation']}")
        if sources:
            print(f"[Sources : {', '.join(sorted(sources))}]")
        print(f"[Route : {result.get('datasource')} | "
              f"Reformulations : {result.get('rewrites', 0)}]\n")


if __name__ == "__main__":
    main()
