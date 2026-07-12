"""Visualisation du graphe : export Mermaid + PNG."""

from pathlib import Path

from src.config import ROOT_DIR
from src.graph import build_graph


def main() -> None:
    graph = build_graph(with_memory=False).get_graph()

    mermaid = graph.draw_mermaid()
    mermaid_path = ROOT_DIR / "graph.mmd"
    mermaid_path.write_text(mermaid, encoding="utf-8")
    print(f"Diagramme Mermaid ecrit dans {mermaid_path}")

    try:
        png_bytes = graph.draw_mermaid_png()
        png_path = ROOT_DIR / "graph.png"
        Path(png_path).write_bytes(png_bytes)
        print(f"Image PNG ecrite dans {png_path}")
    except Exception as exc:
        print(f"PNG non genere ({exc}) — le fichier graph.mmd reste utilisable "
              "sur https://mermaid.live")


if __name__ == "__main__":
    main()
