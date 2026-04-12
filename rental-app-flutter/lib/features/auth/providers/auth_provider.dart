import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/user_dto.dart';
import '../models/auth_response.dart';
import '../../../core/api/api_client.dart';
import 'package:dio/dio.dart';

class AuthState {
  final UserDto? user;
  final bool isLoading;
  final String? error;
  final String? message;

  AuthState({this.user, this.isLoading = false, this.error, this.message});

  AuthState copyWith({UserDto? user, bool? isLoading, String? error, String? message}) {
    return AuthState(
      user: user ?? this.user,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      message: message,
    );
  }
}

class AuthNotifier extends Notifier<AuthState> {
  @override
  AuthState build() => AuthState();

  final _apiClient = ApiClient();

  Future<void> fetchCurrentUser() async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await _apiClient.dio.get('/users/me');
      final user = UserDto.fromJson(response.data);
      state = state.copyWith(user: user, isLoading: false);
    } on DioException {
      state = state.copyWith(isLoading: false);
    }
  }

  Future<void> login(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _apiClient.dio.post('/auth/login', data: {
        'email': email,
        'password': password,
      });
      final authResponse = AuthResponse.fromJson(response.data);
      state = state.copyWith(user: authResponse.user, isLoading: false);
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Login failed',
      );
    }
  }

  Future<void> register({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
    String? phoneNumber,
    required String role,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _apiClient.dio.post('/auth/register', data: {
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
        if (phoneNumber != null && phoneNumber.isNotEmpty) 'phoneNumber': phoneNumber,
        'role': role,
      });
      final authResponse = AuthResponse.fromJson(response.data);
      state = state.copyWith(user: authResponse.user, isLoading: false);
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Registration failed',
      );
    }
  }

  Future<void> forgotPassword(String email) async {
    state = state.copyWith(isLoading: true, error: null, message: null);
    try {
      final response = await _apiClient.dio.post('/auth/forgot-password', data: {
        'email': email,
      });
      state = state.copyWith(
        isLoading: false,
        message: response.data['data'] ?? 'Reset link sent to your email',
      );
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Failed to send reset link',
      );
    }
  }

  Future<void> resetPassword(String token, String newPassword) async {
    state = state.copyWith(isLoading: true, error: null, message: null);
    try {
      final response = await _apiClient.dio.post('/auth/reset-password', data: {
        'token': token,
        'newPassword': newPassword,
      });
      state = state.copyWith(
        isLoading: false,
        message: response.data['data'] ?? 'Password reset successfully',
      );
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Failed to reset password',
      );
    }
  }

  Future<void> updateProfile({
    required String firstName,
    required String lastName,
    String? phoneNumber,
    String? languagePreference,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _apiClient.dio.put('/users/me', data: {
        'firstName': firstName,
        'lastName': lastName,
        'phoneNumber': ?phoneNumber,
        'languagePreference': ?languagePreference,
      });
      final user = UserDto.fromJson(response.data);
      state = state.copyWith(user: user, isLoading: false);
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Failed to update profile',
      );
    }
  }

  Future<void> uploadAvatar(String filePath) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(filePath),
      });
      final response = await _apiClient.dio.post(
        '/users/me/avatar',
        data: formData,
        options: Options(contentType: 'multipart/form-data'),
      );
      final user = UserDto.fromJson(response.data);
      state = state.copyWith(user: user, isLoading: false);
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Failed to upload avatar',
      );
    }
  }

  Future<void> changePassword(String currentPassword, String newPassword) async {
    state = state.copyWith(isLoading: true, error: null, message: null);
    try {
      final response = await _apiClient.dio.put('/users/me/password', data: {
        'currentPassword': currentPassword,
        'newPassword': newPassword,
      });
      state = state.copyWith(
        isLoading: false,
        message: response.data['data'] ?? 'Password changed successfully',
      );
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Failed to change password',
      );
    }
  }

  Future<void> googleSignIn(String idToken) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _apiClient.dio.post('/auth/google', data: {
        'token': idToken,
      });
      final authResponse = AuthResponse.fromJson(response.data);
      state = state.copyWith(user: authResponse.user, isLoading: false);
    } on DioException catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.response?.data['message'] ?? 'Google sign-in failed',
      );
    }
  }

  void updateUser(UserDto user) {
    state = state.copyWith(user: user);
  }

  Future<void> logout() async {
    try {
      await _apiClient.dio.post('/auth/logout');
    } finally {
      state = AuthState();
    }
  }
}

final authProvider = NotifierProvider<AuthNotifier, AuthState>(AuthNotifier.new);
