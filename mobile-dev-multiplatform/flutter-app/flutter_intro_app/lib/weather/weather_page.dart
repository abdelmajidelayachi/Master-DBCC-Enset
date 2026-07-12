import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:intl/intl.dart';

import '../config.dart';

/// Petite page d'accueil ajoutée pour choisir la ville
/// (le support codait la ville en dur : `Weather('Marrakech')`).
class WeatherHomePage extends StatefulWidget {
  const WeatherHomePage({super.key});

  @override
  State<WeatherHomePage> createState() => _WeatherHomePageState();
}

class _WeatherHomePageState extends State<WeatherHomePage> {
  final _controller = TextEditingController(text: 'Marrakech');

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _openForecast() {
    final city = _controller.text.trim();
    if (city.isEmpty) return;
    Navigator.of(context)
        .push(MaterialPageRoute(builder: (_) => Weather(city)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Weather'),
        backgroundColor: Colors.orange,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _controller,
              decoration: const InputDecoration(labelText: 'City'),
              onSubmitted: (_) => _openForecast(),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  foregroundColor: Colors.white,
                ),
                onPressed: _openForecast,
                child: const Text('Get Forecast'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Prévisions sur 5 jours (endpoint `forecast` d'OpenWeatherMap),
/// reproduction du `weather.dart` du support.
class Weather extends StatefulWidget {
  final String city;

  const Weather(this.city, {super.key});

  @override
  State<Weather> createState() => _WeatherState();
}

class _WeatherState extends State<Weather> {
  List<dynamic>? weatherData;
  String? error;

  Future<void> getData(String url) async {
    try {
      final resp = await http
          .get(Uri.parse(url), headers: {'accept': 'application/json'});
      final body = json.decode(resp.body) as Map<String, dynamic>;
      if (!mounted) return;
      setState(() {
        if (resp.statusCode == 200) {
          weatherData = body['list'] as List<dynamic>;
        } else {
          error = body['message']?.toString() ?? 'HTTP ${resp.statusCode}';
        }
      });
    } catch (err) {
      if (!mounted) return;
      setState(() => error = err.toString());
    }
  }

  @override
  void initState() {
    super.initState();
    final url = 'https://api.openweathermap.org/data/2.5/forecast'
        '?q=${Uri.encodeComponent(widget.city)}&units=metric&appid=$owmApiKey';
    getData(url);
  }

  @override
  Widget build(BuildContext context) {
    final data = weatherData;
    return Scaffold(
      appBar: AppBar(
        title: Text('City: ${widget.city}'),
        backgroundColor: Colors.orange,
      ),
      body: error != null
          ? Center(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Text(
                  'Erreur : $error\n\nVérifiez la ville ou la clé API '
                  '(--dart-define=OWM_API_KEY=...).',
                  textAlign: TextAlign.center,
                ),
              ),
            )
          : data == null
              ? const Center(child: CircularProgressIndicator())
              : ListView.builder(
                  itemCount: data.length,
                  itemBuilder: (context, index) {
                    final item = data[index] as Map<String, dynamic>;
                    final weather = item['weather'][0] as Map<String, dynamic>;
                    final date = DateTime.fromMillisecondsSinceEpoch(
                        (item['dt'] as num).toInt() * 1000);
                    final temp = (item['main']['temp'] as num).round();
                    return Card(
                      color: Colors.deepOrangeAccent,
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Row(
                              children: [
                                CircleAvatar(
                                  backgroundColor: Colors.white,
                                  backgroundImage: NetworkImage(
                                    'https://openweathermap.org/img/wn/'
                                    '${weather['icon']}@2x.png',
                                  ),
                                ),
                                Padding(
                                  padding: const EdgeInsets.only(left: 10),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        DateFormat('E dd/MM/yyyy').format(date),
                                        style: const TextStyle(
                                          fontSize: 16,
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                      Text(
                                        '${DateFormat('HH:mm').format(date)} | '
                                        '${weather['main']}',
                                        style: const TextStyle(
                                          fontSize: 20,
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                            Text(
                              '$temp °C',
                              style: const TextStyle(
                                fontSize: 20,
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
    );
  }
}
