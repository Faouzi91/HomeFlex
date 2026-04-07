import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/vehicle_provider.dart';

class CreateVehicleBookingScreen extends ConsumerStatefulWidget {
  final String vehicleId;
  const CreateVehicleBookingScreen({super.key, required this.vehicleId});

  @override
  ConsumerState<CreateVehicleBookingScreen> createState() => _CreateVehicleBookingScreenState();
}

class _CreateVehicleBookingScreenState extends ConsumerState<CreateVehicleBookingScreen> {
  final _messageController = TextEditingController();
  DateTimeRange? _dateRange;
  bool _isSubmitting = false;
  bool? _isAvailable;

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Book Vehicle')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            OutlinedButton.icon(
              onPressed: _selectDates,
              icon: const Icon(Icons.calendar_today),
              label: Text(_dateRange != null
                  ? '${_dateRange!.start.toString().split(' ')[0]} - ${_dateRange!.end.toString().split(' ')[0]}'
                  : 'Select Rental Dates'),
            ),
            if (_isAvailable != null) ...[
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(
                    _isAvailable! ? Icons.check_circle : Icons.cancel,
                    color: _isAvailable! ? Colors.green : Colors.red,
                    size: 18,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    _isAvailable! ? 'Vehicle is available' : 'Vehicle is not available',
                    style: TextStyle(color: _isAvailable! ? Colors.green : Colors.red),
                  ),
                ],
              ),
            ],
            const SizedBox(height: 16),
            TextField(
              controller: _messageController,
              decoration: const InputDecoration(
                labelText: 'Message (optional)',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
            const SizedBox(height: 24),
            SizedBox(
              height: 48,
              child: _isSubmitting
                  ? const Center(child: CircularProgressIndicator())
                  : FilledButton(
                      onPressed: (_isAvailable == true) ? _submit : null,
                      child: const Text('Book Vehicle'),
                    ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _selectDates() async {
    final range = await showDateRangePicker(
      context: context,
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 365)),
    );
    if (range != null) {
      setState(() {
        _dateRange = range;
        _isAvailable = null;
      });
      _checkAvailability();
    }
  }

  Future<void> _checkAvailability() async {
    if (_dateRange == null) return;
    try {
      final available = await ref.read(vehicleAvailabilityProvider({
        'id': widget.vehicleId,
        'startDate': _dateRange!.start.toString().split(' ')[0],
        'endDate': _dateRange!.end.toString().split(' ')[0],
      }).future);
      if (mounted) setState(() => _isAvailable = available);
    } catch (_) {
      if (mounted) setState(() => _isAvailable = true);
    }
  }

  Future<void> _submit() async {
    if (_dateRange == null) return;
    setState(() => _isSubmitting = true);
    try {
      await ref.read(vehicleNotifierProvider).createVehicleBooking(
            vehicleId: widget.vehicleId,
            startDate: _dateRange!.start.toString().split(' ')[0],
            endDate: _dateRange!.end.toString().split(' ')[0],
            message: _messageController.text.isNotEmpty ? _messageController.text : null,
          );
      ref.invalidate(myVehicleBookingsProvider);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Vehicle booked!')),
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
