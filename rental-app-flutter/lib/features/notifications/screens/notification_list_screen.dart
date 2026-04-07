import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/notification_provider.dart';

class NotificationListScreen extends ConsumerWidget {
  const NotificationListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final notificationsAsync = ref.watch(allNotificationsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Notifications'),
        actions: [
          TextButton(
            onPressed: () async {
              await ref.read(notificationNotifierProvider).markAllAsRead();
              ref.invalidate(allNotificationsProvider);
            },
            child: const Text('Mark all read'),
          ),
        ],
      ),
      body: notificationsAsync.when(
        data: (notifications) {
          if (notifications.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.notifications_off, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('No notifications', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }
          return ListView.separated(
            itemCount: notifications.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, index) {
              final notif = notifications[index];
              return Dismissible(
                key: Key(notif.id),
                direction: DismissDirection.endToStart,
                background: Container(
                  color: Colors.red,
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 16),
                  child: const Icon(Icons.delete, color: Colors.white),
                ),
                onDismissed: (_) async {
                  await ref.read(notificationNotifierProvider).deleteNotification(notif.id);
                  ref.invalidate(allNotificationsProvider);
                },
                child: ListTile(
                  leading: Icon(
                    _notifIcon(notif.type),
                    color: notif.isRead ? Colors.grey : Theme.of(context).primaryColor,
                  ),
                  title: Text(
                    notif.title,
                    style: TextStyle(
                        fontWeight: notif.isRead ? FontWeight.normal : FontWeight.bold),
                  ),
                  subtitle: Text(notif.message, maxLines: 2, overflow: TextOverflow.ellipsis),
                  trailing: Text(
                    _formatDate(notif.createdAt),
                    style: const TextStyle(fontSize: 11, color: Colors.grey),
                  ),
                  onTap: () async {
                    if (!notif.isRead) {
                      await ref.read(notificationNotifierProvider).markAsRead(notif.id);
                      ref.invalidate(allNotificationsProvider);
                    }
                    if (context.mounted) {
                      _navigateToRelated(context, notif.relatedEntityType, notif.relatedEntityId);
                    }
                  },
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

  IconData _notifIcon(String type) {
    switch (type.toLowerCase()) {
      case 'booking_request':
      case 'booking_approved':
      case 'booking_rejected':
        return Icons.calendar_today;
      case 'message':
        return Icons.chat;
      case 'payment':
        return Icons.payment;
      default:
        return Icons.notifications;
    }
  }

  String _formatDate(String dateStr) {
    if (dateStr.length >= 10) return dateStr.substring(0, 10);
    return dateStr;
  }

  void _navigateToRelated(BuildContext context, String? entityType, String? entityId) {
    if (entityType == null || entityId == null) return;
    switch (entityType.toUpperCase()) {
      case 'BOOKING':
        context.push('/bookings/$entityId');
        break;
      case 'PROPERTY':
        context.push('/properties/$entityId');
        break;
      case 'CHAT':
      case 'CHAT_ROOM':
        context.push('/chat/$entityId');
        break;
    }
  }
}
