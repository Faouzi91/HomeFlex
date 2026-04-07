import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/api/api_client.dart';

class KycVerificationScreen extends ConsumerStatefulWidget {
  const KycVerificationScreen({super.key});

  @override
  ConsumerState<KycVerificationScreen> createState() => _KycVerificationScreenState();
}

class _KycVerificationScreenState extends ConsumerState<KycVerificationScreen> {
  bool _loading = false;
  String? _status;
  String? _rejectionReason;
  String? _error;
  String? _sessionId;

  @override
  void initState() {
    super.initState();
    _loadStatus();
  }

  Future<void> _loadStatus() async {
    setState(() => _loading = true);
    try {
      final res = await ApiClient().dio.get('/kyc/status');
      setState(() {
        _status = res.data['status'];
        _rejectionReason = res.data['rejectionReason'];
        _loading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load status';
        _loading = false;
      });
    }
  }

  Future<void> _startVerification() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final res = await ApiClient().dio.post('/kyc/session');
      setState(() {
        _sessionId = res.data['sessionId'];
        _status = 'PENDING';
        _loading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to start verification';
        _loading = false;
      });
    }
  }

  ({IconData icon, Color color, String label}) _statusInfo(String? status) {
    switch (status) {
      case 'VERIFIED':
        return (icon: Icons.verified, color: Colors.green, label: 'Verified');
      case 'PENDING':
        return (icon: Icons.hourglass_top, color: Colors.orange, label: 'Pending Review');
      case 'REJECTED':
        return (icon: Icons.cancel, color: Colors.red, label: 'Rejected');
      default:
        return (icon: Icons.shield_outlined, color: Colors.grey, label: 'Not Started');
    }
  }

  @override
  Widget build(BuildContext context) {
    final info = _statusInfo(_status);
    return Scaffold(
      appBar: AppBar(title: const Text('Identity Verification')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const SizedBox(height: 24),
                  Icon(info.icon, size: 96, color: info.color),
                  const SizedBox(height: 16),
                  Text(info.label,
                      textAlign: TextAlign.center,
                      style: Theme.of(context).textTheme.headlineSmall),
                  const SizedBox(height: 16),
                  Text(
                    _status == 'VERIFIED'
                        ? 'Your identity has been verified. You can list properties and vehicles.'
                        : _status == 'PENDING'
                            ? 'Your verification is being processed. This usually takes a few minutes.'
                            : _status == 'REJECTED'
                                ? (_rejectionReason ?? 'Verification failed. Please try again.')
                                : 'Landlords must verify their identity before listing properties or vehicles.',
                    textAlign: TextAlign.center,
                    style: const TextStyle(color: Colors.grey),
                  ),
                  if (_sessionId != null) ...[
                    const SizedBox(height: 24),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          children: [
                            const Text('Verification Session Created'),
                            const SizedBox(height: 8),
                            SelectableText(_sessionId!,
                                style: const TextStyle(fontFamily: 'monospace', fontSize: 12)),
                            const SizedBox(height: 8),
                            const Text(
                              'Complete the verification on the Stripe Identity portal sent to your email.',
                              textAlign: TextAlign.center,
                              style: TextStyle(fontSize: 12, color: Colors.grey),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                  if (_error != null) ...[
                    const SizedBox(height: 16),
                    Text(_error!, style: const TextStyle(color: Colors.red), textAlign: TextAlign.center),
                  ],
                  const Spacer(),
                  if (_status != 'VERIFIED' && _status != 'PENDING')
                    FilledButton.icon(
                      onPressed: _startVerification,
                      icon: const Icon(Icons.verified_user),
                      label: const Text('Start Verification'),
                    ),
                  const SizedBox(height: 8),
                  OutlinedButton.icon(
                    onPressed: _loadStatus,
                    icon: const Icon(Icons.refresh),
                    label: const Text('Refresh Status'),
                  ),
                ],
              ),
            ),
    );
  }
}
