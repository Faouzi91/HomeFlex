import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/admin_provider.dart';
import '../models/report_dto.dart';

class AdminReportsScreen extends ConsumerWidget {
  const AdminReportsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final reportsAsync = ref.watch(reportsProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Reports')),
      body: reportsAsync.when(
        data: (reports) {
          if (reports.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.report_off, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('No reports', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }
          return RefreshIndicator(
            onRefresh: () async => ref.invalidate(reportsProvider),
            child: ListView.builder(
              padding: const EdgeInsets.all(8),
              itemCount: reports.length,
              itemBuilder: (context, index) =>
                  _ReportCard(report: reports[index]),
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }
}

class _ReportCard extends ConsumerWidget {
  final ReportDto report;
  const _ReportCard({required this.report});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isPending = report.status.toUpperCase() == 'PENDING';

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Text(
                    report.reason ?? 'No reason provided',
                    style: const TextStyle(
                        fontWeight: FontWeight.bold, fontSize: 16),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                _StatusChip(status: report.status),
              ],
            ),
            if (report.description != null &&
                report.description!.isNotEmpty) ...[
              const SizedBox(height: 8),
              Text(report.description!,
                  style: const TextStyle(color: Colors.grey)),
            ],
            if (report.reporter != null) ...[
              const SizedBox(height: 8),
              Text(
                'Reported by: ${report.reporter!.firstName} ${report.reporter!.lastName}',
                style: const TextStyle(fontSize: 12, color: Colors.grey),
              ),
            ],
            Text(
              'Date: ${report.createdAt.length >= 10 ? report.createdAt.substring(0, 10) : report.createdAt}',
              style: const TextStyle(fontSize: 12, color: Colors.grey),
            ),
            if (isPending) ...[
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: FilledButton(
                  onPressed: () => _resolveDialog(context, ref),
                  child: const Text('Resolve'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Future<void> _resolveDialog(BuildContext context, WidgetRef ref) async {
    final notesController = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Resolve Report'),
        content: TextField(
          controller: notesController,
          decoration:
              const InputDecoration(hintText: 'Resolution notes (optional)'),
          maxLines: 3,
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          FilledButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: const Text('Resolve')),
        ],
      ),
    );
    if (confirmed == true) {
      try {
        await ref.read(adminNotifierProvider).resolveReport(
              report.id,
              reason:
                  notesController.text.isNotEmpty ? notesController.text : null,
            );
        ref.invalidate(reportsProvider);
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Report resolved')),
          );
        }
      } catch (e) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error: $e')),
          );
        }
      }
    }
    notesController.dispose();
  }
}

class _StatusChip extends StatelessWidget {
  final String status;
  const _StatusChip({required this.status});

  @override
  Widget build(BuildContext context) {
    final isPending = status.toUpperCase() == 'PENDING';
    final color = isPending ? Colors.orange : Colors.green;
    return Chip(
      label: Text(status, style: TextStyle(color: color, fontSize: 12)),
      backgroundColor: color.withOpacity(0.1),
      side: BorderSide.none,
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}
