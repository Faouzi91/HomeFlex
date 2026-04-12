import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/maintenance_request_dto.dart';
import '../providers/maintenance_provider.dart';

class MaintenanceRequestDetailScreen extends ConsumerWidget {
  final String id;

  const MaintenanceRequestDetailScreen({super.key, required this.id});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final requestAsync = ref.watch(maintenanceRequestDetailProvider(id));

    return Scaffold(
      appBar: AppBar(title: const Text('Request Details')),
      body: requestAsync.when(
        data: (request) => SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHeader(request),
              const Divider(height: 32),
              _buildDescription(request),
              if (request.imageUrls != null &&
                  request.imageUrls!.isNotEmpty) ...[
                const SizedBox(height: 24),
                _buildImages(request),
              ],
              if (request.status == MaintenanceStatus.RESOLVED) ...[
                const SizedBox(height: 24),
                _buildResolution(request),
              ],
              const SizedBox(height: 32),
              _buildStatusActions(context, ref, request),
            ],
          ),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, s) => Center(child: Text('Error: $e')),
      ),
    );
  }

  Widget _buildHeader(MaintenanceRequestDto request) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          request.title,
          style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        Row(
          children: [
            _buildBadge(
              request.category.name,
              Colors.blue.shade100,
              Colors.blue.shade900,
            ),
            const SizedBox(width: 8),
            _buildBadge(
              request.priority.name,
              _getPriorityColor(request.priority).withValues(alpha: 0.1),
              _getPriorityColor(request.priority),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Text(
          'Property: ${request.propertyTitle}',
          style: const TextStyle(fontSize: 16),
        ),
        Text(
          'Tenant: ${request.tenantName}',
          style: const TextStyle(fontSize: 14, color: Colors.grey),
        ),
        Text(
          'Reported: ${request.createdAt}',
          style: const TextStyle(fontSize: 14, color: Colors.grey),
        ),
      ],
    );
  }

  Widget _buildDescription(MaintenanceRequestDto request) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Description',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        Text(request.description, style: const TextStyle(fontSize: 16)),
      ],
    );
  }

  Widget _buildImages(MaintenanceRequestDto request) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Photos',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        SizedBox(
          height: 120,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: request.imageUrls!.length,
            itemBuilder: (context, index) {
              return Padding(
                padding: const EdgeInsets.only(right: 8.0),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: Image.network(
                    request.imageUrls![index],
                    width: 120,
                    height: 120,
                    fit: BoxFit.cover,
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildResolution(MaintenanceRequestDto request) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.green.shade50,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.green.shade200),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Resolution Notes',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: Colors.green,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            request.resolutionNotes ?? 'Resolved without notes.',
            style: const TextStyle(fontSize: 15),
          ),
          const SizedBox(height: 4),
          Text(
            'Resolved at: ${request.resolvedAt}',
            style: const TextStyle(fontSize: 12, color: Colors.grey),
          ),
        ],
      ),
    );
  }

  Widget _buildStatusActions(
    BuildContext context,
    WidgetRef ref,
    MaintenanceRequestDto request,
  ) {
    // Basic logic: only show "Update Status" if not already resolved/cancelled
    if (request.status == MaintenanceStatus.RESOLVED ||
        request.status == MaintenanceStatus.CANCELLED) {
      return const SizedBox.shrink();
    }

    return SizedBox(
      width: double.infinity,
      child: ElevatedButton(
        onPressed: () => _showStatusUpdateDialog(context, ref, request),
        child: const Text('Update Status'),
      ),
    );
  }

  void _showStatusUpdateDialog(
    BuildContext context,
    WidgetRef ref,
    MaintenanceRequestDto request,
  ) {
    final notesController = TextEditingController();
    MaintenanceStatus selectedStatus = request.status;

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) => AlertDialog(
          title: const Text('Update Status'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButtonFormField<MaintenanceStatus>(
                initialValue: selectedStatus,
                items: MaintenanceStatus.values
                    .map((s) => DropdownMenuItem(value: s, child: Text(s.name)))
                    .toList(),
                onChanged: (val) => setState(() => selectedStatus = val!),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: notesController,
                decoration: const InputDecoration(labelText: 'Notes'),
                maxLines: 3,
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () async {
                try {
                  await ref
                      .read(maintenanceNotifierProvider.notifier)
                      .updateStatus(
                        request.id,
                        MaintenanceStatusUpdateRequest(
                          status: selectedStatus,
                          resolutionNotes: notesController.text,
                        ),
                      );
                  if (context.mounted) {
                    Navigator.pop(context);
                    ref.invalidate(maintenanceRequestDetailProvider(id));
                    ref.invalidate(landlordMaintenanceRequestsProvider);
                    ref.invalidate(myMaintenanceRequestsProvider);
                  }
                } catch (e) {
                  // Handle error
                }
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBadge(String label, Color bgColor, Color textColor) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: textColor,
          fontSize: 12,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Color _getPriorityColor(MaintenancePriority priority) {
    switch (priority) {
      case MaintenancePriority.LOW:
        return Colors.green;
      case MaintenancePriority.MEDIUM:
        return Colors.blue;
      case MaintenancePriority.HIGH:
        return Colors.orange;
      case MaintenancePriority.URGENT:
        return Colors.red;
    }
  }
}
