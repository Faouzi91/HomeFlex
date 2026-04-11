import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../models/maintenance_request_dto.dart';
import '../providers/maintenance_provider.dart';

class MaintenanceRequestListScreen extends ConsumerWidget {
  final bool isLandlord;

  const MaintenanceRequestListScreen({super.key, this.isLandlord = false});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final provider = isLandlord ? landlordMaintenanceRequestsProvider : myMaintenanceRequestsProvider;
    final requestsAsync = ref.watch(provider);

    return Scaffold(
      appBar: AppBar(
        title: Text(isLandlord ? 'Property Maintenance' : 'My Requests'),
      ),
      body: RefreshIndicator(
        onRefresh: () => ref.refresh(provider.future),
        child: requestsAsync.when(
          data: (requests) {
            if (requests.isEmpty) {
              return const Center(
                child: Text('No maintenance requests found'),
              );
            }
            return ListView.builder(
              itemCount: requests.length,
              itemBuilder: (context, index) {
                final request = requests[index];
                return ListTile(
                  title: Text(request.title),
                  subtitle: Text(
                    '${request.propertyTitle} • ${request.status.name}',
                    style: TextStyle(
                      color: _getStatusColor(request.status),
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push('/maintenance/${request.id}'),
                );
              },
            );
          },
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, s) => Center(child: Text('Error: $e')),
        ),
      ),
    );
  }

  Color _getStatusColor(MaintenanceStatus status) {
    switch (status) {
      case MaintenanceStatus.REPORTED:
        return Colors.orange;
      case MaintenanceStatus.IN_PROGRESS:
        return Colors.blue;
      case MaintenanceStatus.RESOLVED:
        return Colors.green;
      case MaintenanceStatus.CANCELLED:
        return Colors.grey;
    }
  }
}
