import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../providers/vehicle_provider.dart';
import '../models/vehicle_response.dart';
import '../../../core/api/api_client.dart';

final _myVehiclesProvider = FutureProvider<List<VehicleResponse>>((ref) async {
  // Backend doesn't have a dedicated my-vehicles endpoint; use search filtered by owner
  // For now, use the general search - landlord can see their vehicles
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/vehicles/search');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => VehicleResponse.fromJson(json)).toList();
});

class MyVehiclesScreen extends ConsumerWidget {
  const MyVehiclesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final vehiclesAsync = ref.watch(_myVehiclesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('My Vehicles')),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/vehicles/create'),
        child: const Icon(Icons.add),
      ),
      body: vehiclesAsync.when(
        data: (vehicles) {
          if (vehicles.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.directions_car_outlined, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('No vehicles yet', style: TextStyle(color: Colors.grey)),
                  SizedBox(height: 8),
                  Text('Tap + to add your first vehicle', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }
          return ListView.builder(
            padding: const EdgeInsets.all(8),
            itemCount: vehicles.length,
            itemBuilder: (context, index) {
              final vehicle = vehicles[index];
              return _MyVehicleCard(vehicle: vehicle);
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}

class _MyVehicleCard extends ConsumerWidget {
  final VehicleResponse vehicle;
  const _MyVehicleCard({required this.vehicle});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final imageUrl = (vehicle.images != null && vehicle.images!.isNotEmpty)
        ? vehicle.images!.first.imageUrl
        : null;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => context.push('/vehicles/${vehicle.id}'),
        child: Row(
          children: [
            SizedBox(
              width: 120,
              height: 100,
              child: imageUrl != null
                  ? CachedNetworkImage(imageUrl: imageUrl, fit: BoxFit.cover)
                  : Container(color: Colors.grey[300], child: const Icon(Icons.directions_car)),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('${vehicle.brand} ${vehicle.model}',
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text('${vehicle.dailyPrice} ${vehicle.currency}/day',
                        style: TextStyle(color: Theme.of(context).primaryColor)),
                    const SizedBox(height: 4),
                    Text(vehicle.status, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                  ],
                ),
              ),
            ),
            PopupMenuButton<String>(
              onSelected: (action) async {
                if (action == 'edit') {
                  context.push('/vehicles/${vehicle.id}/edit');
                } else if (action == 'delete') {
                  final confirmed = await showDialog<bool>(
                    context: context,
                    builder: (ctx) => AlertDialog(
                      title: const Text('Delete Vehicle'),
                      content: const Text('Are you sure?'),
                      actions: [
                        TextButton(onPressed: () => ctx.pop(false), child: const Text('Cancel')),
                        TextButton(onPressed: () => ctx.pop(true), child: const Text('Delete')),
                      ],
                    ),
                  );
                  if (confirmed == true) {
                    await ref.read(vehicleNotifierProvider).deleteVehicle(vehicle.id);
                    ref.invalidate(_myVehiclesProvider);
                  }
                }
              },
              itemBuilder: (_) => [
                const PopupMenuItem(value: 'edit', child: Text('Edit')),
                const PopupMenuItem(value: 'delete', child: Text('Delete')),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
