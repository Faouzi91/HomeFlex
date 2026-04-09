import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../auth/providers/auth_provider.dart';

class LandingScreen extends ConsumerStatefulWidget {
  const LandingScreen({super.key});

  @override
  ConsumerState<LandingScreen> createState() => _LandingScreenState();
}

class _LandingScreenState extends ConsumerState<LandingScreen> {
  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).user;
    final width = MediaQuery.sizeOf(context).width;
    final isWide = width >= 900;
    final theme = Theme.of(context);
    final cs = theme.colorScheme;

    return Scaffold(
      backgroundColor: cs.surface,
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            elevation: 0,
            scrolledUnderElevation: 0.5,
            backgroundColor: cs.surface,
            surfaceTintColor: Colors.transparent,
            toolbarHeight: 72,
            title: InkWell(
              onTap: () => context.go('/'),
              borderRadius: BorderRadius.circular(8),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: cs.primary,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: const Icon(Icons.home_work,
                        color: Colors.white, size: 20),
                  ),
                  const SizedBox(width: 12),
                  Text('HomeFlex',
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.5,
                      )),
                ],
              ),
            ),
            actions: [
              if (isWide) ...[
                _NavLink('Stays', () => context.go('/properties')),
                _NavLink('Vehicles', () => context.go('/vehicles')),
                _NavLink('How it works', () {}),
                const SizedBox(width: 8),
              ],
              if (user == null) ...[
                TextButton(
                  onPressed: () => context.push('/login'),
                  child: const Text('Sign in'),
                ),
                const SizedBox(width: 8),
                FilledButton(
                  onPressed: () => context.push('/register'),
                  child: const Text('Get started'),
                ),
              ] else
                FilledButton.icon(
                  onPressed: () => context.go('/properties'),
                  icon: const Icon(Icons.dashboard, size: 18),
                  label: const Text('Open app'),
                ),
              const SizedBox(width: 24),
            ],
          ),
          SliverToBoxAdapter(
            child: Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 1240),
                child: Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: isWide ? 48 : 20,
                    vertical: isWide ? 64 : 32,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _Hero(isWide: isWide),
                      SizedBox(height: isWide ? 96 : 64),
                      _SectionLabel('Browse by category'),
                      const SizedBox(height: 24),
                      const _Categories(),
                      SizedBox(height: isWide ? 96 : 64),
                      _SectionLabel('Why HomeFlex'),
                      const SizedBox(height: 24),
                      const _Features(),
                      SizedBox(height: isWide ? 96 : 64),
                      const _Stats(),
                      SizedBox(height: isWide ? 96 : 64),
                      _CtaBanner(isWide: isWide),
                      const SizedBox(height: 64),
                      _Footer(),
                      const SizedBox(height: 32),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _NavLink extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  const _NavLink(this.label, this.onTap);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 4),
      child: TextButton(
        onPressed: onTap,
        style: TextButton.styleFrom(
          foregroundColor: Theme.of(context).colorScheme.onSurface,
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
        child: Text(label),
      ),
    );
  }
}

class _Hero extends StatelessWidget {
  final bool isWide;
  const _Hero({required this.isWide});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final cs = theme.colorScheme;

    final text = Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
          decoration: BoxDecoration(
            color: cs.secondaryContainer,
            borderRadius: BorderRadius.circular(999),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.bolt, size: 16, color: cs.onSecondaryContainer),
              const SizedBox(width: 6),
              Text('Now with verified landlords',
                  style: TextStyle(
                    color: cs.onSecondaryContainer,
                    fontWeight: FontWeight.w600,
                    fontSize: 13,
                  )),
            ],
          ),
        ),
        const SizedBox(height: 24),
        Text(
          'Find your\nnext place.',
          style: (isWide
                  ? theme.textTheme.displayLarge
                  : theme.textTheme.displayMedium)
              ?.copyWith(
            fontWeight: FontWeight.w800,
            letterSpacing: -2,
            height: 1.0,
            fontSize: isWide ? 84 : 56,
          ),
        ),
        const SizedBox(height: 20),
        ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 520),
          child: Text(
            'Apartments, houses and cars from people you can trust. Secure payments, real-time chat, and verified hosts — everything you need to book with confidence.',
            style: theme.textTheme.titleMedium?.copyWith(
              color: cs.onSurfaceVariant,
              height: 1.5,
              fontSize: 17,
            ),
          ),
        ),
        const SizedBox(height: 32),
        const _SearchBar(),
        const SizedBox(height: 20),
        Wrap(
          spacing: 16,
          runSpacing: 8,
          children: [
            _Trust(Icons.verified_outlined, 'KYC verified hosts'),
            _Trust(Icons.lock_outline, 'Escrowed payments'),
            _Trust(Icons.support_agent, '24/7 support'),
          ],
        ),
      ],
    );

    if (!isWide) return text;

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(flex: 6, child: text),
        const SizedBox(width: 64),
        Expanded(flex: 5, child: const _HeroVisual()),
      ],
    );
  }
}

class _Trust extends StatelessWidget {
  final IconData icon;
  final String label;
  const _Trust(this.icon, this.label);

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 18, color: cs.primary),
        const SizedBox(width: 6),
        Text(label,
            style: TextStyle(
              color: cs.onSurfaceVariant,
              fontWeight: FontWeight.w600,
              fontSize: 13,
            )),
      ],
    );
  }
}

class _SearchBar extends StatefulWidget {
  const _SearchBar();
  @override
  State<_SearchBar> createState() => _SearchBarState();
}

class _SearchBarState extends State<_SearchBar> {
  final _whereCtrl = TextEditingController();

  @override
  void dispose() {
    _whereCtrl.dispose();
    super.dispose();
  }

  void _submit() {
    final qp = <String, String>{};
    if (_whereCtrl.text.trim().isNotEmpty) qp['city'] = _whereCtrl.text.trim();
    final uri = Uri(path: '/properties', queryParameters: qp.isEmpty ? null : qp);
    context.go(uri.toString());
  }

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.light
            ? Colors.white
            : cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: cs.outlineVariant),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.06),
            blurRadius: 24,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      padding: const EdgeInsets.all(8),
      child: Row(
        children: [
          Expanded(
            child: _SearchField(
              icon: Icons.place_outlined,
              label: 'Where',
              hint: 'Douala, Yaoundé…',
              controller: _whereCtrl,
              onSubmitted: (_) => _submit(),
            ),
          ),
          _Sep(),
          const Expanded(
            child: _SearchField(
              icon: Icons.calendar_today_outlined,
              label: 'When',
              hint: 'Any week',
            ),
          ),
          _Sep(),
          const Expanded(
            child: _SearchField(
              icon: Icons.person_outline,
              label: 'Guests',
              hint: 'Add guests',
            ),
          ),
          const SizedBox(width: 8),
          FilledButton.icon(
            onPressed: _submit,
            style: FilledButton.styleFrom(
              padding:
                  const EdgeInsets.symmetric(horizontal: 24, vertical: 18),
              shape: const StadiumBorder(),
            ),
            icon: const Icon(Icons.search, size: 20),
            label: const Text('Search'),
          ),
        ],
      ),
    );
  }
}

class _Sep extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      width: 1,
      height: 36,
      color: Theme.of(context).colorScheme.outlineVariant,
    );
  }
}

class _SearchField extends StatelessWidget {
  final IconData icon;
  final String label;
  final String hint;
  final TextEditingController? controller;
  final ValueChanged<String>? onSubmitted;
  const _SearchField({
    required this.icon,
    required this.label,
    required this.hint,
    this.controller,
    this.onSubmitted,
  });

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 18, color: cs.onSurfaceVariant),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(label,
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.w700,
                      letterSpacing: 0.5,
                      color: cs.onSurface,
                    )),
                if (controller != null)
                  TextField(
                    controller: controller,
                    onSubmitted: onSubmitted,
                    decoration: InputDecoration(
                      hintText: hint,
                      isDense: true,
                      contentPadding: EdgeInsets.zero,
                      border: InputBorder.none,
                      enabledBorder: InputBorder.none,
                      focusedBorder: InputBorder.none,
                      hintStyle: TextStyle(
                        fontSize: 14,
                        color: cs.onSurfaceVariant,
                      ),
                    ),
                    style: const TextStyle(fontSize: 14),
                  )
                else
                  Padding(
                    padding: const EdgeInsets.only(top: 2),
                    child: Text(hint,
                        style: TextStyle(
                          fontSize: 14,
                          color: cs.onSurfaceVariant,
                        )),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _HeroVisual extends StatelessWidget {
  const _HeroVisual();

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return AspectRatio(
      aspectRatio: 4 / 5,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          // Big card
          Positioned.fill(
            child: Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [cs.primary, cs.primary.withOpacity(0.7)],
                ),
                borderRadius: BorderRadius.circular(32),
                boxShadow: [
                  BoxShadow(
                    color: cs.primary.withOpacity(0.25),
                    blurRadius: 60,
                    offset: const Offset(0, 24),
                  ),
                ],
              ),
              child: Stack(
                children: [
                  Positioned(
                    right: -40,
                    bottom: -40,
                    child: Icon(Icons.home_work,
                        size: 320, color: Colors.white.withOpacity(0.12)),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(32),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 12, vertical: 6),
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(999),
                          ),
                          child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(Icons.star,
                                  size: 14, color: Colors.white),
                              SizedBox(width: 4),
                              Text('Featured',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontWeight: FontWeight.w700,
                                    fontSize: 12,
                                  )),
                            ],
                          ),
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Loft Marais',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: 28,
                                  fontWeight: FontWeight.w800,
                                  letterSpacing: -0.5,
                                )),
                            const SizedBox(height: 4),
                            Text('Paris, France',
                                style: TextStyle(
                                  color: Colors.white.withOpacity(0.85),
                                  fontSize: 15,
                                )),
                            const SizedBox(height: 16),
                            Row(
                              children: [
                                const Text('€180',
                                    style: TextStyle(
                                      color: Colors.white,
                                      fontSize: 24,
                                      fontWeight: FontWeight.w800,
                                    )),
                                Text(' /night',
                                    style: TextStyle(
                                      color: Colors.white.withOpacity(0.85),
                                      fontSize: 14,
                                    )),
                              ],
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          // Floating chip
          Positioned(
            left: -20,
            top: 60,
            child: _FloatingPill(
              icon: Icons.verified,
              text: 'KYC verified',
              color: cs.secondary,
            ),
          ),
          Positioned(
            right: -16,
            bottom: 80,
            child: _FloatingPill(
              icon: Icons.bolt,
              text: 'Instant book',
              color: cs.tertiary,
            ),
          ),
        ],
      ),
    );
  }
}

class _FloatingPill extends StatelessWidget {
  final IconData icon;
  final String text;
  final Color color;
  const _FloatingPill(
      {required this.icon, required this.text, required this.color});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(999),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.12),
            blurRadius: 24,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, color: color, size: 18),
          const SizedBox(width: 8),
          Text(text,
              style: const TextStyle(
                fontWeight: FontWeight.w700,
                fontSize: 13,
              )),
        ],
      ),
    );
  }
}

class _SectionLabel extends StatelessWidget {
  final String label;
  const _SectionLabel(this.label);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Text(
      label,
      style: theme.textTheme.headlineMedium?.copyWith(
        fontWeight: FontWeight.w800,
        letterSpacing: -0.8,
      ),
    );
  }
}

class _Categories extends StatelessWidget {
  const _Categories();

  static const _items = [
    (Icons.apartment, 'Apartments'),
    (Icons.house, 'Houses'),
    (Icons.villa, 'Villas'),
    (Icons.bed, 'Studios'),
    (Icons.directions_car, 'Cars'),
    (Icons.beach_access, 'Beach'),
  ];

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Wrap(
      spacing: 16,
      runSpacing: 16,
      children: [
        for (final (icon, label) in _items)
          InkWell(
            onTap: () => context.go('/properties'),
            borderRadius: BorderRadius.circular(20),
            child: Container(
              width: 140,
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Theme.of(context).brightness == Brightness.light
                    ? Colors.white
                    : cs.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: cs.outlineVariant),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      color: cs.primaryContainer,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Icon(icon, color: cs.primary, size: 22),
                  ),
                  const SizedBox(height: 16),
                  Text(label,
                      style: const TextStyle(
                        fontWeight: FontWeight.w700,
                        fontSize: 15,
                      )),
                ],
              ),
            ),
          ),
      ],
    );
  }
}

class _Features extends StatelessWidget {
  const _Features();

  @override
  Widget build(BuildContext context) {
    return const Wrap(
      spacing: 24,
      runSpacing: 24,
      children: [
        _FeatureCard(
          icon: Icons.verified_user,
          title: 'Verified hosts',
          description:
              'Every landlord goes through Stripe Identity KYC. No more guesswork.',
        ),
        _FeatureCard(
          icon: Icons.shield_outlined,
          title: 'Protected payments',
          description:
              'Your money is held in escrow via Stripe Connect until you check in.',
        ),
        _FeatureCard(
          icon: Icons.chat_bubble_outline,
          title: 'Real-time chat',
          description:
              'Talk directly with hosts over a secure WebSocket — no middlemen.',
        ),
        _FeatureCard(
          icon: Icons.directions_car,
          title: 'Cars too',
          description:
              'Need a ride? Rent vehicles by the day with transparent condition reports.',
        ),
      ],
    );
  }
}

class _FeatureCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String description;
  const _FeatureCard(
      {required this.icon, required this.title, required this.description});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final cs = theme.colorScheme;
    return SizedBox(
      width: 280,
      child: Container(
        padding: const EdgeInsets.all(28),
        decoration: BoxDecoration(
          color: theme.brightness == Brightness.light
              ? Colors.white
              : cs.surfaceContainerHighest,
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: cs.outlineVariant),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: cs.primaryContainer,
                borderRadius: BorderRadius.circular(14),
              ),
              child: Icon(icon, color: cs.primary, size: 24),
            ),
            const SizedBox(height: 20),
            Text(title,
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.w800,
                  letterSpacing: -0.4,
                )),
            const SizedBox(height: 8),
            Text(description,
                style: TextStyle(
                  color: cs.onSurfaceVariant,
                  fontSize: 14,
                  height: 1.5,
                )),
          ],
        ),
      ),
    );
  }
}

class _Stats extends StatelessWidget {
  const _Stats();

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 48),
      decoration: BoxDecoration(
        color: cs.inverseSurface,
        borderRadius: BorderRadius.circular(28),
      ),
      child: Wrap(
        spacing: 64,
        runSpacing: 32,
        alignment: WrapAlignment.spaceAround,
        children: [
          _Stat('12k+', 'Listings'),
          _Stat('98%', 'Verified hosts'),
          _Stat('50+', 'Cities'),
          _Stat('4.9★', 'Avg rating'),
        ],
      ),
    );
  }
}

class _Stat extends StatelessWidget {
  final String value;
  final String label;
  const _Stat(this.value, this.label);

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(value,
            style: TextStyle(
              color: cs.onInverseSurface,
              fontSize: 48,
              fontWeight: FontWeight.w800,
              letterSpacing: -1.5,
              height: 1,
            )),
        const SizedBox(height: 8),
        Text(label,
            style: TextStyle(
              color: cs.onInverseSurface.withOpacity(0.7),
              fontSize: 14,
              fontWeight: FontWeight.w500,
            )),
      ],
    );
  }
}

class _CtaBanner extends StatelessWidget {
  final bool isWide;
  const _CtaBanner({required this.isWide});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Container(
      width: double.infinity,
      padding: EdgeInsets.all(isWide ? 64 : 32),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [cs.secondary, cs.secondary.withOpacity(0.75)],
        ),
        borderRadius: BorderRadius.circular(32),
      ),
      child: Flex(
        direction: isWide ? Axis.horizontal : Axis.vertical,
        crossAxisAlignment:
            isWide ? CrossAxisAlignment.center : CrossAxisAlignment.start,
        children: [
          Expanded(
            flex: isWide ? 2 : 0,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('List your place.\nEarn on your terms.',
                    style: TextStyle(
                      color: cs.onSecondary,
                      fontSize: isWide ? 44 : 32,
                      fontWeight: FontWeight.w800,
                      letterSpacing: -1,
                      height: 1.05,
                    )),
                const SizedBox(height: 16),
                Text(
                  'Join thousands of hosts already earning extra income with HomeFlex.',
                  style: TextStyle(
                    color: cs.onSecondary.withOpacity(0.9),
                    fontSize: 16,
                  ),
                ),
              ],
            ),
          ),
          SizedBox(width: isWide ? 32 : 0, height: isWide ? 0 : 24),
          FilledButton.icon(
            onPressed: () => context.push('/register'),
            style: FilledButton.styleFrom(
              backgroundColor: Colors.white,
              foregroundColor: cs.secondary,
              padding:
                  const EdgeInsets.symmetric(horizontal: 32, vertical: 20),
              textStyle: const TextStyle(
                fontWeight: FontWeight.w700,
                fontSize: 16,
              ),
            ),
            icon: const Icon(Icons.add_home_work),
            label: const Text('Become a host'),
          ),
        ],
      ),
    );
  }
}

class _Footer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Column(
      children: [
        Divider(color: cs.outlineVariant),
        const SizedBox(height: 32),
        Wrap(
          spacing: 64,
          runSpacing: 32,
          children: const [
            _FooterCol('Explore', ['Stays', 'Vehicles', 'Cities', 'Featured']),
            _FooterCol('Hosting', ['List a place', 'Resources', 'Community']),
            _FooterCol('Support', ['Help center', 'Trust & safety', 'Contact']),
            _FooterCol('Company', ['About', 'Careers', 'Press']),
          ],
        ),
        const SizedBox(height: 48),
        Divider(color: cs.outlineVariant),
        const SizedBox(height: 24),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text('© ${DateTime.now().year} HomeFlex. All rights reserved.',
                style:
                    TextStyle(color: cs.onSurfaceVariant, fontSize: 13)),
            Row(
              children: [
                Icon(Icons.language, size: 16, color: cs.onSurfaceVariant),
                const SizedBox(width: 6),
                Text('English (US)',
                    style: TextStyle(
                      color: cs.onSurfaceVariant,
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                    )),
              ],
            ),
          ],
        ),
      ],
    );
  }
}

class _FooterCol extends StatelessWidget {
  final String title;
  final List<String> items;
  const _FooterCol(this.title, this.items);

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title,
            style: const TextStyle(
              fontWeight: FontWeight.w800,
              fontSize: 14,
              letterSpacing: 0.2,
            )),
        const SizedBox(height: 16),
        for (final item in items)
          Padding(
            padding: const EdgeInsets.only(bottom: 10),
            child: Text(item,
                style: TextStyle(color: cs.onSurfaceVariant, fontSize: 14)),
          ),
      ],
    );
  }
}
