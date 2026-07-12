/// Modèle `messages.model.dart` du support, en Dart null-safe.
class Message {
  final int? id;
  final int contactID;
  final DateTime dateTime;
  final String type; // 'sent' | 'received'
  final String message;

  Message({
    this.id,
    required this.contactID,
    required this.dateTime,
    required this.type,
    required this.message,
  });

  Message.fromJson(Map<String, dynamic> json)
      : id = json['id'] as int?,
        contactID = json['contactID'] as int,
        dateTime = DateTime.parse(json['date'] as String),
        type = json['type'] as String,
        message = json['message'] as String;

  Map<String, dynamic> toJson() => {
        'contactID': contactID,
        'message': message,
        'type': type,
        'date': dateTime.toString(),
      };
}
