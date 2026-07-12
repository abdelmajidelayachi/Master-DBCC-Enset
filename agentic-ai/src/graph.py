"""Architecture du graphe agentic RAG (LangGraph)."""

from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import END, START, StateGraph

from src.nodes import (
    calculate,
    check_groundedness,
    decide_after_grading,
    decide_route,
    direct_answer,
    generate,
    grade_documents,
    retrieve,
    rewrite_query,
    route_question,
    web_search,
)
from src.state import AgentState


def build_graph(with_memory: bool = True):
    """Construit et compile le graphe agentic RAG.

    Flux :
        START -> route_question
            'vectorstore' -> retrieve -> grade_documents
                pertinent      -> generate
                non pertinent  -> rewrite_query -> retrieve (boucle)
                epuise         -> web_search -> generate
            'web'    -> web_search -> generate
            'calcul' -> calculate -> END
            'direct' -> direct_answer -> END
        generate -> verification d'ancrage (anti-hallucination)
            fonde     -> END
            non fonde -> rewrite_query (nouvelle boucle de retrieval)
    """
    workflow = StateGraph(AgentState)

    workflow.add_node("route_question", route_question)
    workflow.add_node("retrieve", retrieve)
    workflow.add_node("grade_documents", grade_documents)
    workflow.add_node("rewrite_query", rewrite_query)
    workflow.add_node("web_search", web_search)
    workflow.add_node("generate", generate)
    workflow.add_node("direct_answer", direct_answer)
    workflow.add_node("calculate", calculate)

    workflow.add_edge(START, "route_question")
    workflow.add_conditional_edges(
        "route_question",
        decide_route,
        {
            "vectorstore": "retrieve",
            "web": "web_search",
            "calcul": "calculate",
            "direct": "direct_answer",
        },
    )
    workflow.add_edge("retrieve", "grade_documents")
    workflow.add_conditional_edges(
        "grade_documents",
        decide_after_grading,
        {
            "generate": "generate",
            "rewrite_query": "rewrite_query",
            "web_search": "web_search",
        },
    )
    workflow.add_edge("rewrite_query", "retrieve")
    workflow.add_edge("web_search", "generate")
    workflow.add_conditional_edges(
        "generate",
        check_groundedness,
        {"end": END, "rewrite_query": "rewrite_query"},
    )
    workflow.add_edge("direct_answer", END)
    workflow.add_edge("calculate", END)

    # Memoire : le checkpointer persiste l'etat (dont les messages) par thread_id
    checkpointer = MemorySaver() if with_memory else None
    return workflow.compile(checkpointer=checkpointer)


def ask(graph, question: str, thread_id: str = "session-1") -> dict:
    """Pose une question au graphe dans un fil de conversation donne."""
    from langchain_core.messages import HumanMessage

    config = {"configurable": {"thread_id": thread_id}, "recursion_limit": 40}
    return graph.invoke(
        {"question": question, "messages": [HumanMessage(content=question)]},
        config=config,
    )
