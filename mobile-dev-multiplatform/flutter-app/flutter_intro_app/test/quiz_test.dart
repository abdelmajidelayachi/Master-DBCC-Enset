import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_intro_app/quiz/quiz.dart';

void main() {
  testWidgets('le quiz affiche la première question et ses réponses',
      (tester) async {
    await tester.pumpWidget(const MaterialApp(home: QuizPage()));

    expect(find.text('Quiz App'), findsOneWidget);
    expect(find.text('1/2'), findsOneWidget);
    expect(find.textContaining('Q 1 -'), findsOneWidget);
    expect(find.byType(ElevatedButton), findsNWidgets(4));
  });

  testWidgets('répondre aux 2 questions affiche le score puis Restart',
      (tester) async {
    await tester.pumpWidget(const MaterialApp(home: QuizPage()));

    // Q1 : bonne réponse (C)
    await tester.tap(find.text('C - Both of the above.'));
    await tester.pump();
    expect(find.text('2/2'), findsOneWidget);

    // Q2 : mauvaise réponse (D)
    await tester.tap(find.text('D - None of the above.'));
    await tester.pump();
    expect(find.textContaining('Your score is 50.0 %'), findsOneWidget);

    // Restart repart à la question 1
    await tester.tap(find.text('Restart'));
    await tester.pump();
    expect(find.text('1/2'), findsOneWidget);
  });
}
