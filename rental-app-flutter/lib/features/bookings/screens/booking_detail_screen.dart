import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/booking_provider.dart';
import '../../auth/providers/auth_provider.dart';

class BookingDetailScreen extends ConsumerWidget {
  final String id;
  const BookingDetailScreen({super.key, required this.id});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final bookingAsync = ref.watch(bookingDetailProvider(id));
    final currentUser = ref.watch(authProvider).user;

    return Scaffold(
      appBar: AppBar(title: const Text('Booking Details')),
      body: bookingAsync.when(
        data: (booking) {
          final isTenant = currentUser?.id == booking.tenant?.id;
          final isLandlord = currentUser?.role == 'LANDLORD' || currentUser?.role == 'ADMIN';
          final isPending = booking.status.toUpperCase() == 'PENDING';

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(booking.property?.title ?? 'Property',
                            style: Theme.of(context).textTheme.titleLarge),
                        const SizedBox(height: 8),
                        _InfoRow('Status', booking.status),
                        _InfoRow('Type', booking.bookingType),
                        if (booking.startDate != null)
                          _InfoRow('Start Date', booking.startDate!),
                        if (booking.endDate != null)
                          _InfoRow('End Date', booking.endDate!),
                        if (booking.numberOfOccupants != null)
                          _InfoRow('Occupants', '${booking.numberOfOccupants}'),
                        if (booking.totalPrice != null)
                          _InfoRow('Total Price', '${booking.totalPrice} XAF'),
                        if (booking.platformFee != null)
                          _InfoRow('Platform Fee', '${booking.platformFee} XAF'),
                      ],
                    ),
                  ),
                ),
                if (booking.message != null && booking.message!.isNotEmpty) ...[
                  const SizedBox(height: 16),
                  Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Message', style: TextStyle(fontWeight: FontWeight.bold)),
                          const SizedBox(height: 8),
                          Text(booking.message!),
                        ],
                      ),
                    ),
                  ),
                ],
                if (booking.landlordResponse != null) ...[
                  const SizedBox(height: 16),
                  Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Landlord Response',
                              style: TextStyle(fontWeight: FontWeight.bold)),
                          const SizedBox(height: 8),
                          Text(booking.landlordResponse!),
                        ],
                      ),
                    ),
                  ),
                ],
                const SizedBox(height: 24),
                if (isPending && isTenant)
                  SizedBox(
                    width: double.infinity,
                    child: OutlinedButton(
                      onPressed: () => _cancelBooking(context, ref),
                      style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
                      child: const Text('Cancel Booking'),
                    ),
                  ),
                if ((booking.status.toUpperCase() == 'APPROVED' ||
                        booking.status.toUpperCase() == 'COMPLETED') &&
                    isTenant)
                  Padding(
                    padding: const EdgeInsets.only(top: 16),
                    child: SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () => context.push(
                            '/properties/${booking.property?.id}/maintenance/create'),
                        icon: const Icon(Icons.report_problem),
                        label: const Text('Report Maintenance Issue'),
                      ),
                    ),
                  ),
                if (isPending && isLandlord) ...[
                  Row(
                    children: [
                      Expanded(
                        child: FilledButton(
                          onPressed: () => _approveBooking(context, ref),
                          child: const Text('Approve'),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () => _rejectBooking(context, ref),
                          style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
                          child: const Text('Reject'),
                        ),
                      ),
                    ],
                  ),
                ],
              ],
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }

  Future<void> _cancelBooking(BuildContext context, WidgetRef ref) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Cancel Booking'),
        content: const Text('Are you sure you want to cancel this booking?'),
        actions: [
          TextButton(onPressed: () => ctx.pop(false), child: const Text('No')),
          TextButton(onPressed: () => ctx.pop(true), child: const Text('Yes, Cancel')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(bookingNotifierProvider).cancelBooking(id);
      ref.invalidate(bookingDetailProvider(id));
      ref.invalidate(myBookingsProvider);
    }
  }

  Future<void> _approveBooking(BuildContext context, WidgetRef ref) async {
    await ref.read(bookingNotifierProvider).approveBooking(id);
    ref.invalidate(bookingDetailProvider(id));
    ref.invalidate(myBookingsProvider);
  }

  Future<void> _rejectBooking(BuildContext context, WidgetRef ref) async {
    final messageController = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Reject Booking'),
        content: TextField(
          controller: messageController,
          decoration: const InputDecoration(hintText: 'Reason for rejection'),
        ),
        actions: [
          TextButton(onPressed: () => ctx.pop(false), child: const Text('Cancel')),
          TextButton(onPressed: () => ctx.pop(true), child: const Text('Reject')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(bookingNotifierProvider).rejectBooking(
            id,
            message: messageController.text.isNotEmpty ? messageController.text : null,
          );
      ref.invalidate(bookingDetailProvider(id));
      ref.invalidate(myBookingsProvider);
    }
    messageController.dispose();
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  const _InfoRow(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.grey)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }
}
