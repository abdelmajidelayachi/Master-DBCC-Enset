import 'dart:typed_data';

import 'package:flutter/foundation.dart' show kIsWeb, defaultTargetPlatform;
import 'package:flutter/material.dart';
import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:image_picker/image_picker.dart';

/// Page Camera du support : choisir une image (galerie ou caméra) puis
/// reconnaître le texte qu'elle contient.
///
/// Améliorations par rapport au support :
///  - `firebase_ml_vision` (abandonné) remplacé par
///    `google_mlkit_text_recognition` : aucun projet Firebase requis ;
///  - affichage via `Image.memory`, compatible toutes plateformes.
class CameraPage extends StatefulWidget {
  const CameraPage({super.key});

  @override
  State<CameraPage> createState() => _CameraPageState();
}

class _CameraPageState extends State<CameraPage> {
  Uint8List? imageBytes;
  String? ocrText;

  bool get _ocrSupported =>
      !kIsWeb &&
      (defaultTargetPlatform == TargetPlatform.android ||
          defaultTargetPlatform == TargetPlatform.iOS);

  Future<String?> textRecognition(String imagePath) async {
    if (!_ocrSupported) {
      return "(OCR disponible uniquement sur Android/iOS)";
    }
    final inputImage = InputImage.fromFilePath(imagePath);
    final textRecognizer = TextRecognizer();
    try {
      final visionText = await textRecognizer.processImage(inputImage);
      return visionText.text;
    } finally {
      textRecognizer.close();
    }
  }

  Future<void> _pick(ImageSource source) async {
    Navigator.of(context).pop(); // ferme le dialogue
    final file = await ImagePicker().pickImage(
      source: source,
      maxWidth: 1200,
      maxHeight: 1200,
    );
    if (file == null) return;
    final bytes = await file.readAsBytes();
    final text = await textRecognition(file.path);
    if (!mounted) return;
    setState(() {
      imageBytes = bytes;
      ocrText = text;
    });
  }

  Future<void> openDialog(BuildContext context) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Make a Choice'),
        actions: [
          TextButton(
            child: const Text('Gallery'),
            onPressed: () => _pick(ImageSource.gallery),
          ),
          TextButton(
            child: const Text('Camera'),
            onPressed: () => _pick(ImageSource.camera),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Camera'),
        backgroundColor: Colors.deepOrange,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(10),
              child: MaterialButton(
                color: Colors.blue,
                onPressed: () => openDialog(context),
                child: const Text(
                  'Pick Image',
                  style: TextStyle(color: Colors.white, fontSize: 22),
                ),
              ),
            ),
            Container(
              height: 200,
              width: double.infinity,
              padding: const EdgeInsets.all(8),
              child: SingleChildScrollView(
                child: Text(ocrText ?? ''),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8),
              child: Container(
                width: double.infinity,
                height: 400,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.deepOrangeAccent),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: imageBytes != null
                    ? Image.memory(imageBytes!, fit: BoxFit.contain)
                    : const Center(
                        child: Icon(
                          Icons.image,
                          size: 120,
                          color: Colors.deepOrangeAccent,
                        ),
                      ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
