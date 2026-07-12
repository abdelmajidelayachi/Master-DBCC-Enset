import 'package:flutter/material.dart';

/// Affiche le numéro et l'intitulé de la question courante
/// (widget `Question` du support).
class Question extends StatelessWidget {
  final String question;
  final int currentQuestionIndex;
  final int numberOfQuestions;

  const Question(
    this.question,
    this.currentQuestionIndex,
    this.numberOfQuestions, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          '${currentQuestionIndex + 1}/$numberOfQuestions',
          style: const TextStyle(fontSize: 22),
        ),
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(10),
          margin: const EdgeInsets.all(10),
          child: Text(
            question,
            style: const TextStyle(fontSize: 20),
            textAlign: TextAlign.center,
          ),
        ),
      ],
    );
  }
}
