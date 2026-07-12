import 'package:flutter/material.dart';

import '../camera/camera_page.dart';
import '../chat/login_page.dart';
import '../gallery/gallery_page.dart';
import '../qrscan/qr_scan_page.dart';
import '../quiz/quiz.dart';
import '../weather/weather_page.dart';

/// Page d'accueil : reproduit le drawer du support (Quiz, Weather, Gallery,
/// Camera, QR Scan) et ajoute l'application Chat (json-server).
class HomePage extends StatelessWidget {
  const HomePage({super.key});

  void _open(BuildContext context, Widget page) {
    Navigator.of(context).pop(); // ferme le drawer
    Navigator.of(context).push(MaterialPageRoute(builder: (_) => page));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Introduction à Flutter')),
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            const DrawerHeader(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [Colors.deepOrange, Colors.orangeAccent],
                ),
              ),
              child: Center(
                child: CircleAvatar(
                  radius: 40,
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, size: 48, color: Colors.deepOrange),
                ),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.quiz),
              title: const Text('Quiz'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const QuizPage()),
            ),
            ListTile(
              leading: const Icon(Icons.wb_sunny),
              title: const Text('Weather'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const WeatherHomePage()),
            ),
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Gallery'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const GalleryPage()),
            ),
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text('Camera (OCR)'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const CameraPage()),
            ),
            ListTile(
              leading: const Icon(Icons.qr_code_scanner),
              title: const Text('QR Scan'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const QRCodePage()),
            ),
            const Divider(),
            ListTile(
              leading: const Icon(Icons.chat),
              title: const Text('Chat (json-server)'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _open(context, const LoginPage()),
            ),
          ],
        ),
      ),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text(
            'Ouvrez le menu pour naviguer entre les applications du support :\n\n'
            'Quiz • Weather • Gallery • Camera (OCR) • QR Scan • Chat',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 18),
          ),
        ),
      ),
    );
  }
}
