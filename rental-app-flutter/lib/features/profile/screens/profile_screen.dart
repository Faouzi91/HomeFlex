import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../auth/providers/auth_provider.dart';
import '../../../core/theme/theme_provider.dart';

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final user = ref.watch(authProvider).user;

    if (user == null) {
      return const Scaffold(body: Center(child: Text('Not logged in')));
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ListView(
        children: [
          const SizedBox(height: 24),
          Center(
            child: CircleAvatar(
              radius: 50,
              backgroundImage: user.profilePictureUrl != null
                  ? NetworkImage(user.profilePictureUrl!)
                  : null,
              child: user.profilePictureUrl == null
                  ? Text(
                      '${user.firstName[0]}${user.lastName[0]}',
                      style: const TextStyle(fontSize: 32),
                    )
                  : null,
            ),
          ),
          const SizedBox(height: 16),
          Center(
            child: Text(
              '${user.firstName} ${user.lastName}',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
          ),
          Center(
            child: Chip(
              label: Text(user.role),
              backgroundColor: _roleColor(user.role),
            ),
          ),
          const SizedBox(height: 8),
          Center(child: Text(user.email, style: const TextStyle(color: Colors.grey))),
          const Divider(height: 32),
          ListTile(
            leading: const Icon(Icons.edit),
            title: const Text('Edit Profile'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/profile/edit'),
          ),
          ListTile(
            leading: const Icon(Icons.lock),
            title: const Text('Change Password'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/profile/change-password'),
          ),
          ListTile(
            leading: const Icon(Icons.favorite),
            title: const Text('My Favorites'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/favorites'),
          ),
          ListTile(
            leading: const Icon(Icons.build_circle_outlined),
            title: const Text('My Maintenance Requests'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/my-maintenance'),
          ),
          if (user.role == 'LANDLORD' || user.role == 'ADMIN') ...[
            const Divider(),
            ListTile(
              leading: const Icon(Icons.home_work),
              title: const Text('My Properties'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => context.push('/my-properties'),
            ),
            ListTile(
              leading: const Icon(Icons.build),
              title: const Text('Property Maintenance'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => context.push('/landlord-maintenance'),
            ),
            ListTile(
              leading: const Icon(Icons.directions_car),
              title: const Text('My Vehicles'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => context.push('/my-vehicles'),
            ),
            ListTile(
              leading: const Icon(Icons.verified_user),
              title: const Text('Identity Verification'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => context.push('/kyc'),
            ),
          ],
          if (user.role == 'ADMIN') ...[
            const Divider(),
            ListTile(
              leading: const Icon(Icons.admin_panel_settings),
              title: const Text('Admin Panel'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => context.push('/admin'),
            ),
          ],
          const Divider(),
          ListTile(
            leading: const Icon(Icons.language),
            title: const Text('Language'),
            trailing: DropdownButton<String>(
              value: user.languagePreference ?? 'EN',
              underline: const SizedBox(),
              items: const [
                DropdownMenuItem(value: 'EN', child: Text('English')),
                DropdownMenuItem(value: 'FR', child: Text('Français')),
              ],
              onChanged: (value) async {
                if (value == null) return;
                await ref.read(authProvider.notifier).updateProfile(
                      firstName: user.firstName,
                      lastName: user.lastName,
                      phoneNumber: user.phoneNumber,
                      languagePreference: value,
                    );
              },
            ),
          ),
          ListTile(
            leading: const Icon(Icons.dark_mode),
            title: const Text('Dark Mode'),
            trailing: Switch(
              value: ref.watch(themeProvider) == ThemeMode.dark,
              onChanged: (enabled) {
                ref.read(themeProvider.notifier).setThemeMode(
                    enabled ? ThemeMode.dark : ThemeMode.light);
              },
            ),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout, color: Colors.red),
            title: const Text('Logout', style: TextStyle(color: Colors.red)),
            onTap: () async {
              await ref.read(authProvider.notifier).logout();
              if (context.mounted) context.go('/login');
            },
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }

  Color _roleColor(String role) {
    switch (role) {
      case 'ADMIN':
        return Colors.red.shade100;
      case 'LANDLORD':
        return Colors.blue.shade100;
      default:
        return Colors.green.shade100;
    }
  }
}
