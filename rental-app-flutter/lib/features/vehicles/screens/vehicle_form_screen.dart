import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import '../providers/vehicle_provider.dart';

class VehicleFormScreen extends ConsumerStatefulWidget {
  final String? vehicleId;
  const VehicleFormScreen({super.key, this.vehicleId});

  @override
  ConsumerState<VehicleFormScreen> createState() => _VehicleFormScreenState();
}

class _VehicleFormScreenState extends ConsumerState<VehicleFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _brandController = TextEditingController();
  final _modelController = TextEditingController();
  final _yearController = TextEditingController();
  final _priceController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _mileageController = TextEditingController();
  final _seatsController = TextEditingController(text: '5');
  final _colorController = TextEditingController();
  final _plateController = TextEditingController();
  final _cityController = TextEditingController();
  final _addressController = TextEditingController();
  String _transmission = 'AUTOMATIC';
  String _fuelType = 'PETROL';
  final List<XFile> _selectedImages = [];
  bool _isSubmitting = false;

  bool get _isEditing => widget.vehicleId != null;

  @override
  void initState() {
    super.initState();
    if (_isEditing) _loadVehicle();
  }

  Future<void> _loadVehicle() async {
    try {
      final vehicle = await ref.read(vehicleDetailProvider(widget.vehicleId!).future);
      _brandController.text = vehicle.brand;
      _modelController.text = vehicle.model;
      _yearController.text = vehicle.year.toString();
      _priceController.text = vehicle.dailyPrice.toString();
      _descriptionController.text = vehicle.description ?? '';
      _mileageController.text = vehicle.mileage?.toString() ?? '';
      _seatsController.text = vehicle.seats?.toString() ?? '5';
      _colorController.text = vehicle.color ?? '';
      _plateController.text = vehicle.licensePlate ?? '';
      _cityController.text = vehicle.pickupCity ?? '';
      _addressController.text = vehicle.pickupAddress ?? '';
      setState(() {
        _transmission = vehicle.transmission;
        _fuelType = vehicle.fuelType;
      });
    } catch (_) {}
  }

  @override
  void dispose() {
    _brandController.dispose();
    _modelController.dispose();
    _yearController.dispose();
    _priceController.dispose();
    _descriptionController.dispose();
    _mileageController.dispose();
    _seatsController.dispose();
    _colorController.dispose();
    _plateController.dispose();
    _cityController.dispose();
    _addressController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_isEditing ? 'Edit Vehicle' : 'Add Vehicle')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _brandController,
                      decoration: const InputDecoration(labelText: 'Brand', border: OutlineInputBorder()),
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _modelController,
                      decoration: const InputDecoration(labelText: 'Model', border: OutlineInputBorder()),
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
                      controller: _yearController,
                      decoration: const InputDecoration(labelText: 'Year', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _priceController,
                      decoration: const InputDecoration(labelText: 'Daily Price (XAF)', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      initialValue: _transmission,
                      decoration: const InputDecoration(labelText: 'Transmission', border: OutlineInputBorder()),
                      items: const [
                        DropdownMenuItem(value: 'AUTOMATIC', child: Text('Automatic')),
                        DropdownMenuItem(value: 'MANUAL', child: Text('Manual')),
                      ],
                      onChanged: (v) => setState(() => _transmission = v!),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      initialValue: _fuelType,
                      decoration: const InputDecoration(labelText: 'Fuel Type', border: OutlineInputBorder()),
                      items: const [
                        DropdownMenuItem(value: 'PETROL', child: Text('Petrol')),
                        DropdownMenuItem(value: 'DIESEL', child: Text('Diesel')),
                        DropdownMenuItem(value: 'HYBRID', child: Text('Hybrid')),
                        DropdownMenuItem(value: 'ELECTRIC', child: Text('Electric')),
                      ],
                      onChanged: (v) => setState(() => _fuelType = v!),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(labelText: 'Description', border: OutlineInputBorder()),
                maxLines: 3,
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _seatsController,
                      decoration: const InputDecoration(labelText: 'Seats', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _mileageController,
                      decoration: const InputDecoration(labelText: 'Mileage (km)', border: OutlineInputBorder()),
                      keyboardType: TextInputType.number,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _colorController,
                      decoration: const InputDecoration(labelText: 'Color', border: OutlineInputBorder()),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _plateController,
                      decoration: const InputDecoration(labelText: 'License Plate', border: OutlineInputBorder()),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _cityController,
                      decoration: const InputDecoration(labelText: 'Pickup City', border: OutlineInputBorder()),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _addressController,
                      decoration: const InputDecoration(labelText: 'Pickup Address', border: OutlineInputBorder()),
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
              const SizedBox(height: 24),
              SizedBox(
                height: 48,
                child: _isSubmitting
                    ? const Center(child: CircularProgressIndicator())
                    : FilledButton(
                        onPressed: _submit,
                        child: Text(_isEditing ? 'Update Vehicle' : 'Add Vehicle'),
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
      final data = {
        'brand': _brandController.text.trim(),
        'model': _modelController.text.trim(),
        'year': int.parse(_yearController.text.trim()),
        'transmission': _transmission,
        'fuelType': _fuelType,
        'dailyPrice': double.parse(_priceController.text.trim()),
        'currency': 'XAF',
        'seats': int.parse(_seatsController.text.trim()),
        if (_descriptionController.text.isNotEmpty) 'description': _descriptionController.text.trim(),
        if (_mileageController.text.isNotEmpty) 'mileage': int.parse(_mileageController.text),
        if (_colorController.text.isNotEmpty) 'color': _colorController.text.trim(),
        if (_plateController.text.isNotEmpty) 'licensePlate': _plateController.text.trim(),
        if (_cityController.text.isNotEmpty) 'pickupCity': _cityController.text.trim(),
        if (_addressController.text.isNotEmpty) 'pickupAddress': _addressController.text.trim(),
      };

      if (_isEditing) {
        await ref.read(vehicleNotifierProvider).updateVehicle(widget.vehicleId!, data);
        if (_selectedImages.isNotEmpty) {
          await ref.read(vehicleNotifierProvider).uploadImages(
                widget.vehicleId!,
                _selectedImages.map((img) => img.path).toList(),
              );
        }
      } else {
        final vehicle = await ref.read(vehicleNotifierProvider).createVehicle(data);
        if (_selectedImages.isNotEmpty) {
          await ref.read(vehicleNotifierProvider).uploadImages(
                vehicle.id,
                _selectedImages.map((img) => img.path).toList(),
              );
        }
      }

      ref.invalidate(vehiclesProvider);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(_isEditing ? 'Vehicle updated' : 'Vehicle added')),
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
