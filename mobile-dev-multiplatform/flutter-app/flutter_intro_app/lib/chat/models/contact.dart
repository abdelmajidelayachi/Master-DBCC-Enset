/// Modèle `contacts.model.dart` du support, en Dart null-safe.
class Contact {
  final int id;
  final String name;
  final String profile;
  final String type;
  final int score;

  Contact({
    required this.id,
    required this.name,
    required this.profile,
    required this.type,
    required this.score,
  });

  Contact.fromJson(Map<String, dynamic> json)
      : id = json['id'] as int,
        name = json['name'] as String,
        profile = json['profile'] as String,
        type = json['type'] as String,
        score = json['score'] as int;
}
