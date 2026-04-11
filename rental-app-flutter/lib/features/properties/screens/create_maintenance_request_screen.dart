import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import 'package:dio/dio.dart';
import 'dart:io';
import '../models/maintenance_request_dto.dart';
import '../providers/maintenance_provider.dart';

class CreateMaintenanceRequestScreen extends ConsumerStatefulWidget {
  final String propertyId;

  const CreateMaintenanceRequestScreen({super.key, required this.propertyId});

  @override
  ConsumerState<CreateMaintenanceRequestScreen> createState() => _CreateMaintenanceRequestScreenState();
}

class _CreateMaintenanceRequestScreenState extends ConsumerState<CreateMaintenanceRequestScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  MaintenanceCategory _category = MaintenanceCategory.OTHER;
  MaintenancePriority _priority = MaintenancePriority.MEDIUM;
  final List<XFile> _images = [];
  bool _isSubmitting = false;

  Future<void> _pickImages() async {
    final picker = ImagePicker();
    final picked = await picker.pickMultiImage();
    if (picked.isNotEmpty) {
      setState(() => _images.addAll(picked));
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    try {
      final notifier = ref.read(maintenanceNotifierProvider.notifier);
      final request = await notifier.createRequest(
        MaintenanceRequestCreateRequest(
          propertyId: widget.propertyId,
          title: _titleController.text,
          description: _descriptionController.text,
          category: _category,
          priority: _priority,
        ),
      );

      if (_images.isNotEmpty) {
        final multipartFiles = <MultipartFile>[];
        for (var image in _images) {
          multipartFiles.add(await MultipartFile.fromFile(image.path));
        }
        await notifier.uploadImages(request.id, multipartFiles);
      }

      if (mounted) {
        ref.invalidate(myMaintenanceRequestsProvider);
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to create request: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Report Issue')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: 'Summary', hintText: 'e.g. Broken AC'),
                validator: (val) => val == null || val.isEmpty ? 'Please enter a title' : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<MaintenanceCategory>(
                value: _category,
                decoration: const InputDecoration(labelText: 'Category'),
                items: MaintenanceCategory.values
                    .map((c) => DropdownMenuItem(value: c, child: Text(c.name)))
                    .toList(),
                onChanged: (val) => setState(() => _category = val!),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<MaintenancePriority>(
                value: _priority,
                decoration: const InputDecoration(labelText: 'Priority'),
                items: MaintenancePriority.values
                    .map((p) => DropdownMenuItem(value: p, child: Text(p.name)))
                    .toList(),
                onChanged: (val) => setState(() => _priority = val!),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(labelText: 'Description', alignLabelWithHint: true),
                maxLines: 5,
                validator: (val) => val == null || val.isEmpty ? 'Please describe the issue' : null,
              ),
              const SizedBox(height: 24),
              const Text('Photos', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: [
                  ..._images.map((img) => Stack(
                    children: [
                      ClipRRect(
                        borderRadius: BorderRadius.circular(8),
                        child: Image.file(File(img.path), width: 80, height: 80, fit: BoxFit.cover),
                      ),
                      Positioned(
                        right: -4,
                        top: -4,
                        child: IconButton(
                          icon: const Icon(Icons.cancel, color: Colors.red),
                          onPressed: () => setState(() => _images.remove(img)),
                        ),
                      ),
                    ],
                  )),
                  GestureDetector(
                    onTap: _pickImages,
                    child: Container(
                      width: 80,
                      height: 80,
                      decoration: BoxDecoration(
                        color: Colors.grey.shade200,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.grey.shade400, style: BorderStyle.none),
                      ),
                      child: const Icon(Icons.add_a_photo, color: Colors.grey),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                height: 50,
                child: ElevatedButton(
                  onPressed: _isSubmitting ? null : _submit,
                  child: _isSubmitting
                      ? const CircularProgressIndicator()
                      : const Text('Submit Request'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
