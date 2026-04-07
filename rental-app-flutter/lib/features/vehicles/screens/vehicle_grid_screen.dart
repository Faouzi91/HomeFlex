import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/vehicle_provider.dart';
import '../models/vehicle_response.dart';
import 'package:cached_network_image/cached_network_image.dart';

class VehicleGridScreen extends ConsumerStatefulWidget {
  const VehicleGridScreen({super.key});

  @override
  ConsumerState<VehicleGridScreen> createState() => _VehicleGridScreenState();
}

class _VehicleGridScreenState extends ConsumerState<VehicleGridScreen> {
  final _scrollController = ScrollController();
  String? _selectedTransmission;
  String? _selectedFuelType;
  String? _selectedCity;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(paginatedVehiclesProvider.notifier).loadNextPage();
    }
  }

  void _applyFilters() {
    final filters = <String, dynamic>{
      if (_selectedTransmission != null) 'transmission': _selectedTransmission,
      if (_selectedFuelType != null) 'fuelType': _selectedFuelType,
      if (_selectedCity != null) 'city': _selectedCity,
    };
    ref.read(paginatedVehiclesProvider.notifier).loadFirstPage(filters: filters);
  }

  @override
  Widget build(BuildContext context) {
    final paginatedState = ref.watch(paginatedVehiclesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Vehicles')),
      body: Column(
        children: [
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.all(8),
            child: Row(
              children: [
                _FilterChip(
                  label: _selectedTransmission ?? 'Transmission',
                  selected: _selectedTransmission != null,
                  options: const ['AUTOMATIC', 'MANUAL'],
                  onSelected: (v) {
                    setState(() => _selectedTransmission = v);
                    _applyFilters();
                  },
                  onClear: () {
                    setState(() => _selectedTransmission = null);
                    _applyFilters();
                  },
                ),
                const SizedBox(width: 8),
                _FilterChip(
                  label: _selectedFuelType ?? 'Fuel',
                  selected: _selectedFuelType != null,
                  options: const ['PETROL', 'DIESEL', 'HYBRID', 'ELECTRIC'],
                  onSelected: (v) {
                    setState(() => _selectedFuelType = v);
                    _applyFilters();
                  },
                  onClear: () {
                    setState(() => _selectedFuelType = null);
                    _applyFilters();
                  },
                ),
                const SizedBox(width: 8),
                _FilterChip(
                  label: _selectedCity ?? 'City',
                  selected: _selectedCity != null,
                  options: const ['Douala', 'Yaoundé', 'Bamenda', 'Bafoussam', 'Kribi'],
                  onSelected: (v) {
                    setState(() => _selectedCity = v);
                    _applyFilters();
                  },
                  onClear: () {
                    setState(() => _selectedCity = null);
                    _applyFilters();
                  },
                ),
              ],
            ),
          ),
          Expanded(
            child: _buildBody(paginatedState),
          ),
        ],
      ),
    );
  }

  Widget _buildBody(PaginatedVehicleState state) {
    if (state.vehicles.isEmpty && state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (state.vehicles.isEmpty) {
      return const Center(
        child: Text('No vehicles found', style: TextStyle(color: Colors.grey)),
      );
    }
    return RefreshIndicator(
      onRefresh: () async => _applyFilters(),
      child: GridView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.all(8),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 10,
          mainAxisSpacing: 10,
        ),
        itemCount: state.vehicles.length + (state.hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index >= state.vehicles.length) {
            return const Center(child: CircularProgressIndicator());
          }
          return _VehicleCard(vehicle: state.vehicles[index]);
        },
      ),
    );
  }
}

class _VehicleCard extends StatelessWidget {
  final VehicleResponse vehicle;
  const _VehicleCard({required this.vehicle});

  @override
  Widget build(BuildContext context) {
    final imageUrl = (vehicle.images != null && vehicle.images!.isNotEmpty)
        ? vehicle.images!.first.imageUrl
        : 'https://via.placeholder.com/150';

    return GestureDetector(
      onTap: () => context.push('/vehicles/${vehicle.id}'),
      child: Card(
        clipBehavior: Clip.antiAlias,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: CachedNetworkImage(
                imageUrl: imageUrl,
                fit: BoxFit.cover,
                width: double.infinity,
                placeholder: (context, url) => Container(color: Colors.grey[300]),
                errorWidget: (context, url, error) => const Icon(Icons.error),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${vehicle.brand} ${vehicle.model}',
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(
                    '${vehicle.dailyPrice} ${vehicle.currency}/day',
                    style: TextStyle(color: Theme.of(context).primaryColor),
                  ),
                  Text(
                    vehicle.pickupCity ?? '',
                    style: const TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _FilterChip extends StatelessWidget {
  final String label;
  final bool selected;
  final List<String> options;
  final ValueChanged<String> onSelected;
  final VoidCallback onClear;
  const _FilterChip({
    required this.label,
    required this.selected,
    required this.options,
    required this.onSelected,
    required this.onClear,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () async {
        if (selected) {
          onClear();
          return;
        }
        final result = await showModalBottomSheet<String>(
          context: context,
          builder: (ctx) => ListView(
            shrinkWrap: true,
            children: options
                .map((o) => ListTile(title: Text(o), onTap: () => Navigator.pop(ctx, o)))
                .toList(),
          ),
        );
        if (result != null) onSelected(result);
      },
      child: Chip(
        label: Text(label, style: TextStyle(fontSize: 13, color: selected ? Colors.white : null)),
        backgroundColor: selected ? Theme.of(context).primaryColor : null,
        deleteIcon: selected ? const Icon(Icons.close, size: 16) : null,
        onDeleted: selected ? onClear : null,
      ),
    );
  }
}
