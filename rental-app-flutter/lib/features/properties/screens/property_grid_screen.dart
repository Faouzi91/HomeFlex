import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/property_provider.dart';
import '../models/property_dto.dart';
import '../../favorites/providers/favorite_provider.dart';
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
  bool _initFiltersFromQuery = false;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_initFiltersFromQuery) return;
    _initFiltersFromQuery = true;
    final uri = GoRouterState.of(context).uri;
    final qp = uri.queryParameters;
    if (qp.isEmpty) return;
    _selectedCity = qp['city'];
    _selectedType = qp['propertyType']?.toUpperCase();
    if (qp['q'] != null) _searchController.text = qp['q']!;
    if (qp['minPrice'] != null && qp['maxPrice'] != null) {
      _priceRange = RangeValues(
        double.tryParse(qp['minPrice']!) ?? 0,
        double.tryParse(qp['maxPrice']!) ?? 1000000,
      );
    }
    _minBedrooms = int.tryParse(qp['minBedrooms'] ?? '');
    _minBathrooms = int.tryParse(qp['minBathrooms'] ?? '');
    WidgetsBinding.instance.addPostFrameCallback((_) => _applyFilters());
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 300) {
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
    ref
        .read(paginatedPropertiesProvider.notifier)
        .loadFirstPage(filters: filters);
  }

  int _crossAxisCount(double width) {
    if (width >= 1400) return 4;
    if (width >= 1024) return 3;
    if (width >= 640) return 2;
    return 1;
  }

  @override
  Widget build(BuildContext context) {
    final paginatedState = ref.watch(paginatedPropertiesProvider);
    final cs = Theme.of(context).colorScheme;
    final width = MediaQuery.sizeOf(context).width;
    final isWide = width >= 800;

    return Scaffold(
      backgroundColor: cs.surface,
      body: SafeArea(
        child: Column(
          children: [
            // Sticky header
            Container(
              decoration: BoxDecoration(
                color: cs.surface,
                border: Border(bottom: BorderSide(color: cs.outlineVariant)),
              ),
              padding: EdgeInsets.symmetric(
                horizontal: isWide ? 32 : 16,
                vertical: 16,
              ),
              child: Column(
                children: [
                  Row(
                    children: [
                      Expanded(child: _searchField()),
                      const SizedBox(width: 12),
                      _IconBtn(icon: Icons.tune, onTap: _openAdvancedFilters),
                    ],
                  ),
                  const SizedBox(height: 12),
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: [
                        _Chip(
                          label: _selectedType ?? 'Any type',
                          icon: Icons.home_outlined,
                          selected: _selectedType != null,
                          onTap: () => _pickFromList(
                            'Property type',
                            const [
                              'APARTMENT',
                              'HOUSE',
                              'VILLA',
                              'STUDIO',
                              'ROOM',
                            ],
                            (v) => setState(() {
                              _selectedType = v;
                              _applyFilters();
                            }),
                          ),
                          onClear: _selectedType == null
                              ? null
                              : () => setState(() {
                                  _selectedType = null;
                                  _applyFilters();
                                }),
                        ),
                        const SizedBox(width: 8),
                        _Chip(
                          label: _selectedCity ?? 'Anywhere',
                          icon: Icons.place_outlined,
                          selected: _selectedCity != null,
                          onTap: () => _pickFromList(
                            'City',
                            const [
                              'Douala',
                              'Yaoundé',
                              'Limbe',
                              'Bamenda',
                              'Bafoussam',
                              'Kribi',
                            ],
                            (v) => setState(() {
                              _selectedCity = v;
                              _applyFilters();
                            }),
                          ),
                          onClear: _selectedCity == null
                              ? null
                              : () => setState(() {
                                  _selectedCity = null;
                                  _applyFilters();
                                }),
                        ),
                        const SizedBox(width: 8),
                        _Chip(
                          label: _priceRange == null
                              ? 'Any price'
                              : '${_priceRange!.start.round()} – ${_priceRange!.end.round()}',
                          icon: Icons.payments_outlined,
                          selected: _priceRange != null,
                          onTap: _openAdvancedFilters,
                          onClear: _priceRange == null
                              ? null
                              : () => setState(() {
                                  _priceRange = null;
                                  _applyFilters();
                                }),
                        ),
                        const SizedBox(width: 8),
                        _Chip(
                          label: _minBedrooms == null
                              ? 'Beds'
                              : '${_minBedrooms!}+ beds',
                          icon: Icons.bed_outlined,
                          selected: _minBedrooms != null,
                          onTap: _openAdvancedFilters,
                          onClear: _minBedrooms == null
                              ? null
                              : () => setState(() {
                                  _minBedrooms = null;
                                  _applyFilters();
                                }),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            Expanded(child: _buildBody(paginatedState, isWide)),
          ],
        ),
      ),
    );
  }

  Widget _searchField() {
    final cs = Theme.of(context).colorScheme;
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.light
            ? Colors.white
            : cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: cs.outlineVariant),
      ),
      child: TextField(
        controller: _searchController,
        onSubmitted: (_) => _applyFilters(),
        decoration: InputDecoration(
          hintText: 'Search by city, neighborhood, title…',
          prefixIcon: Icon(Icons.search, color: cs.onSurfaceVariant),
          suffixIcon: _searchController.text.isEmpty
              ? null
              : IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () {
                    _searchController.clear();
                    _applyFilters();
                    setState(() {});
                  },
                ),
          border: InputBorder.none,
          enabledBorder: InputBorder.none,
          focusedBorder: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 8,
            vertical: 14,
          ),
        ),
      ),
    );
  }

  Future<void> _pickFromList(
    String title,
    List<String> options,
    ValueChanged<String> onPick,
  ) async {
    final result = await showModalBottomSheet<String>(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (ctx) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const SizedBox(height: 12),
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Theme.of(ctx).colorScheme.outlineVariant,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(height: 16),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(title, style: Theme.of(ctx).textTheme.titleLarge),
              ),
            ),
            const SizedBox(height: 8),
            ...options.map(
              (o) => ListTile(
                title: Text(o),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => Navigator.pop(ctx, o),
              ),
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
    if (result != null) onPick(result);
  }

  Future<void> _openAdvancedFilters() async {
    RangeValues range = _priceRange ?? const RangeValues(0, 1000000);
    int? beds = _minBedrooms;
    int? baths = _minBathrooms;
    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setLocal) => Padding(
          padding: EdgeInsets.fromLTRB(
            24,
            24,
            24,
            MediaQuery.of(ctx).viewInsets.bottom + 24,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Theme.of(ctx).colorScheme.outlineVariant,
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Text('Filters', style: Theme.of(ctx).textTheme.headlineSmall),
              const SizedBox(height: 24),
              Text('Price range', style: Theme.of(ctx).textTheme.titleMedium),
              const SizedBox(height: 4),
              Text(
                '${range.start.round()} – ${range.end.round()} XAF',
                style: TextStyle(
                  color: Theme.of(ctx).colorScheme.onSurfaceVariant,
                ),
              ),
              RangeSlider(
                values: range,
                min: 0,
                max: 1000000,
                divisions: 100,
                labels: RangeLabels(
                  range.start.round().toString(),
                  range.end.round().toString(),
                ),
                onChanged: (v) => setLocal(() => range = v),
              ),
              const SizedBox(height: 16),
              Text('Bedrooms', style: Theme.of(ctx).textTheme.titleMedium),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                children: [null, 1, 2, 3, 4, 5]
                    .map(
                      (n) => ChoiceChip(
                        label: Text(n == null ? 'Any' : '$n+'),
                        selected: beds == n,
                        onSelected: (_) => setLocal(() => beds = n),
                      ),
                    )
                    .toList(),
              ),
              const SizedBox(height: 16),
              Text('Bathrooms', style: Theme.of(ctx).textTheme.titleMedium),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                children: [null, 1, 2, 3, 4]
                    .map(
                      (n) => ChoiceChip(
                        label: Text(n == null ? 'Any' : '$n+'),
                        selected: baths == n,
                        onSelected: (_) => setLocal(() => baths = n),
                      ),
                    )
                    .toList(),
              ),
              const SizedBox(height: 28),
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
                      child: const Text('Reset all'),
                    ),
                  ),
                  const SizedBox(width: 12),
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
                      child: const Text('Show results'),
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

  Widget _buildBody(PaginatedPropertyState state, bool isWide) {
    if (state.properties.isEmpty && state.isLoading) {
      return const Padding(padding: EdgeInsets.all(24), child: ShimmerGrid());
    }
    if (state.properties.isEmpty && state.error != null) {
      return ErrorRetry(message: state.error!, onRetry: _applyFilters);
    }
    if (state.properties.isEmpty) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.search_off,
              size: 64,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 16),
            Text(
              'No properties match your search',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            Text(
              'Try widening the filters',
              style: TextStyle(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      );
    }
    final width = MediaQuery.sizeOf(context).width;
    final cols = _crossAxisCount(width);
    return RefreshIndicator(
      onRefresh: () async => _applyFilters(),
      child: GridView.builder(
        controller: _scrollController,
        padding: EdgeInsets.fromLTRB(
          isWide ? 32 : 16,
          24,
          isWide ? 32 : 16,
          32,
        ),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: cols,
          childAspectRatio: 0.78,
          crossAxisSpacing: 24,
          mainAxisSpacing: 32,
        ),
        itemCount: state.properties.length + (state.hasMore ? cols : 0),
        itemBuilder: (context, index) {
          if (index >= state.properties.length) {
            return const ShimmerCard();
          }
          return _PropertyCard(property: state.properties[index]);
        },
      ),
    );
  }
}

class _IconBtn extends StatelessWidget {
  final IconData icon;
  final VoidCallback onTap;
  const _IconBtn({required this.icon, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(999),
        child: Container(
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: Theme.of(context).brightness == Brightness.light
                ? Colors.white
                : cs.surfaceContainerHighest,
            border: Border.all(color: cs.outlineVariant),
          ),
          child: Icon(icon, size: 20, color: cs.onSurface),
        ),
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  final String label;
  final IconData icon;
  final bool selected;
  final VoidCallback onTap;
  final VoidCallback? onClear;
  const _Chip({
    required this.label,
    required this.icon,
    required this.selected,
    required this.onTap,
    this.onClear,
  });

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(999),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(
            color: selected ? cs.primary : Colors.transparent,
            borderRadius: BorderRadius.circular(999),
            border: Border.all(
              color: selected ? cs.primary : cs.outlineVariant,
            ),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                icon,
                size: 16,
                color: selected ? cs.onPrimary : cs.onSurfaceVariant,
              ),
              const SizedBox(width: 6),
              Text(
                label,
                style: TextStyle(
                  color: selected ? cs.onPrimary : cs.onSurface,
                  fontWeight: FontWeight.w600,
                  fontSize: 13,
                ),
              ),
              if (selected && onClear != null) ...[
                const SizedBox(width: 6),
                GestureDetector(
                  onTap: onClear,
                  child: Icon(Icons.close, size: 14, color: cs.onPrimary),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

class _PropertyCard extends ConsumerWidget {
  final PropertyDto property;
  const _PropertyCard({required this.property});

  String _formatPrice(double price) {
    if (price >= 1000000) {
      return '${(price / 1000000).toStringAsFixed(1)}M';
    }
    if (price >= 1000) return '${(price / 1000).round()}k';
    return price.toStringAsFixed(0);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final cs = Theme.of(context).colorScheme;
    final theme = Theme.of(context);
    final favoriteIds = ref.watch(favoriteProvider).favoriteIds;
    final isFavorite = favoriteIds.contains(property.id);

    final imageUrl = (property.images != null && property.images!.isNotEmpty)
        ? property.images!.first.imageUrl
        : null;

    return InkWell(
      onTap: () => context.push('/properties/${property.id}'),
      borderRadius: BorderRadius.circular(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Image
          AspectRatio(
            aspectRatio: 4 / 3,
            child: Stack(
              fit: StackFit.expand,
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(20),
                  child: imageUrl == null
                      ? Container(
                          color: cs.surfaceContainerHighest,
                          child: Icon(
                            Icons.home_work,
                            size: 48,
                            color: cs.onSurfaceVariant,
                          ),
                        )
                      : CachedNetworkImage(
                          imageUrl: imageUrl,
                          fit: BoxFit.cover,
                          placeholder: (_, _) =>
                              Container(color: cs.surfaceContainerHighest),
                          errorWidget: (_, _, _) => Container(
                            color: cs.surfaceContainerHighest,
                            child: Icon(
                              Icons.image_not_supported,
                              color: cs.onSurfaceVariant,
                            ),
                          ),
                        ),
                ),
                // Heart
                Positioned(
                  top: 12,
                  right: 12,
                  child: Material(
                    color: Colors.transparent,
                    child: InkWell(
                      onTap: () => ref
                          .read(favoriteProvider.notifier)
                          .toggleFavorite(property.id),
                      borderRadius: BorderRadius.circular(999),
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: Colors.white,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withValues(alpha: 0.15),
                              blurRadius: 8,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: Icon(
                          isFavorite ? Icons.favorite : Icons.favorite_border,
                          size: 18,
                          color: isFavorite ? cs.secondary : Colors.black87,
                        ),
                      ),
                    ),
                  ),
                ),
                // Type pill
                Positioned(
                  top: 12,
                  left: 12,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 10,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.white.withValues(alpha: 0.95),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Text(
                      property.propertyType,
                      style: const TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.w700,
                        letterSpacing: 0.3,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 14),
          // Title + location
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Text(
                    property.title,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w700,
                      letterSpacing: -0.2,
                    ),
                  ),
                ),
                if ((property.viewCount ?? 0) > 0) ...[
                  const SizedBox(width: 6),
                  Icon(Icons.star, size: 14, color: cs.tertiary),
                  const SizedBox(width: 2),
                  Text(
                    '4.9',
                    style: theme.textTheme.bodySmall?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ],
            ),
          ),
          const SizedBox(height: 4),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: Text(
              '${property.city}, ${property.country}',
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(color: cs.onSurfaceVariant, fontSize: 13),
            ),
          ),
          const SizedBox(height: 6),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: Wrap(
              spacing: 10,
              children: [
                if (property.bedrooms != null)
                  _Meta(Icons.bed_outlined, '${property.bedrooms}'),
                if (property.bathrooms != null)
                  _Meta(Icons.bathtub_outlined, '${property.bathrooms}'),
                if (property.areaSqm != null)
                  _Meta(Icons.square_foot, '${property.areaSqm!.round()} m²'),
              ],
            ),
          ),
          const SizedBox(height: 8),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: RichText(
              text: TextSpan(
                children: [
                  TextSpan(
                    text:
                        '${_formatPrice(property.price)} ${property.currency}',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w800,
                      color: cs.onSurface,
                      letterSpacing: -0.3,
                    ),
                  ),
                  TextSpan(
                    text: ' / month',
                    style: TextStyle(color: cs.onSurfaceVariant, fontSize: 13),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _Meta extends StatelessWidget {
  final IconData icon;
  final String text;
  const _Meta(this.icon, this.text);

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 14, color: cs.onSurfaceVariant),
        const SizedBox(width: 4),
        Text(
          text,
          style: TextStyle(
            color: cs.onSurfaceVariant,
            fontSize: 12,
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }
}
