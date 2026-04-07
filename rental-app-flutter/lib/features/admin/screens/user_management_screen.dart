import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/admin_provider.dart';

class UserManagementScreen extends ConsumerWidget {
  const UserManagementScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final usersAsync = ref.watch(allUsersProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('User Management')),
      body: usersAsync.when(
        data: (users) {
          if (users.isEmpty) {
            return const Center(child: Text('No users found'));
          }
          return ListView.separated(
            itemCount: users.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, index) {
              final user = users[index];
              return ListTile(
                leading: CircleAvatar(
                  child: Text('${user.firstName[0]}${user.lastName[0]}'),
                ),
                title: Text('${user.firstName} ${user.lastName}'),
                subtitle: Text('${user.email} - ${user.role}'),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      user.isActive ? Icons.check_circle : Icons.cancel,
                      color: user.isActive ? Colors.green : Colors.red,
                      size: 20,
                    ),
                    const SizedBox(width: 8),
                    PopupMenuButton<String>(
                      onSelected: (action) async {
                        if (action == 'suspend') {
                          await ref.read(adminNotifierProvider).suspendUser(user.id);
                          ref.invalidate(allUsersProvider);
                        } else if (action == 'activate') {
                          await ref.read(adminNotifierProvider).activateUser(user.id);
                          ref.invalidate(allUsersProvider);
                        }
                      },
                      itemBuilder: (_) => [
                        if (user.isActive)
                          const PopupMenuItem(value: 'suspend', child: Text('Suspend'))
                        else
                          const PopupMenuItem(value: 'activate', child: Text('Activate')),
                      ],
                    ),
                  ],
                ),
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}
