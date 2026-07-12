import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

import '../config.dart';

/// Recherche d'images Pixabay (page `gallery.dart` du support).
class GalleryPage extends StatefulWidget {
  const GalleryPage({super.key});

  @override
  State<GalleryPage> createState() => _GalleryPageState();
}

class _GalleryPageState extends State<GalleryPage> {
  String keyword = '';

  void _openResults(String value) {
    final query = value.trim();
    if (query.isEmpty) return;
    Navigator.of(context)
        .push(MaterialPageRoute(builder: (_) => GalleryData(query)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.deepOrange,
        title: Text(keyword.isEmpty ? 'Gallery' : keyword),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8),
        child: Column(
          children: [
            TextField(
              decoration: const InputDecoration(hintText: 'Key word'),
              onChanged: (value) => setState(() => keyword = value),
              onSubmitted: _openResults,
            ),
            const SizedBox(height: 8),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepOrange,
                  foregroundColor: Colors.white,
                ),
                onPressed: () => _openResults(keyword),
                child: const Text('Get Data'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Liste paginée des résultats avec scroll infini
/// (classe `GalleryData` du support).
class GalleryData extends StatefulWidget {
  final String keyWord;

  const GalleryData(this.keyWord, {super.key});

  @override
  State<GalleryData> createState() => _GalleryDataState();
}

class _GalleryDataState extends State<GalleryData> {
  int currentPage = 1;
  final int pageSize = 10;
  int totalPages = 0;
  final ScrollController _scrollController = ScrollController();
  Map<String, dynamic>? dataGallery;
  final List<dynamic> hits = [];
  String? error;

  Future<void> getData(String url) async {
    try {
      final resp = await http.get(Uri.parse(url));
      if (!mounted) return;
      if (resp.statusCode != 200) {
        setState(() => error = 'HTTP ${resp.statusCode}: ${resp.body}');
        return;
      }
      setState(() {
        dataGallery = json.decode(resp.body) as Map<String, dynamic>;
        hits.addAll(dataGallery!['hits'] as List<dynamic>);
        final totalHits = dataGallery!['totalHits'] as int;
        totalPages = (totalHits / pageSize).ceil();
      });
    } catch (err) {
      if (!mounted) return;
      setState(() => error = err.toString());
    }
  }

  void loadData() {
    final url = 'https://pixabay.com/api/?key=$pixabayApiKey'
        '&q=${Uri.encodeComponent(widget.keyWord)}'
        '&page=$currentPage&per_page=$pageSize';
    getData(url);
  }

  @override
  void initState() {
    super.initState();
    loadData();
    _scrollController.addListener(() {
      if (_scrollController.position.pixels ==
          _scrollController.position.maxScrollExtent) {
        if (currentPage < totalPages) {
          ++currentPage;
          loadData();
        }
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${widget.keyWord} : $currentPage / $totalPages'),
        backgroundColor: Colors.deepOrange,
      ),
      body: error != null
          ? Center(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Text('Erreur : $error', textAlign: TextAlign.center),
              ),
            )
          : dataGallery == null
              ? const Center(child: CircularProgressIndicator())
              : ListView.builder(
                  controller: _scrollController,
                  itemCount: hits.length,
                  itemBuilder: (context, index) {
                    final hit = hits[index] as Map<String, dynamic>;
                    return Column(
                      children: [
                        Container(
                          width: double.infinity,
                          padding:
                              const EdgeInsets.symmetric(horizontal: 5),
                          child: Card(
                            color: Colors.deepOrange,
                            child: Padding(
                              padding: const EdgeInsets.all(8),
                              child: Text(
                                hit['tags'] as String,
                                style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                        Container(
                          width: double.infinity,
                          padding:
                              const EdgeInsets.symmetric(horizontal: 5),
                          child: Card(
                            child: Image.network(
                              // webformatURL est plus net que previewURL
                              (hit['webformatURL'] ?? hit['previewURL'])
                                  as String,
                              fit: BoxFit.fitWidth,
                            ),
                          ),
                        ),
                        const Divider(color: Colors.grey, thickness: 2),
                      ],
                    );
                  },
                ),
    );
  }
}
