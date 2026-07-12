import 'package:flutter/material.dart';

/// Écran de résultat (widget `Score` du support).
/// `FlatButton` (supprimé de Flutter) est remplacé par `TextButton`.
class Score extends StatelessWidget {
  final int score;
  final VoidCallback resetQuiz;
  final int numberOfQuestions;

  const Score(this.score, this.resetQuiz, this.numberOfQuestions, {super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      child: Column(
        children: [
          Text(
            'Results: Your score is ${score / numberOfQuestions * 100} %',
            style: const TextStyle(fontSize: 20),
          ),
          TextButton(
            onPressed: resetQuiz,
            child: const Text(
              'Restart',
              style: TextStyle(fontSize: 20, color: Colors.blue),
            ),
          ),
        ],
      ),
    );
  }
}
