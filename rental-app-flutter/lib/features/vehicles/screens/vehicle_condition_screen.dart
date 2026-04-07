import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import '../../../core/api/api_client.dart';

class VehicleConditionScreen extends ConsumerStatefulWidget {
  final String vehicleId;
  const VehicleConditionScreen({super.key, required this.vehicleId});

  @override
  ConsumerState<VehicleConditionScreen> createState() => _VehicleConditionScreenState();
}

class _VehicleConditionScreenState extends ConsumerState<VehicleConditionScreen> {
  final _notesController = TextEditingController();
  final _mileageController = TextEditingController();
  final _fuelController = TextEditingController();
  final List<XFile> _photos = [];
  bool _loading = true;
  bool _submitting = false;
  List<dynamic> _reports = [];
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  @override
  void dispose() {
    _notesController.dispose();
    _mileageController.dispose();
    _fuelController.dispose();
    super.dispose();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final res = await ApiClient().dio.get('/vehicles/${widget.vehicleId}/condition');
      setState(() {
        _reports = res.data['data'] ?? [];
        _loading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load reports';
        _loading = false;
      });
    }
  }

  Future<void> _pickPhotos() async {
    final picker = ImagePicker();
    final picked = await picker.pickMultiImage();
    if (picked.isNotEmpty) {
      setState(() => _photos.addAll(picked));
    }
  }

  Future<void> _submit() async {
    if (_notesController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Notes are required')),
      );
      return;
    }
    setState(() => _submitting = true);
    try {
      final formData = FormData.fromMap({
        'notes': _notesController.text.trim(),
        if (_mileageController.text.isNotEmpty) 'mileageAt': _mileageController.text,
        if (_fuelController.text.isNotEmpty) 'fuelLevel': _fuelController.text,
        if (_photos.isNotEmpty)
          'photos': await Future.wait(
              _photos.map((p) => MultipartFile.fromFile(p.path))),
      });
      await ApiClient().dio.post(
            '/vehicles/${widget.vehicleId}/condition',
            data: formData,
            options: Options(contentType: 'multipart/form-data'),
          );
      _notesController.clear();
      _mileageController.clear();
      _fuelController.clear();
      setState(() {
        _photos.clear();
        _submitting = false;
      });
      _load();
    } catch (e) {
      setState(() => _submitting = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to submit report')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Condition Reports')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Text('New Report', style: Theme.of(context).textTheme.titleLarge),
                const SizedBox(height: 8),
                TextField(
                  controller: _notesController,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    labelText: 'Notes *',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 8),
                TextField(
                  controller: _mileageController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    labelText: 'Mileage (km)',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 8),
                TextField(
                  controller: _fuelController,
                  decoration: const InputDecoration(
                    labelText: 'Fuel Level (e.g. 3/4, FULL)',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 8),
                OutlinedButton.icon(
                  onPressed: _pickPhotos,
                  icon: const Icon(Icons.add_a_photo),
                  label: Text('Add Photos (${_photos.length})'),
                ),
                const SizedBox(height: 8),
                FilledButton(
                  onPressed: _submitting ? null : _submit,
                  child: _submitting
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2))
                      : const Text('Submit Report'),
                ),
                const Divider(height: 32),
                Text('Past Reports (${_reports.length})',
                    style: Theme.of(context).textTheme.titleLarge),
                const SizedBox(height: 8),
                if (_error != null)
                  Text(_error!, style: const TextStyle(color: Colors.red)),
                ..._reports.map((r) => Card(
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(r['notes'] ?? '',
                                style: const TextStyle(fontWeight: FontWeight.bold)),
                            if (r['mileageAt'] != null)
                              Text('Mileage: ${r['mileageAt']} km'),
                            if (r['fuelLevel'] != null)
                              Text('Fuel: ${r['fuelLevel']}'),
                            Text('${r['createdAt'] ?? ''}',
                                style: const TextStyle(
                                    fontSize: 12, color: Colors.grey)),
                            if ((r['imageUrls'] as List?)?.isNotEmpty ?? false) ...[
                              const SizedBox(height: 8),
                              SizedBox(
                                height: 80,
                                child: ListView(
                                  scrollDirection: Axis.horizontal,
                                  children: (r['imageUrls'] as List)
                                      .map<Widget>((url) => Padding(
                                            padding: const EdgeInsets.only(right: 8),
                                            child: ClipRRect(
                                              borderRadius:
                                                  BorderRadius.circular(8),
                                              child: Image.network(url,
                                                  width: 80, fit: BoxFit.cover),
                                            ),
                                          ))
                                      .toList(),
                                ),
                              ),
                            ],
                          ],
                        ),
                      ),
                    )),
              ],
            ),
    );
  }
}
