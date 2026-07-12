/// Clés et URLs des APIs utilisées dans le support de cours.
///
/// Elles peuvent être surchargées au lancement :
///   flutter run --dart-define=OWM_API_KEY=xxx --dart-define=PIXABAY_API_KEY=yyy
library;

import 'dart:io' show Platform;

import 'package:flutter/foundation.dart' show kIsWeb;

/// Clé OpenWeatherMap (celle du support est la clé de démonstration publique).
const owmApiKey = String.fromEnvironment(
  'OWM_API_KEY',
  defaultValue: 'b6907d289e10d714a6e88b30761fae22',
);

/// Clé Pixabay reprise du support de cours.
const pixabayApiKey = String.fromEnvironment(
  'PIXABAY_API_KEY',
  defaultValue: '5832566-81dc7429a63c86e3b707d0429',
);

/// Base URL du json-server (chat) : l'émulateur Android n'accède pas à
/// localhost directement, il passe par 10.0.2.2.
String get jsonServerBaseUrl {
  const override = String.fromEnvironment('JSON_SERVER_URL');
  if (override.isNotEmpty) return override;
  if (!kIsWeb && Platform.isAndroid) return 'http://10.0.2.2:3000';
  return 'http://localhost:3000';
}
