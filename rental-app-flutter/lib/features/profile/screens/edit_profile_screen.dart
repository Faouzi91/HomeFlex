import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import '../../auth/providers/auth_provider.dart';

class EditProfileScreen extends ConsumerStatefulWidget {
  const EditProfileScreen({super.key});

  @override
  ConsumerState<EditProfileScreen> createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends ConsumerState<EditProfileScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _firstNameController;
  late TextEditingController _lastNameController;
  late TextEditingController _phoneController;

  @override
  void initState() {
    super.initState();
    final user = ref.read(authProvider).user;
    _firstNameController = TextEditingController(text: user?.firstName ?? '');
    _lastNameController = TextEditingController(text: user?.lastName ?? '');
    _phoneController = TextEditingController(text: user?.phoneNumber ?? '');
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);
    final user = authState.user;

    return Scaffold(
      appBar: AppBar(title: const Text('Edit Profile')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              GestureDetector(
                onTap: _pickAvatar,
                child: Stack(
                  children: [
                    CircleAvatar(
                      radius: 50,
                      backgroundImage: user?.profilePictureUrl != null
                          ? NetworkImage(user!.profilePictureUrl!)
                          : null,
                      child: user?.profilePictureUrl == null
                          ? const Icon(Icons.person, size: 50)
                          : null,
                    ),
                    const Positioned(
                      bottom: 0,
                      right: 0,
                      child: CircleAvatar(
                        radius: 16,
                        child: Icon(Icons.camera_alt, size: 16),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              TextFormField(
                controller: _firstNameController,
                decoration: const InputDecoration(
                  labelText: 'First Name',
                  border: OutlineInputBorder(),
                ),
                validator: (v) => v == null || v.length < 2 ? 'Min 2 characters' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _lastNameController,
                decoration: const InputDecoration(
                  labelText: 'Last Name',
                  border: OutlineInputBorder(),
                ),
                validator: (v) => v == null || v.length < 2 ? 'Min 2 characters' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _phoneController,
                decoration: const InputDecoration(
                  labelText: 'Phone Number',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.phone,
              ),
              const SizedBox(height: 24),
              if (authState.error != null)
                Padding(
                  padding: const EdgeInsets.only(bottom: 16),
                  child: Text(authState.error!, style: const TextStyle(color: Colors.red)),
                ),
              SizedBox(
                width: double.infinity,
                height: 48,
                child: authState.isLoading
                    ? const Center(child: CircularProgressIndicator())
                    : FilledButton(
                        onPressed: () async {
                          if (!_formKey.currentState!.validate()) return;
                          await ref.read(authProvider.notifier).updateProfile(
                                firstName: _firstNameController.text.trim(),
                                lastName: _lastNameController.text.trim(),
                                phoneNumber: _phoneController.text.trim(),
                              );
                          if (!context.mounted) return;
                          if (ref.read(authProvider).error == null) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Profile updated')),
                            );
                            context.pop();
                          }
                        },
                        child: const Text('Save Changes'),
                      ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _pickAvatar() async {
    final picker = ImagePicker();
    final image = await picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      await ref.read(authProvider.notifier).uploadAvatar(image.path);
    }
  }
}
