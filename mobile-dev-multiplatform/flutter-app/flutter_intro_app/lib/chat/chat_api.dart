import 'dart:convert';

import 'package:http/http.dart' as http;

import '../config.dart';
import 'models/contact.dart';
import 'models/message.dart';

/// Accès au backend json-server (`json-server --watch db.json`).
class ChatApi {
  final String baseUrl;

  ChatApi({String? baseUrl}) : baseUrl = baseUrl ?? jsonServerBaseUrl;

  Future<bool> login(String username, String password) async {
    final resp = await http.get(Uri.parse(
        '$baseUrl/users?username=$username&password=$password'));
    if (resp.statusCode != 200) return false;
    return (json.decode(resp.body) as List).isNotEmpty;
  }

  Future<List<Contact>> getContacts({String? type}) async {
    final query = type == null ? '' : '?type=$type';
    final resp = await http.get(Uri.parse('$baseUrl/contacts$query'));
    if (resp.statusCode != 200) {
      throw Exception('HTTP ${resp.statusCode}');
    }
    return (json.decode(resp.body) as List)
        .map((e) => Contact.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<Message>> getMessages(int contactID) async {
    final resp =
        await http.get(Uri.parse('$baseUrl/messages?contactID=$contactID'));
    if (resp.statusCode != 200) {
      throw Exception('HTTP ${resp.statusCode}');
    }
    return (json.decode(resp.body) as List)
        .map((e) => Message.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> sendMessage(Message message) async {
    final resp = await http.post(
      Uri.parse('$baseUrl/messages'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(message.toJson()),
    );
    if (resp.statusCode != 201) {
      throw Exception('HTTP ${resp.statusCode}');
    }
  }
}
