import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../shared/widgets/image_gallery.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import '../providers/property_provider.dart';
import '../../auth/providers/auth_provider.dart';
import '../../favorites/providers/favorite_provider.dart';
import '../../reviews/providers/review_provider.dart';
import '../../reviews/models/review_dto.dart';
import '../../chat/providers/chat_provider.dart';

class PropertyDetailScreen extends ConsumerStatefulWidget {
  final String id;
  const PropertyDetailScreen({super.key, required this.id});

  @override
  ConsumerState<PropertyDetailScreen> createState() => _PropertyDetailScreenState();
}

class _PropertyDetailScreenState extends ConsumerState<PropertyDetailScreen> {
  @override
  void initState() {
    super.initState();
    // Record view and check favorite status
    Future.microtask(() {
      ref.read(propertyNotifierProvider).recordView(widget.id);
      ref.read(favoriteProvider.notifier).checkFavorite(widget.id);
    });
  }

  @override
  Widget build(BuildContext context) {
    final propertyAsync = ref.watch(propertyDetailProvider(widget.id));
    final favoriteIds = ref.watch(favoriteProvider).favoriteIds;
    final isFavorite = favoriteIds.contains(widget.id);
    final currentUser = ref.watch(authProvider).user;
    final isTenant = currentUser?.role == 'TENANT';

    return Scaffold(
      appBar: AppBar(
        title: const Text('Property Details'),
        actions: [
          IconButton(
            icon: Icon(isFavorite ? Icons.favorite : Icons.favorite_border,
                color: isFavorite ? Colors.red : null),
            onPressed: () => ref.read(favoriteProvider.notifier).toggleFavorite(widget.id),
          ),
          PopupMenuButton<String>(
            onSelected: (action) {
              if (action == 'report') _showReportDialog();
            },
            itemBuilder: (_) => const [
              PopupMenuItem(value: 'report', child: Text('Report listing')),
            ],
          ),
        ],
      ),
      floatingActionButton: isTenant
          ? FloatingActionButton.extended(
              onPressed: () => context.push('/properties/${widget.id}/book'),
              icon: const Icon(Icons.calendar_today),
              label: const Text('Book Now'),
            )
          : (currentUser?.role == 'LANDLORD' || currentUser?.role == 'ADMIN')
              ? FloatingActionButton.extended(
                  onPressed: () => context.push('/properties/${widget.id}/bookings'),
                  icon: const Icon(Icons.list_alt),
                  label: const Text('View Bookings'),
                )
              : null,
      body: propertyAsync.when(
        data: (property) => SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Image carousel
              if (property.images != null && property.images!.isNotEmpty)
                SizedBox(
                  height: 250,
                  child: PageView.builder(
                    itemCount: property.images!.length,
                    itemBuilder: (context, index) => GestureDetector(
                      onTap: () => FullScreenImageGallery.show(
                        context,
                        property.images!.map((i) => i.imageUrl).toList(),
                        initialIndex: index,
                      ),
                      child: CachedNetworkImage(
                        imageUrl: property.images![index].imageUrl,
                        fit: BoxFit.cover,
                        width: double.infinity,
                      ),
                    ),
                  ),
                ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(property.title, style: Theme.of(context).textTheme.headlineMedium),
                    Text('${property.price} ${property.currency}',
                        style: TextStyle(
                            fontSize: 20,
                            color: Theme.of(context).primaryColor,
                            fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        const Icon(Icons.location_on, size: 16, color: Colors.grey),
                        const SizedBox(width: 4),
                        Expanded(
                          child: Text('${property.address}, ${property.city}',
                              style: const TextStyle(color: Colors.grey)),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      children: [
                        Chip(label: Text(property.propertyType), visualDensity: VisualDensity.compact),
                        Chip(label: Text(property.listingType), visualDensity: VisualDensity.compact),
                        if (property.isAvailable)
                          const Chip(
                            label: Text('Available', style: TextStyle(color: Colors.green)),
                            visualDensity: VisualDensity.compact,
                          ),
                      ],
                    ),
                    const Divider(height: 32),

                    // Description
                    const Text('Description', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    Text(property.description),
                    const SizedBox(height: 16),

                    // Specs
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        _InfoIcon(icon: Icons.king_bed, label: '${property.bedrooms ?? 0} Beds'),
                        _InfoIcon(icon: Icons.bathtub, label: '${property.bathrooms ?? 0} Baths'),
                        _InfoIcon(icon: Icons.square_foot, label: '${property.areaSqm ?? 0} m²'),
                        if (property.floorNumber != null)
                          _InfoIcon(icon: Icons.layers, label: 'Floor ${property.floorNumber}'),
                      ],
                    ),

                    // Amenities
                    if (property.amenities != null && property.amenities!.isNotEmpty) ...[
                      const Divider(height: 32),
                      const Text('Amenities', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      Wrap(
                        spacing: 8,
                        runSpacing: 4,
                        children: property.amenities!
                            .map((a) => Chip(
                                  avatar: a.icon != null ? Icon(Icons.check, size: 16) : null,
                                  label: Text(a.name),
                                  visualDensity: VisualDensity.compact,
                                ))
                            .toList(),
                      ),
                    ],

                    // Landlord info
                    if (property.landlord != null) ...[
                      const Divider(height: 32),
                      const Text('Landlord', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: CircleAvatar(
                          backgroundImage: property.landlord!.profilePictureUrl != null
                              ? NetworkImage(property.landlord!.profilePictureUrl!)
                              : null,
                          child: property.landlord!.profilePictureUrl == null
                              ? Text('${property.landlord!.firstName[0]}${property.landlord!.lastName[0]}')
                              : null,
                        ),
                        title: Text('${property.landlord!.firstName} ${property.landlord!.lastName}'),
                        subtitle: Text(property.landlord!.email),
                        trailing: isTenant
                            ? IconButton(
                                icon: const Icon(Icons.chat),
                                onPressed: () => _contactLandlord(property.landlord!.id),
                              )
                            : null,
                      ),
                    ],

                    // Reviews section
                    const Divider(height: 32),
                    _ReviewsSection(propertyId: widget.id),

                    // Similar properties
                    const Divider(height: 32),
                    _SimilarPropertiesSection(propertyId: widget.id),

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

  void _showReportDialog() {
    String? selectedReason;
    final descriptionController = TextEditingController();
    final reasons = [
      'Misleading information',
      'Inappropriate content',
      'Suspected fraud',
      'Duplicate listing',
      'Other',
    ];

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: const Text('Report Listing'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButtonFormField<String>(
                decoration: const InputDecoration(
                  labelText: 'Reason',
                  border: OutlineInputBorder(),
                ),
                items: reasons
                    .map((r) => DropdownMenuItem(value: r, child: Text(r)))
                    .toList(),
                onChanged: (v) => setDialogState(() => selectedReason = v),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: descriptionController,
                decoration: const InputDecoration(
                  labelText: 'Details (optional)',
                  border: OutlineInputBorder(),
                ),
                maxLines: 3,
              ),
            ],
          ),
          actions: [
            TextButton(
                onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
            FilledButton(
              onPressed: selectedReason == null
                  ? null
                  : () async {
                      Navigator.pop(ctx);
                      try {
                        await ref.read(propertyNotifierProvider).reportProperty(
                              widget.id,
                              reason: selectedReason!,
                              description: descriptionController.text.isNotEmpty
                                  ? descriptionController.text
                                  : null,
                            );
                        if (mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Report submitted. Thank you.')),
                          );
                        }
                      } catch (e) {
                        if (mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Error: $e')),
                          );
                        }
                      }
                    },
              child: const Text('Submit Report'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _contactLandlord(String landlordId) async {
    final currentUser = ref.read(authProvider).user;
    if (currentUser == null) return;
    try {
      final room = await ref.read(chatNotifierProvider).createRoom(
            propertyId: widget.id,
            tenantId: currentUser.id,
            landlordId: landlordId,
          );
      if (mounted) context.push('/chat/${room.id}');
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
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

class _ReviewsSection extends ConsumerStatefulWidget {
  final String propertyId;
  const _ReviewsSection({required this.propertyId});

  @override
  ConsumerState<_ReviewsSection> createState() => _ReviewsSectionState();
}

class _ReviewsSectionState extends ConsumerState<_ReviewsSection> {
  @override
  Widget build(BuildContext context) {
    final reviewsAsync = ref.watch(propertyReviewsProvider(widget.propertyId));
    final ratingAsync = ref.watch(propertyAverageRatingProvider(widget.propertyId));
    final currentUser = ref.watch(authProvider).user;
    final isTenant = currentUser?.role == 'TENANT';

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                const Text('Reviews', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(width: 8),
                ratingAsync.whenOrNull(
                      data: (rating) => Row(
                        children: [
                          const Icon(Icons.star, color: Colors.amber, size: 18),
                          Text(' ${rating.toStringAsFixed(1)}',
                              style: const TextStyle(fontWeight: FontWeight.bold)),
                        ],
                      ),
                    ) ??
                    const SizedBox.shrink(),
              ],
            ),
            if (isTenant)
              TextButton.icon(
                onPressed: () => _showReviewForm(context),
                icon: const Icon(Icons.rate_review, size: 18),
                label: const Text('Write Review'),
              ),
          ],
        ),
        const SizedBox(height: 8),
        reviewsAsync.when(
          data: (reviews) {
            if (reviews.isEmpty) {
              return const Text('No reviews yet', style: TextStyle(color: Colors.grey));
            }
            return Column(
              children: reviews.take(5).map((review) => _ReviewCard(review: review)).toList(),
            );
          },
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (err, stack) => Text('Error: $err'),
        ),
      ],
    );
  }

  void _showReviewForm(BuildContext context) {
    double rating = 3;
    final commentController = TextEditingController();

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => Padding(
        padding: EdgeInsets.fromLTRB(24, 24, 24, MediaQuery.of(ctx).viewInsets.bottom + 24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Text('Write a Review', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            Center(
              child: RatingBar.builder(
                initialRating: rating,
                minRating: 1,
                itemCount: 5,
                itemSize: 36,
                itemBuilder: (context, _) => const Icon(Icons.star, color: Colors.amber),
                onRatingUpdate: (v) => rating = v,
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: commentController,
              decoration: const InputDecoration(
                hintText: 'Your review...',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: () async {
                try {
                  await ref.read(reviewNotifierProvider).submitReview(
                        propertyId: widget.propertyId,
                        rating: rating.toInt(),
                        comment: commentController.text.isNotEmpty ? commentController.text : null,
                      );
                  ref.invalidate(propertyReviewsProvider(widget.propertyId));
                  ref.invalidate(propertyAverageRatingProvider(widget.propertyId));
                  if (ctx.mounted) Navigator.pop(ctx);
                  if (mounted) {
                    ScaffoldMessenger.of(context)
                        .showSnackBar(const SnackBar(content: Text('Review submitted!')));
                  }
                } catch (e) {
                  if (ctx.mounted) {
                    ScaffoldMessenger.of(context)
                        .showSnackBar(SnackBar(content: Text('Error: $e')));
                  }
                }
              },
              child: const Text('Submit Review'),
            ),
          ],
        ),
      ),
    );
    // Dispose after bottom sheet closes
    // commentController is disposed when the widget tree is removed
  }
}

class _ReviewCard extends StatelessWidget {
  final ReviewDto review;
  const _ReviewCard({required this.review});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 18,
            child: Text(
              review.reviewer != null
                  ? '${review.reviewer!.firstName[0]}${review.reviewer!.lastName[0]}'
                  : '?',
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      review.reviewer != null
                          ? '${review.reviewer!.firstName} ${review.reviewer!.lastName}'
                          : 'Anonymous',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    const Spacer(),
                    ...List.generate(
                      5,
                      (i) => Icon(
                        i < review.rating ? Icons.star : Icons.star_border,
                        size: 14,
                        color: Colors.amber,
                      ),
                    ),
                  ],
                ),
                if (review.comment != null && review.comment!.isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(review.comment!),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SimilarPropertiesSection extends ConsumerWidget {
  final String propertyId;
  const _SimilarPropertiesSection({required this.propertyId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final similarAsync = ref.watch(similarPropertiesProvider(propertyId));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Similar Properties', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        similarAsync.when(
          data: (properties) {
            if (properties.isEmpty) {
              return const Text('No similar properties', style: TextStyle(color: Colors.grey));
            }
            return SizedBox(
              height: 180,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: properties.length,
                itemBuilder: (context, index) {
                  final p = properties[index];
                  final imageUrl = (p.images != null && p.images!.isNotEmpty)
                      ? p.images!.first.imageUrl
                      : 'https://via.placeholder.com/150';
                  return GestureDetector(
                    onTap: () => context.push('/properties/${p.id}'),
                    child: SizedBox(
                      width: 160,
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
                              ),
                            ),
                            Padding(
                              padding: const EdgeInsets.all(8),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(p.title,
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                                  Text('${p.price} ${p.currency}',
                                      style: TextStyle(
                                          fontSize: 12, color: Theme.of(context).primaryColor)),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  );
                },
              ),
            );
          },
          loading: () => const SizedBox(height: 180, child: Center(child: CircularProgressIndicator())),
          error: (_, _) => const SizedBox.shrink(),
        ),
      ],
    );
  }
}
