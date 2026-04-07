import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/booking_provider.dart';

class CreateBookingScreen extends ConsumerStatefulWidget {
  final String propertyId;
  const CreateBookingScreen({super.key, required this.propertyId});

  @override
  ConsumerState<CreateBookingScreen> createState() => _CreateBookingScreenState();
}

class _CreateBookingScreenState extends ConsumerState<CreateBookingScreen> {
  final _formKey = GlobalKey<FormState>();
  final _messageController = TextEditingController();
  final _occupantsController = TextEditingController(text: '1');
  String _bookingType = 'VISIT';
  DateTimeRange? _dateRange;
  bool _isSubmitting = false;

  @override
  void dispose() {
    _messageController.dispose();
    _occupantsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Book Property')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              DropdownButtonFormField<String>(
                initialValue: _bookingType,
                decoration: const InputDecoration(
                  labelText: 'Booking Type',
                  border: OutlineInputBorder(),
                ),
                items: const [
                  DropdownMenuItem(value: 'VISIT', child: Text('Visit')),
                  DropdownMenuItem(value: 'SHORT_TERM', child: Text('Short Term Rental')),
                  DropdownMenuItem(value: 'LONG_TERM', child: Text('Long Term Rental')),
                ],
                onChanged: (v) => setState(() => _bookingType = v!),
              ),
              const SizedBox(height: 16),
              OutlinedButton.icon(
                onPressed: () async {
                  final range = await showDateRangePicker(
                    context: context,
                    firstDate: DateTime.now(),
                    lastDate: DateTime.now().add(const Duration(days: 365)),
                  );
                  if (range != null) setState(() => _dateRange = range);
                },
                icon: const Icon(Icons.calendar_today),
                label: Text(_dateRange != null
                    ? '${_dateRange!.start.toString().split(' ')[0]} - ${_dateRange!.end.toString().split(' ')[0]}'
                    : 'Select Date Range'),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _occupantsController,
                decoration: const InputDecoration(
                  labelText: 'Number of Occupants',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (v) {
                  if (v == null || v.isEmpty) return 'Required';
                  if (int.tryParse(v) == null || int.parse(v) < 1) return 'Must be at least 1';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
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
                        onPressed: _submit,
                        child: const Text('Submit Booking'),
                      ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_dateRange == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a date range')),
      );
      return;
    }

    setState(() => _isSubmitting = true);
    try {
      await ref.read(bookingNotifierProvider).createBooking(
            propertyId: widget.propertyId,
            bookingType: _bookingType,
            startDate: _dateRange!.start.toString().split(' ')[0],
            endDate: _dateRange!.end.toString().split(' ')[0],
            message: _messageController.text.isNotEmpty ? _messageController.text : null,
            numberOfOccupants: int.parse(_occupantsController.text),
          );
      ref.invalidate(myBookingsProvider);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Booking submitted!')),
        );
        context.pop();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }
}
