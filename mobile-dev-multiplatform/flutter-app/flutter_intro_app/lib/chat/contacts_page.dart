import 'package:flutter/material.dart';

import 'chat_api.dart';
import 'messages_page.dart';
import 'models/contact.dart';

/// Liste des contacts avec filtres par type (tous / Professor / Student /
/// Developer), comme la barre d'icônes du support.
class ContactsPage extends StatefulWidget {
  const ContactsPage({super.key});

  @override
  State<ContactsPage> createState() => _ContactsPageState();
}

class _ContactsPageState extends State<ContactsPage> {
  final _api = ChatApi();
  List<Contact>? _contacts;
  String? _error;
  String? _filter; // null = tous

  static const _filters = <(String?, IconData, String)>[
    (null, Icons.list, 'Tous'),
    ('Professor', Icons.school, 'Professors'),
    ('Developer', Icons.build, 'Developers'),
    ('Student', Icons.computer, 'Students'),
  ];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _contacts = null;
      _error = null;
    });
    try {
      final contacts = await _api.getContacts(type: _filter);
      if (!mounted) return;
      setState(() => _contacts = contacts);
    } catch (e) {
      if (!mounted) return;
      setState(() => _error = e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    final contacts = _contacts;
    return Scaffold(
      appBar: AppBar(title: const Text('Contacts')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8),
            child: Row(
              children: [
                for (final (type, icon, tooltip) in _filters)
                  Padding(
                    padding: const EdgeInsets.only(right: 8),
                    child: IconButton(
                      tooltip: tooltip,
                      icon: Icon(icon),
                      style: IconButton.styleFrom(
                        backgroundColor: _filter == type
                            ? Colors.deepOrange
                            : Colors.grey.shade200,
                        foregroundColor:
                            _filter == type ? Colors.white : Colors.black87,
                      ),
                      onPressed: () {
                        setState(() => _filter = type);
                        _load();
                      },
                    ),
                  ),
              ],
            ),
          ),
          Expanded(
            child: _error != null
                ? Center(child: Text('Erreur : $_error'))
                : contacts == null
                    ? const Center(child: CircularProgressIndicator())
                    : RefreshIndicator(
                        onRefresh: _load,
                        child: ListView.builder(
                          itemCount: contacts.length,
                          itemBuilder: (context, index) {
                            final contact = contacts[index];
                            return ListTile(
                              leading: CircleAvatar(
                                backgroundImage:
                                    NetworkImage(contact.profile),
                              ),
                              title: Text(contact.name),
                              subtitle: Text(contact.type),
                              trailing: CircleAvatar(
                                radius: 16,
                                backgroundColor: Colors.deepOrange,
                                child: Text(
                                  '${contact.score}',
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontSize: 10,
                                  ),
                                ),
                              ),
                              onTap: () => Navigator.of(context).push(
                                MaterialPageRoute(
                                  builder: (_) => MessagesPage(contact),
                                ),
                              ),
                            );
                          },
                        ),
                      ),
          ),
        ],
      ),
    );
  }
}
