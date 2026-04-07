import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/admin_provider.dart';

class AdminDashboardScreen extends ConsumerWidget {
  const AdminDashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final analyticsAsync = ref.watch(analyticsProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Admin Dashboard')),
      body: analyticsAsync.when(
        data: (analytics) => SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Overview', style: Theme.of(context).textTheme.headlineSmall),
              const SizedBox(height: 16),
              GridView.count(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisCount: 2,
                childAspectRatio: 1.5,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                children: [
                  _StatCard('Total Users', '${analytics.totalUsers}', Icons.people, Colors.blue),
                  _StatCard('Total Properties', '${analytics.totalProperties}', Icons.home, Colors.green),
                  _StatCard('Pending Properties', '${analytics.pendingProperties}', Icons.pending, Colors.orange),
                  _StatCard('Total Bookings', '${analytics.totalBookings}', Icons.calendar_today, Colors.purple),
                  _StatCard('Tenants', '${analytics.totalTenants}', Icons.person, Colors.teal),
                  _StatCard('Landlords', '${analytics.totalLandlords}', Icons.business, Colors.indigo),
                ],
              ),
              const SizedBox(height: 24),
              Text('Management', style: Theme.of(context).textTheme.titleLarge),
              const SizedBox(height: 12),
              ListTile(
                leading: const Icon(Icons.pending_actions),
                title: const Text('Pending Properties'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => context.push('/admin/pending-properties'),
              ),
              const Divider(),
              ListTile(
                leading: const Icon(Icons.people),
                title: const Text('User Management'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => context.push('/admin/users'),
              ),
              const Divider(),
              ListTile(
                leading: const Icon(Icons.report),
                title: const Text('Reports'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => context.push('/admin/reports'),
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

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;
  const _StatCard(this.title, this.value, this.icon, this.color);

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: color, size: 28),
            const SizedBox(height: 8),
            Text(value, style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: color)),
            Text(title, style: const TextStyle(fontSize: 12, color: Colors.grey), textAlign: TextAlign.center),
          ],
        ),
      ),
    );
  }
}
