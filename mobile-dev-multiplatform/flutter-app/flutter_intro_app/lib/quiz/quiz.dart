import 'package:flutter/material.dart';

import 'answer.dart';
import 'question.dart';
import 'score.dart';

/// Page hôte du quiz : reproduit le `main.dart` de l'application Quiz du
/// support (Scaffold + AppBar orange) mais s'intègre au drawer de l'app.
class QuizPage extends StatelessWidget {
  const QuizPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: const Text('Quiz App'),
        backgroundColor: Colors.orangeAccent,
      ),
      body: const Center(child: Quiz()),
    );
  }
}

class Quiz extends StatefulWidget {
  const Quiz({super.key});

  @override
  State<Quiz> createState() => QuizState();
}

class QuizState extends State<Quiz> {
  int currentQuestionIndex = 0;
  int score = 0;

  final quiz = <Map<String, Object>>[
    {
      'question': 'Q 1 - Which of the following is correct about Java 8 '
          'lambda expression ?',
      'answers': [
        {
          'answer': 'A - Using lambda expression, you can refer to final '
              'variable or effectively final variable (which is assigned '
              'only once)',
          'correct': false,
        },
        {
          'answer': 'B - Lambda expression throws a compilation error, if a '
              'variable is assigned a value the second time ?',
          'correct': false,
        },
        {'answer': 'C - Both of the above.', 'correct': true},
        {'answer': 'D - None of the above.', 'correct': false},
      ],
    },
    {
      'question': 'Q 8 - Which of the following is the correct lambda '
          'expression which add two numbers and return their sum?',
      'answers': [
        {'answer': 'A - (int a, int b) -> { return a + b;};', 'correct': false},
        {'answer': 'B - (a, b) -> {return a + b;};', 'correct': false},
        {'answer': 'C - Both of the above.', 'correct': true},
        {'answer': 'D - None of the above.', 'correct': false},
      ],
    },
  ];

  void handleAnswer(Map<String, Object> answer) {
    setState(() {
      ++currentQuestionIndex;
      if (answer['correct'] == true) ++score;
    });
  }

  void resetQuiz() {
    setState(() {
      currentQuestionIndex = 0;
      score = 0;
    });
  }

  @override
  Widget build(BuildContext context) {
    return (currentQuestionIndex >= quiz.length)
        ? Score(score, resetQuiz, quiz.length)
        : SingleChildScrollView(
            padding: const EdgeInsets.all(10),
            child: Column(
              children: [
                Question(
                  quiz[currentQuestionIndex]['question'] as String,
                  currentQuestionIndex,
                  quiz.length,
                ),
                ...(quiz[currentQuestionIndex]['answers']
                        as List<Map<String, Object>>)
                    .map((answer) => Answer(answer, handleAnswer)),
              ],
            ),
          );
  }
}
