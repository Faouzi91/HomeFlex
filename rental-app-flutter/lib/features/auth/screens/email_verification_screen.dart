import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/api/api_client.dart';

class EmailVerificationScreen extends ConsumerStatefulWidget {
  final String? token;
  const EmailVerificationScreen({super.key, this.token});

  @override
  ConsumerState<EmailVerificationScreen> createState() =>
      _EmailVerificationScreenState();
}

class _EmailVerificationScreenState
    extends ConsumerState<EmailVerificationScreen> {
  final _tokenController = TextEditingController();
  bool _isVerifying = false;
  String? _message;
  bool _success = false;

  @override
  void initState() {
    super.initState();
    if (widget.token != null) {
      _tokenController.text = widget.token!;
      _verify();
    }
  }

  @override
  void dispose() {
    _tokenController.dispose();
    super.dispose();
  }

  Future<void> _verify() async {
    final token = _tokenController.text.trim();
    if (token.isEmpty) return;

    setState(() {
      _isVerifying = true;
      _message = null;
    });

    try {
      final apiClient = ApiClient();
      await apiClient.dio.get('/auth/verify', queryParameters: {'token': token});
      setState(() {
        _success = true;
        _message = 'Email verified successfully! You can now sign in.';
        _isVerifying = false;
      });
    } catch (e) {
      setState(() {
        _success = false;
        _message = 'Verification failed. The token may be expired or invalid.';
        _isVerifying = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Verify Email')),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                _success ? Icons.check_circle : Icons.mark_email_read,
                size: 80,
                color: _success ? Colors.green : Theme.of(context).primaryColor,
              ),
              const SizedBox(height: 24),
              Text(
                _success ? 'Email Verified!' : 'Verify Your Email',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
              const SizedBox(height: 8),
              const Text(
                'Enter the verification token from your email',
                style: TextStyle(color: Colors.grey),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              if (!_success) ...[
                TextField(
                  controller: _tokenController,
                  decoration: const InputDecoration(
                    labelText: 'Verification Token',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.key),
                  ),
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: _isVerifying
                      ? const Center(child: CircularProgressIndicator())
                      : FilledButton(
                          onPressed: _verify,
                          child: const Text('Verify'),
                        ),
                ),
              ],
              if (_message != null) ...[
                const SizedBox(height: 16),
                Text(
                  _message!,
                  style: TextStyle(
                    color: _success ? Colors.green : Colors.red,
                  ),
                  textAlign: TextAlign.center,
                ),
              ],
              const SizedBox(height: 24),
              if (_success)
                FilledButton(
                  onPressed: () => context.go('/login'),
                  child: const Text('Go to Login'),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
