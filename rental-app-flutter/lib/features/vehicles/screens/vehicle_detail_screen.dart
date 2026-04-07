import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../providers/vehicle_provider.dart';
import '../../auth/providers/auth_provider.dart';

class VehicleDetailScreen extends ConsumerStatefulWidget {
  final String id;
  const VehicleDetailScreen({super.key, required this.id});

  @override
  ConsumerState<VehicleDetailScreen> createState() => _VehicleDetailScreenState();
}

class _VehicleDetailScreenState extends ConsumerState<VehicleDetailScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() {
      ref.read(vehicleNotifierProvider).recordView(widget.id);
    });
  }

  @override
  Widget build(BuildContext context) {
    final vehicleAsync = ref.watch(vehicleDetailProvider(widget.id));
    final currentUser = ref.watch(authProvider).user;
    final isTenant = currentUser?.role == 'TENANT';

    return Scaffold(
      appBar: AppBar(
        title: const Text('Vehicle Details'),
        actions: [
          if (currentUser?.role == 'LANDLORD' || currentUser?.role == 'ADMIN')
            IconButton(
              tooltip: 'Condition Reports',
              icon: const Icon(Icons.assignment_outlined),
              onPressed: () =>
                  context.push('/vehicles/${widget.id}/condition'),
            ),
        ],
      ),
      floatingActionButton: isTenant
          ? FloatingActionButton.extended(
              onPressed: () => context.push('/vehicles/${widget.id}/book'),
              icon: const Icon(Icons.calendar_today),
              label: const Text('Book Vehicle'),
            )
          : null,
      body: vehicleAsync.when(
        data: (vehicle) => SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (vehicle.images != null && vehicle.images!.isNotEmpty)
                SizedBox(
                  height: 250,
                  child: PageView.builder(
                    itemCount: vehicle.images!.length,
                    itemBuilder: (context, index) => CachedNetworkImage(
                      imageUrl: vehicle.images![index].imageUrl,
                      fit: BoxFit.cover,
                      width: double.infinity,
                    ),
                  ),
                ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('${vehicle.brand} ${vehicle.model}',
                        style: Theme.of(context).textTheme.headlineMedium),
                    Text('${vehicle.dailyPrice} ${vehicle.currency}/day',
                        style: TextStyle(
                            fontSize: 20,
                            color: Theme.of(context).primaryColor,
                            fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    if (vehicle.pickupCity != null || vehicle.pickupAddress != null)
                      Row(
                        children: [
                          const Icon(Icons.location_on, size: 16, color: Colors.grey),
                          const SizedBox(width: 4),
                          Expanded(
                            child: Text(
                              [vehicle.pickupAddress, vehicle.pickupCity]
                                  .where((s) => s != null && s.isNotEmpty)
                                  .join(', '),
                              style: const TextStyle(color: Colors.grey),
                            ),
                          ),
                        ],
                      ),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      children: [
                        Chip(label: Text(vehicle.transmission), visualDensity: VisualDensity.compact),
                        Chip(label: Text(vehicle.fuelType), visualDensity: VisualDensity.compact),
                        if (vehicle.color != null)
                          Chip(label: Text(vehicle.color!), visualDensity: VisualDensity.compact),
                      ],
                    ),
                    const Divider(height: 32),
                    const Text('Description',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    Text(vehicle.description ?? 'No description available'),
                    const SizedBox(height: 16),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        _InfoIcon(icon: Icons.calendar_today, label: '${vehicle.year}'),
                        _InfoIcon(icon: Icons.settings, label: vehicle.transmission),
                        _InfoIcon(icon: Icons.local_gas_station, label: vehicle.fuelType),
                        _InfoIcon(icon: Icons.event_seat, label: '${vehicle.seats ?? '-'} seats'),
                      ],
                    ),
                    if (vehicle.mileage != null) ...[
                      const SizedBox(height: 16),
                      Row(
                        children: [
                          const Icon(Icons.speed, size: 18, color: Colors.grey),
                          const SizedBox(width: 8),
                          Text('${vehicle.mileage} km', style: const TextStyle(color: Colors.grey)),
                        ],
                      ),
                    ],
                    if (vehicle.licensePlate != null) ...[
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Icon(Icons.confirmation_number, size: 18, color: Colors.grey),
                          const SizedBox(width: 8),
                          Text(vehicle.licensePlate!, style: const TextStyle(color: Colors.grey)),
                        ],
                      ),
                    ],
                    const SizedBox(height: 80), // space for FAB
                  ],
                ),
              ),
            ],
          ),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}

class _InfoIcon extends StatelessWidget {
  final IconData icon;
  final String label;
  const _InfoIcon({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(icon, color: Colors.blue),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 12)),
      ],
    );
  }
}
