import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../providers/property_provider.dart';
import '../models/property_dto.dart';

class MyPropertiesScreen extends ConsumerWidget {
  const MyPropertiesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final propertiesAsync = ref.watch(myPropertiesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('My Properties')),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/properties/create'),
        child: const Icon(Icons.add),
      ),
      body: propertiesAsync.when(
        data: (properties) {
          if (properties.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.home_work_outlined, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('No properties yet', style: TextStyle(color: Colors.grey)),
                  SizedBox(height: 8),
                  Text('Tap + to add your first property', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }
          return ListView.builder(
            padding: const EdgeInsets.all(8),
            itemCount: properties.length,
            itemBuilder: (context, index) {
              final property = properties[index];
              return _MyPropertyCard(property: property);
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}

class _MyPropertyCard extends ConsumerWidget {
  final PropertyDto property;
  const _MyPropertyCard({required this.property});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final imageUrl = (property.images != null && property.images!.isNotEmpty)
        ? property.images!.first.imageUrl
        : null;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => context.push('/properties/${property.id}'),
        child: Row(
          children: [
            SizedBox(
              width: 120,
              height: 100,
              child: imageUrl != null
                  ? CachedNetworkImage(imageUrl: imageUrl, fit: BoxFit.cover)
                  : Container(color: Colors.grey[300], child: const Icon(Icons.home)),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(property.title,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text('${property.price} ${property.currency}',
                        style: TextStyle(color: Theme.of(context).primaryColor)),
                    const SizedBox(height: 4),
                    _statusChip(property.status),
                  ],
                ),
              ),
            ),
            PopupMenuButton<String>(
              onSelected: (action) async {
                if (action == 'edit') {
                  context.push('/properties/${property.id}/edit');
                } else if (action == 'delete') {
                  final confirmed = await showDialog<bool>(
                    context: context,
                    builder: (ctx) => AlertDialog(
                      title: const Text('Delete Property'),
                      content: const Text('Are you sure?'),
                      actions: [
                        TextButton(onPressed: () => ctx.pop(false), child: const Text('Cancel')),
                        TextButton(onPressed: () => ctx.pop(true), child: const Text('Delete')),
                      ],
                    ),
                  );
                  if (confirmed == true) {
                    await ref.read(propertyNotifierProvider).deleteProperty(property.id);
                    ref.invalidate(myPropertiesProvider);
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

  Widget _statusChip(String status) {
    Color color;
    switch (status.toUpperCase()) {
      case 'APPROVED':
        color = Colors.green;
        break;
      case 'REJECTED':
        color = Colors.red;
        break;
      case 'INACTIVE':
        color = Colors.grey;
        break;
      default:
        color = Colors.orange;
    }
    return Chip(
      label: Text(status, style: TextStyle(color: color, fontSize: 11)),
      backgroundColor: color.withOpacity(0.1),
      side: BorderSide.none,
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}
