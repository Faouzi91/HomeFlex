import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'max_width_content.dart';

/// Adaptive shell:
/// - width < 600 → bottom NavigationBar (mobile)
/// - 600 ≤ width < 1240 → NavigationRail (tablet)
/// - width ≥ 1240 → extended NavigationRail (desktop)
class ResponsiveScaffold extends StatelessWidget {
  final int currentIndex;
  final ValueChanged<int> onDestinationSelected;
  final Widget body;

  const ResponsiveScaffold({
    super.key,
    required this.currentIndex,
    required this.onDestinationSelected,
    required this.body,
  });

  static const _destinations = [
    _Dest(Icons.home_outlined, Icons.home, 'Properties'),
    _Dest(Icons.directions_car_outlined, Icons.directions_car, 'Vehicles'),
    _Dest(Icons.calendar_today_outlined, Icons.calendar_today, 'Bookings'),
    _Dest(Icons.chat_outlined, Icons.chat, 'Chat'),
    _Dest(Icons.person_outlined, Icons.person, 'Profile'),
  ];

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final isMobile = width < 600;
    final isDesktop = width >= 1240;

    final wrappedBody = MaxWidthContent(child: body);

    if (isMobile) {
      return Scaffold(
        body: SafeArea(child: wrappedBody),
        bottomNavigationBar: NavigationBar(
          selectedIndex: currentIndex,
          onDestinationSelected: onDestinationSelected,
          destinations: [
            for (final d in _destinations)
              NavigationDestination(
                icon: Icon(d.icon),
                selectedIcon: Icon(d.selectedIcon),
                label: d.label,
              ),
          ],
        ),
      );
    }

    return Scaffold(
      appBar: _WebAppBar(currentIndex: currentIndex),
      body: SafeArea(
        child: Row(
          children: [
            NavigationRail(
              extended: isDesktop,
              selectedIndex: currentIndex,
              onDestinationSelected: onDestinationSelected,
              labelType: isDesktop
                  ? NavigationRailLabelType.none
                  : NavigationRailLabelType.all,
              destinations: [
                for (final d in _destinations)
                  NavigationRailDestination(
                    icon: Icon(d.icon),
                    selectedIcon: Icon(d.selectedIcon),
                    label: Text(d.label),
                  ),
              ],
            ),
            const VerticalDivider(width: 1),
            Expanded(child: wrappedBody),
          ],
        ),
      ),
    );
  }
}

class _Dest {
  final IconData icon;
  final IconData selectedIcon;
  final String label;
  const _Dest(this.icon, this.selectedIcon, this.label);
}

class _WebAppBar extends StatelessWidget implements PreferredSizeWidget {
  final int currentIndex;
  const _WebAppBar({required this.currentIndex});

  @override
  Size get preferredSize => const Size.fromHeight(56);

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: InkWell(
        onTap: () => context.go('/'),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.home_work, color: Theme.of(context).colorScheme.primary),
            const SizedBox(width: 8),
            const Text('HomeFlex',
                style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.notifications_outlined),
          onPressed: () => context.push('/notifications'),
        ),
        IconButton(
          icon: const Icon(Icons.favorite_border),
          onPressed: () => context.push('/favorites'),
        ),
        const SizedBox(width: 8),
      ],
    );
  }
}
