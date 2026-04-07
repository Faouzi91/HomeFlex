import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/admin_provider.dart';
class PendingPropertiesScreen extends ConsumerWidget {
  const PendingPropertiesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final propertiesAsync = ref.watch(pendingPropertiesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Pending Properties')),
      body: propertiesAsync.when(
        data: (properties) {
          if (properties.isEmpty) {
            return const Center(child: Text('No pending properties'));
          }
          return ListView.builder(
            padding: const EdgeInsets.all(8),
            itemCount: properties.length,
            itemBuilder: (context, index) {
              final property = properties[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(property.title,
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      const SizedBox(height: 4),
                      Text('${property.city}, ${property.country}',
                          style: const TextStyle(color: Colors.grey)),
                      Text('${property.price} ${property.currency}',
                          style: TextStyle(color: Theme.of(context).primaryColor)),
                      if (property.landlord != null)
                        Text('By: ${property.landlord!.firstName} ${property.landlord!.lastName}',
                            style: const TextStyle(fontSize: 12, color: Colors.grey)),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: FilledButton(
                              onPressed: () async {
                                await ref.read(adminNotifierProvider).approveProperty(property.id);
                                ref.invalidate(pendingPropertiesProvider);
                              },
                              child: const Text('Approve'),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: OutlinedButton(
                              onPressed: () => _rejectDialog(context, ref, property.id),
                              style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
                              child: const Text('Reject'),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }

  Future<void> _rejectDialog(BuildContext context, WidgetRef ref, String propertyId) async {
    final reasonController = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Reject Property'),
        content: TextField(
          controller: reasonController,
          decoration: const InputDecoration(hintText: 'Reason for rejection'),
        ),
        actions: [
          TextButton(onPressed: () => ctx.pop(false), child: const Text('Cancel')),
          TextButton(onPressed: () => ctx.pop(true), child: const Text('Reject')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(adminNotifierProvider).rejectProperty(propertyId, reasonController.text);
      ref.invalidate(pendingPropertiesProvider);
    }
    reasonController.dispose();
  }
}
