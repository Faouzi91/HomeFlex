import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/booking_provider.dart';
import '../models/booking_dto.dart';

class PropertyBookingsScreen extends ConsumerWidget {
  final String propertyId;
  const PropertyBookingsScreen({super.key, required this.propertyId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final bookingsAsync = ref.watch(propertyBookingsProvider(propertyId));

    return Scaffold(
      appBar: AppBar(title: const Text('Property Bookings')),
      body: bookingsAsync.when(
        data: (bookings) {
          if (bookings.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.calendar_today, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('No bookings for this property',
                      style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }
          return RefreshIndicator(
            onRefresh: () async =>
                ref.invalidate(propertyBookingsProvider(propertyId)),
            child: ListView.builder(
              padding: const EdgeInsets.all(8),
              itemCount: bookings.length,
              itemBuilder: (context, index) {
                final booking = bookings[index];
                return _LandlordBookingCard(booking: booking);
              },
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}

class _LandlordBookingCard extends ConsumerWidget {
  final BookingDto booking;
  const _LandlordBookingCard({required this.booking});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isPending = booking.status.toUpperCase() == 'PENDING';

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Text(
                    booking.tenant != null
                        ? '${booking.tenant!.firstName} ${booking.tenant!.lastName}'
                        : 'Tenant',
                    style: const TextStyle(
                        fontWeight: FontWeight.bold, fontSize: 16),
                  ),
                ),
                _StatusChip(status: booking.status),
              ],
            ),
            const SizedBox(height: 8),
            if (booking.startDate != null && booking.endDate != null)
              Row(
                children: [
                  const Icon(Icons.calendar_today,
                      size: 14, color: Colors.grey),
                  const SizedBox(width: 4),
                  Text('${booking.startDate} - ${booking.endDate}',
                      style:
                          const TextStyle(fontSize: 13, color: Colors.grey)),
                ],
              ),
            if (booking.bookingType.isNotEmpty) ...[
              const SizedBox(height: 4),
              Text('Type: ${booking.bookingType}',
                  style: const TextStyle(fontSize: 13, color: Colors.grey)),
            ],
            if (booking.totalPrice != null) ...[
              const SizedBox(height: 4),
              Text('Total: ${booking.totalPrice} XAF',
                  style: TextStyle(
                      color: Theme.of(context).primaryColor,
                      fontWeight: FontWeight.bold)),
            ],
            if (booking.message != null && booking.message!.isNotEmpty) ...[
              const SizedBox(height: 8),
              Text('Message: ${booking.message}',
                  style: const TextStyle(
                      fontStyle: FontStyle.italic, color: Colors.grey)),
            ],
            if (isPending) ...[
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: FilledButton(
                      onPressed: () async {
                        await ref
                            .read(bookingNotifierProvider)
                            .approveBooking(booking.id);
                        ref.invalidate(
                            propertyBookingsProvider(booking.property!.id));
                      },
                      child: const Text('Approve'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () =>
                          _rejectDialog(context, ref, booking),
                      style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.red),
                      child: const Text('Reject'),
                    ),
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Future<void> _rejectDialog(
      BuildContext context, WidgetRef ref, BookingDto booking) async {
    final reasonController = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Reject Booking'),
        content: TextField(
          controller: reasonController,
          decoration:
              const InputDecoration(hintText: 'Reason for rejection'),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          TextButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: const Text('Reject')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(bookingNotifierProvider).rejectBooking(
            booking.id,
            message: reasonController.text.isNotEmpty
                ? reasonController.text
                : null,
          );
      ref.invalidate(propertyBookingsProvider(booking.property!.id));
    }
    reasonController.dispose();
  }
}

class _StatusChip extends StatelessWidget {
  final String status;
  const _StatusChip({required this.status});

  @override
  Widget build(BuildContext context) {
    Color color;
    switch (status.toUpperCase()) {
      case 'APPROVED':
        color = Colors.green;
        break;
      case 'REJECTED':
        color = Colors.red;
        break;
      case 'CANCELLED':
        color = Colors.grey;
        break;
      case 'COMPLETED':
        color = Colors.blue;
        break;
      default:
        color = Colors.orange;
    }
    return Chip(
      label: Text(status, style: TextStyle(color: color, fontSize: 12)),
      backgroundColor: color.withValues(alpha: 0.1),
      side: BorderSide.none,
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}
