import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import 'package:dio/dio.dart';
import '../providers/property_provider.dart';
import 'dart:convert';

class PropertyFormScreen extends ConsumerStatefulWidget {
  final String? propertyId;
  const PropertyFormScreen({super.key, this.propertyId});

  @override
  ConsumerState<PropertyFormScreen> createState() => _PropertyFormScreenState();
}

class _PropertyFormScreenState extends ConsumerState<PropertyFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _priceController = TextEditingController();
  final _addressController = TextEditingController();
  final _cityController = TextEditingController();
  final _countryController = TextEditingController(text: 'Cameroon');
  final _bedroomsController = TextEditingController();
  final _bathroomsController = TextEditingController();
  final _areaController = TextEditingController();
  String _propertyType = 'APARTMENT';
  String _listingType = 'RENT';
  final List<XFile> _selectedImages = [];
  bool _isSubmitting = false;

  bool get _isEditing => widget.propertyId != null;

  @override
  void initState() {
    super.initState();
    if (_isEditing) _loadProperty();
  }

  Future<void> _loadProperty() async {
    try {
      final property = await ref.read(propertyDetailProvider(widget.propertyId!).future);
      _titleController.text = property.title;
      _descriptionController.text = property.description;
      _priceController.text = property.price.toString();
      _addressController.text = property.address;
      _cityController.text = property.city;
      _countryController.text = property.country;
      _bedroomsController.text = property.bedrooms?.toString() ?? '';
      _bathroomsController.text = property.bathrooms?.toString() ?? '';
      _areaController.text = property.areaSqm?.toString() ?? '';
      setState(() {
        _propertyType = property.propertyType;
        _listingType = property.listingType;
      });
    } catch (_) {}
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    _priceController.dispose();
    _addressController.dispose();
    _cityController.dispose();
    _countryController.dispose();
    _bedroomsController.dispose();
    _bathroomsController.dispose();
    _areaController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_isEditing ? 'Edit Property' : 'Create Property')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: 'Title', border: OutlineInputBorder()),
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(labelText: 'Description', border: OutlineInputBorder()),
                maxLines: 3,
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      initialValue: _propertyType,
                      decoration: const InputDecoration(labelText: 'Type', border: OutlineInputBorder()),
                      items: const [
                        DropdownMenuItem(value: 'APARTMENT', child: Text('Apartment')),
                        DropdownMenuItem(value: 'HOUSE', child: Text('House')),
                        DropdownMenuItem(value: 'VILLA', child: Text('Villa')),
                        DropdownMenuItem(value: 'STUDIO', child: Text('Studio')),
                        DropdownMenuItem(value: 'ROOM', child: Text('Room')),
                      ],
                      onChanged: (v) => setState(() => _propertyType = v!),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      initialValue: _listingType,
                      decoration: const InputDecoration(labelText: 'Listing', border: OutlineInputBorder()),
                      items: const [
                        DropdownMenuItem(value: 'RENT', child: Text('Rent')),
                        DropdownMenuItem(value: 'SALE', child: Text('Sale')),
                      ],
                      onChanged: (v) => setState(() => _listingType = v!),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _priceController,
                decoration: const InputDecoration(labelText: 'Price (XAF)', border: OutlineInputBorder()),
                keyboardType: TextInputType.number,
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _addressController,
                decoration: const InputDecoration(labelText: 'Address', border: OutlineInputBorder()),
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _cityController,
                      decoration: const InputDecoration(labelText: 'City', border: OutlineInputBorder()),
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _countryController,
                      decoration: const InputDecoration(labelText: 'Country', border: OutlineInputBorder()),
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _bedroomsController,
                      decoration: const InputDecoration(labelText: 'Bedrooms', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _bathroomsController,
                      decoration: const InputDecoration(labelText: 'Bathrooms', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _areaController,
                      decoration: const InputDecoration(labelText: 'Area (m²)', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              OutlinedButton.icon(
                onPressed: _pickImages,
                icon: const Icon(Icons.add_photo_alternate),
                label: Text('Add Images (${_selectedImages.length} selected)'),
              ),
              if (_selectedImages.isNotEmpty) ...[
                const SizedBox(height: 8),
                SizedBox(
                  height: 80,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: _selectedImages.length,
                    itemBuilder: (context, index) => Padding(
                      padding: const EdgeInsets.only(right: 8),
                      child: Stack(
                        children: [
                          ClipRRect(
                            borderRadius: BorderRadius.circular(8),
                            child: Image.asset(_selectedImages[index].path,
                                width: 80, height: 80, fit: BoxFit.cover,
                                errorBuilder: (_, _, _) => Container(
                                    width: 80, height: 80, color: Colors.grey[300],
                                    child: const Icon(Icons.image))),
                          ),
                          Positioned(
                            top: 0,
                            right: 0,
                            child: GestureDetector(
                              onTap: () => setState(() => _selectedImages.removeAt(index)),
                              child: const CircleAvatar(
                                radius: 12,
                                backgroundColor: Colors.red,
                                child: Icon(Icons.close, size: 14, color: Colors.white),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
              const SizedBox(height: 24),
              SizedBox(
                height: 48,
                child: _isSubmitting
                    ? const Center(child: CircularProgressIndicator())
                    : FilledButton(
                        onPressed: _submit,
                        child: Text(_isEditing ? 'Update Property' : 'Create Property'),
                      ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _pickImages() async {
    final picker = ImagePicker();
    final images = await picker.pickMultiImage();
    if (images.isNotEmpty) {
      setState(() => _selectedImages.addAll(images));
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isSubmitting = true);

    try {
      final propertyData = {
        'title': _titleController.text.trim(),
        'description': _descriptionController.text.trim(),
        'propertyType': _propertyType,
        'listingType': _listingType,
        'price': double.parse(_priceController.text.trim()),
        'currency': 'XAF',
        'address': _addressController.text.trim(),
        'city': _cityController.text.trim(),
        'country': _countryController.text.trim(),
        if (_bedroomsController.text.isNotEmpty) 'bedrooms': int.parse(_bedroomsController.text),
        if (_bathroomsController.text.isNotEmpty) 'bathrooms': int.parse(_bathroomsController.text),
        if (_areaController.text.isNotEmpty) 'areaSqm': double.parse(_areaController.text),
      };

      final formData = FormData.fromMap({
        'property': MultipartFile.fromString(
          jsonEncode(propertyData),
          contentType: DioMediaType.parse('application/json'),
        ),
        if (_selectedImages.isNotEmpty)
          'images': await Future.wait(
            _selectedImages.map((img) => MultipartFile.fromFile(img.path)),
          ),
      });

      if (_isEditing) {
        await ref.read(propertyNotifierProvider).updateProperty(widget.propertyId!, formData);
      } else {
        await ref.read(propertyNotifierProvider).createProperty(formData);
      }

      ref.invalidate(myPropertiesProvider);
      ref.invalidate(propertiesProvider);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(_isEditing ? 'Property updated' : 'Property created')),
        );
        context.pop();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }
}
