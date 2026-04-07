import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/property_provider.dart';
import '../models/property_dto.dart';
import '../../favorites/providers/favorite_provider.dart';
import '../../notifications/providers/notification_provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../shared/widgets/shimmer_card.dart';
import '../../../shared/widgets/error_retry.dart';

class PropertyGridScreen extends ConsumerStatefulWidget {
  const PropertyGridScreen({super.key});

  @override
  ConsumerState<PropertyGridScreen> createState() => _PropertyGridScreenState();
}

class _PropertyGridScreenState extends ConsumerState<PropertyGridScreen> {
  final _searchController = TextEditingController();
  final _scrollController = ScrollController();
  String? _selectedCity;
  String? _selectedType;
  RangeValues? _priceRange;
  int? _minBedrooms;
  int? _minBathrooms;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(paginatedPropertiesProvider.notifier).loadNextPage();
    }
  }

  void _applyFilters() {
    final filters = <String, dynamic>{
      if (_searchController.text.isNotEmpty) 'q': _searchController.text,
      if (_selectedCity != null) 'city': _selectedCity,
      if (_selectedType != null) 'propertyType': _selectedType,
      if (_priceRange != null) 'minPrice': _priceRange!.start.round(),
      if (_priceRange != null) 'maxPrice': _priceRange!.end.round(),
      if (_minBedrooms != null) 'minBedrooms': _minBedrooms,
      if (_minBathrooms != null) 'minBathrooms': _minBathrooms,
    };
    ref.read(paginatedPropertiesProvider.notifier).loadFirstPage(filters: filters);
  }

  @override
  Widget build(BuildContext context) {
    final paginatedState = ref.watch(paginatedPropertiesProvider);
    final unreadCount = ref.watch(unreadCountProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Properties'),
        actions: [
          IconButton(
            icon: Badge(
              isLabelVisible: unreadCount > 0,
              label: Text('$unreadCount'),
              child: const Icon(Icons.notifications_outlined),
            ),
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(8, 8, 8, 0),
            child: SearchBar(
              controller: _searchController,
              hintText: 'Search properties...',
              leading: const Icon(Icons.search),
              trailing: [
                if (_searchController.text.isNotEmpty)
                  IconButton(
                    icon: const Icon(Icons.clear),
                    onPressed: () {
                      _searchController.clear();
                      setState(() {
                        _selectedCity = null;
                        _selectedType = null;
                      });
                      _applyFilters();
                    },
                  ),
              ],
              onSubmitted: (_) => _applyFilters(),
            ),
          ),
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.all(8),
            child: Row(
              children: [
                _FilterChip(
                  label: _selectedType ?? 'Type',
                  selected: _selectedType != null,
                  options: const ['APARTMENT', 'HOUSE', 'VILLA', 'STUDIO', 'ROOM'],
                  onSelected: (v) {
                    setState(() => _selectedType = v);
                    _applyFilters();
                  },
                  onClear: () {
                    setState(() => _selectedType = null);
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
                const SizedBox(width: 8),
                ActionChip(
                  avatar: const Icon(Icons.tune, size: 18),
                  label: const Text('More'),
                  onPressed: _openAdvancedFilters,
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

  Future<void> _openAdvancedFilters() async {
    RangeValues range = _priceRange ?? const RangeValues(0, 1000000);
    int? beds = _minBedrooms;
    int? baths = _minBathrooms;
    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setLocal) => Padding(
          padding: EdgeInsets.fromLTRB(
              16, 16, 16, MediaQuery.of(ctx).viewInsets.bottom + 16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Advanced Filters',
                  style: Theme.of(ctx).textTheme.titleLarge),
              const SizedBox(height: 16),
              Text('Price: ${range.start.round()} – ${range.end.round()}'),
              RangeSlider(
                values: range,
                min: 0,
                max: 1000000,
                divisions: 100,
                labels: RangeLabels(
                    range.start.round().toString(), range.end.round().toString()),
                onChanged: (v) => setLocal(() => range = v),
              ),
              const SizedBox(height: 8),
              const Text('Min Bedrooms'),
              Wrap(
                spacing: 8,
                children: [null, 1, 2, 3, 4, 5]
                    .map((n) => ChoiceChip(
                          label: Text(n == null ? 'Any' : '$n+'),
                          selected: beds == n,
                          onSelected: (_) => setLocal(() => beds = n),
                        ))
                    .toList(),
              ),
              const SizedBox(height: 8),
              const Text('Min Bathrooms'),
              Wrap(
                spacing: 8,
                children: [null, 1, 2, 3, 4]
                    .map((n) => ChoiceChip(
                          label: Text(n == null ? 'Any' : '$n+'),
                          selected: baths == n,
                          onSelected: (_) => setLocal(() => baths = n),
                        ))
                    .toList(),
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () {
                        setState(() {
                          _priceRange = null;
                          _minBedrooms = null;
                          _minBathrooms = null;
                        });
                        Navigator.pop(ctx);
                        _applyFilters();
                      },
                      child: const Text('Reset'),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: FilledButton(
                      onPressed: () {
                        setState(() {
                          _priceRange = range;
                          _minBedrooms = beds;
                          _minBathrooms = baths;
                        });
                        Navigator.pop(ctx);
                        _applyFilters();
                      },
                      child: const Text('Apply'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBody(PaginatedPropertyState state) {
    if (state.properties.isEmpty && state.isLoading) {
      return const ShimmerGrid();
    }
    if (state.properties.isEmpty && state.error != null) {
      return ErrorRetry(message: state.error!, onRetry: _applyFilters);
    }
    if (state.properties.isEmpty) {
      return const Center(
        child: Text('No properties found', style: TextStyle(color: Colors.grey)),
      );
    }
    return RefreshIndicator(
      onRefresh: () async {
        _applyFilters();
      },
      child: GridView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.all(8),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 10,
          mainAxisSpacing: 10,
        ),
        itemCount: state.properties.length + (state.hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index >= state.properties.length) {
            return const Center(child: CircularProgressIndicator());
          }
          return _PropertyCard(property: state.properties[index]);
        },
      ),
    );
  }
}

class _PropertyCard extends ConsumerWidget {
  final PropertyDto property;
  const _PropertyCard({required this.property});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final favoriteIds = ref.watch(favoriteProvider).favoriteIds;
    final isFavorite = favoriteIds.contains(property.id);

    final imageUrl = (property.images != null && property.images!.isNotEmpty)
        ? property.images!.first.imageUrl
        : 'https://via.placeholder.com/150';

    return GestureDetector(
      onTap: () => context.push('/properties/${property.id}'),
      child: Card(
        clipBehavior: Clip.antiAlias,
        child: Stack(
          children: [
            Column(
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
                        property.title,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      Text(
                        '${property.price} ${property.currency}',
                        style: TextStyle(color: Theme.of(context).primaryColor),
                      ),
                      Text(
                        '${property.city}, ${property.country}',
                        style: const TextStyle(fontSize: 12, color: Colors.grey),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            Positioned(
              top: 8,
              right: 8,
              child: GestureDetector(
                onTap: () => ref.read(favoriteProvider.notifier).toggleFavorite(property.id),
                child: CircleAvatar(
                  radius: 16,
                  backgroundColor: Colors.white.withOpacity(0.9),
                  child: Icon(
                    isFavorite ? Icons.favorite : Icons.favorite_border,
                    color: isFavorite ? Colors.red : Colors.grey,
                    size: 18,
                  ),
                ),
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
                .map((o) => ListTile(
                      title: Text(o),
                      onTap: () => Navigator.pop(ctx, o),
                    ))
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
