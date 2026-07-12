import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';

/// Page QR Scan du support. Le package `barcode_scan` n'existe plus :
/// il est remplacé par `mobile_scanner` (Android, iOS, web, macOS).
class QRCodePage extends StatefulWidget {
  const QRCodePage({super.key});

  @override
  State<QRCodePage> createState() => _QRCodePageState();
}

class _QRCodePageState extends State<QRCodePage> {
  String? result;

  Future<void> scanQR() async {
    try {
      final scanned = await Navigator.of(context).push<String>(
        MaterialPageRoute(builder: (_) => const _ScannerScreen()),
      );
      if (scanned == null || !mounted) return;
      setState(() => result = scanned);
    } catch (e) {
      if (!mounted) return;
      setState(() => result = 'Error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('QR Code Scan')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Text(result ?? 'Scan RQ', style: const TextStyle(fontSize: 18)),
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        icon: const Icon(Icons.qr_code_scanner),
        onPressed: scanQR,
        label: const Text('QR Scan'),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
    );
  }
}

/// Écran caméra plein écran : retourne la première valeur détectée.
class _ScannerScreen extends StatefulWidget {
  const _ScannerScreen();

  @override
  State<_ScannerScreen> createState() => _ScannerScreenState();
}

class _ScannerScreenState extends State<_ScannerScreen> {
  final MobileScannerController _controller = MobileScannerController();
  bool _done = false;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('QR Code Scan'),
        actions: [
          IconButton(
            icon: const Icon(Icons.flash_on),
            onPressed: () => _controller.toggleTorch(),
          ),
        ],
      ),
      body: MobileScanner(
        controller: _controller,
        onDetect: (capture) {
          if (_done) return;
          final value = capture.barcodes.firstOrNull?.rawValue;
          if (value != null) {
            _done = true;
            Navigator.of(context).pop(value);
          }
        },
      ),
    );
  }
}
