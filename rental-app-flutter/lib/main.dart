import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'core/api/api_client.dart';
import 'core/theme/app_theme.dart';
import 'core/theme/theme_provider.dart';
import 'features/auth/providers/auth_provider.dart';
import 'features/auth/screens/login_screen.dart';
import 'features/auth/screens/register_screen.dart';
import 'features/auth/screens/forgot_password_screen.dart';
import 'features/auth/screens/reset_password_screen.dart';
import 'features/auth/screens/email_verification_screen.dart';
import 'features/properties/screens/property_grid_screen.dart';
import 'features/properties/screens/property_detail_screen.dart';
import 'features/properties/screens/my_properties_screen.dart';
import 'features/properties/screens/property_form_screen.dart';
import 'features/vehicles/screens/vehicle_grid_screen.dart';
import 'features/vehicles/screens/vehicle_detail_screen.dart';
import 'features/vehicles/screens/my_vehicles_screen.dart';
import 'features/vehicles/screens/vehicle_form_screen.dart';
import 'features/vehicles/screens/create_vehicle_booking_screen.dart';
import 'features/vehicles/screens/vehicle_condition_screen.dart';
import 'features/bookings/screens/bookings_list_screen.dart';
import 'features/bookings/screens/booking_detail_screen.dart';
import 'features/bookings/screens/create_booking_screen.dart';
import 'features/bookings/screens/property_bookings_screen.dart';
import 'features/chat/screens/chat_list_screen.dart';
import 'features/chat/screens/chat_room_screen.dart';
import 'features/profile/screens/profile_screen.dart';
import 'features/profile/screens/edit_profile_screen.dart';
import 'features/profile/screens/change_password_screen.dart';
import 'features/favorites/screens/favorites_screen.dart';
import 'features/notifications/screens/notification_list_screen.dart';
import 'features/admin/screens/admin_dashboard_screen.dart';
import 'features/admin/screens/pending_properties_screen.dart';
import 'features/admin/screens/user_management_screen.dart';
import 'features/admin/screens/admin_reports_screen.dart';
import 'features/kyc/screens/kyc_verification_screen.dart';
import 'features/landing/screens/landing_screen.dart';
import 'features/properties/screens/maintenance_list_screen.dart';
import 'features/properties/screens/maintenance_detail_screen.dart';
import 'features/properties/screens/create_maintenance_request_screen.dart';
import 'shared/widgets/responsive_scaffold.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final apiClient = ApiClient();
  await apiClient.init();

  runApp(const ProviderScope(child: HomeFlexApp()));
}

// ─── Auth-aware route guard ──────────────────────────────────────────────
//
// Public routes are reachable while logged out. Everything else requires a
// user. Some routes additionally require a specific role.
const _publicPaths = <String>{
  '/',
  '/login',
  '/register',
  '/forgot-password',
  '/reset-password',
  '/verify-email',
  '/properties',
  '/vehicles',
};

bool _isPublic(String path) {
  if (_publicPaths.contains(path)) return true;
  // Allow read-only browsing of listings + nested detail.
  if (path.startsWith('/properties/') &&
      !path.endsWith('/book') &&
      !path.endsWith('/edit') &&
      !path.endsWith('/bookings') &&
      path != '/properties/create') {
    return true;
  }
  if (path.startsWith('/vehicles/') &&
      !path.endsWith('/book') &&
      !path.endsWith('/edit') &&
      !path.endsWith('/condition') &&
      path != '/vehicles/create') {
    return true;
  }
  return false;
}

String? _requiredRole(String path) {
  if (path.startsWith('/admin')) return 'ADMIN';
  if (path == '/my-properties' ||
      path == '/properties/create' ||
      path.endsWith('/edit') ||
      path.endsWith('/bookings') ||
      path == '/my-vehicles' ||
      path == '/vehicles/create' ||
      path.endsWith('/condition') ||
      path == '/kyc' ||
      path == '/landlord-maintenance') {
    return 'LANDLORD';
  }
  return null;
}

/// Bridges a Riverpod provider to a `Listenable` so GoRouter re-runs its
/// `redirect` whenever auth state changes.
class _AuthRefresh extends ChangeNotifier {
  _AuthRefresh(Ref ref) {
    ref.listen(authProvider, (_, _) => notifyListeners());
  }
}

final _authRefreshProvider = Provider<_AuthRefresh>((ref) => _AuthRefresh(ref));

/// Fires the silent session restore the first time the app reads it.
final _bootProvider = Provider<void>((ref) {
  Future.microtask(() => ref.read(authProvider.notifier).fetchCurrentUser());
});

final _routerProvider = Provider<GoRouter>((ref) {
  final refresh = ref.watch(_authRefreshProvider);
  return GoRouter(
    initialLocation: '/',
    refreshListenable: refresh,
    redirect: (context, state) {
      final auth = ref.read(authProvider);
      // Wait until silent session restore finishes before redirecting,
      // otherwise a refresh on a guarded page would bounce to /login.
      if (auth.isLoading) return null;
      final user = auth.user;
      final path = state.uri.path;

      // Logged-in users shouldn't see auth screens.
      if (user != null && (path == '/login' || path == '/register')) {
        return '/properties';
      }

      if (_isPublic(path)) return null;

      if (user == null) {
        // Preserve intended destination so we can return after login.
        return '/login?redirect=${Uri.encodeComponent(state.uri.toString())}';
      }

      final required = _requiredRole(path);
      if (required != null) {
        if (required == 'ADMIN' && user.role != 'ADMIN') return '/properties';
        if (required == 'LANDLORD' &&
            user.role != 'LANDLORD' &&
            user.role != 'ADMIN') {
          return '/properties';
        }
      }
      return null;
    },
    routes: [
      // Public landing page (also kicks off silent session restore)
      GoRoute(path: '/', builder: (context, state) => const LandingScreen()),

      // Public auth routes
      GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/forgot-password',
        builder: (context, state) => const ForgotPasswordScreen(),
      ),
      GoRoute(
        path: '/reset-password',
        builder: (context, state) => const ResetPasswordScreen(),
      ),
      GoRoute(
        path: '/verify-email',
        builder: (context, state) =>
            EmailVerificationScreen(token: state.uri.queryParameters['token']),
      ),

      // Main app shell with bottom navigation
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) => ResponsiveScaffold(
          currentIndex: navigationShell.currentIndex,
          onDestinationSelected: (i) => navigationShell.goBranch(
            i,
            initialLocation: i == navigationShell.currentIndex,
          ),
          body: navigationShell,
        ),
        branches: [
          // Tab 0: Properties
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/properties',
                builder: (context, state) => const PropertyGridScreen(),
                routes: [
                  GoRoute(
                    path: ':id',
                    builder: (context, state) =>
                        PropertyDetailScreen(id: state.pathParameters['id']!),
                    routes: [
                      GoRoute(
                        path: 'book',
                        builder: (context, state) => CreateBookingScreen(
                          propertyId: state.pathParameters['id']!,
                        ),
                      ),
                      GoRoute(
                        path: 'edit',
                        builder: (context, state) => PropertyFormScreen(
                          propertyId: state.pathParameters['id']!,
                        ),
                      ),
                      GoRoute(
                        path: 'bookings',
                        builder: (context, state) => PropertyBookingsScreen(
                          propertyId: state.pathParameters['id']!,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),

          // Tab 1: Vehicles
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/vehicles',
                builder: (context, state) => const VehicleGridScreen(),
                routes: [
                  GoRoute(
                    path: ':id',
                    builder: (context, state) =>
                        VehicleDetailScreen(id: state.pathParameters['id']!),
                    routes: [
                      GoRoute(
                        path: 'book',
                        builder: (context, state) => CreateVehicleBookingScreen(
                          vehicleId: state.pathParameters['id']!,
                        ),
                      ),
                      GoRoute(
                        path: 'edit',
                        builder: (context, state) => VehicleFormScreen(
                          vehicleId: state.pathParameters['id']!,
                        ),
                      ),
                      GoRoute(
                        path: 'condition',
                        builder: (context, state) => VehicleConditionScreen(
                          vehicleId: state.pathParameters['id']!,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),

          // Tab 2: Bookings
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/bookings',
                builder: (context, state) => const BookingsListScreen(),
                routes: [
                  GoRoute(
                    path: ':id',
                    builder: (context, state) =>
                        BookingDetailScreen(id: state.pathParameters['id']!),
                  ),
                ],
              ),
            ],
          ),

          // Tab 3: Chat
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/chat',
                builder: (context, state) => const ChatListScreen(),
                routes: [
                  GoRoute(
                    path: ':roomId',
                    builder: (context, state) =>
                        ChatRoomScreen(roomId: state.pathParameters['roomId']!),
                  ),
                ],
              ),
            ],
          ),

          // Tab 4: Profile
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/profile',
                builder: (context, state) => const ProfileScreen(),
                routes: [
                  GoRoute(
                    path: 'edit',
                    builder: (context, state) => const EditProfileScreen(),
                  ),
                  GoRoute(
                    path: 'change-password',
                    builder: (context, state) => const ChangePasswordScreen(),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),

      // Standalone routes (outside shell)
      GoRoute(
        path: '/favorites',
        builder: (context, state) => const FavoritesScreen(),
      ),
      GoRoute(
        path: '/notifications',
        builder: (context, state) => const NotificationListScreen(),
      ),
      GoRoute(
        path: '/my-properties',
        builder: (context, state) => const MyPropertiesScreen(),
      ),
      GoRoute(
        path: '/properties/create',
        builder: (context, state) => const PropertyFormScreen(),
      ),
      GoRoute(
        path: '/kyc',
        builder: (context, state) => const KycVerificationScreen(),
      ),
      GoRoute(
        path: '/my-vehicles',
        builder: (context, state) => const MyVehiclesScreen(),
      ),
      GoRoute(
        path: '/vehicles/create',
        builder: (context, state) => const VehicleFormScreen(),
      ),

      GoRoute(
        path: '/my-maintenance',
        builder: (context, state) => const MaintenanceRequestListScreen(),
      ),
      GoRoute(
        path: '/landlord-maintenance',
        builder: (context, state) =>
            const MaintenanceRequestListScreen(isLandlord: true),
      ),
      GoRoute(
        path: '/maintenance/:id',
        builder: (context, state) =>
            MaintenanceRequestDetailScreen(id: state.pathParameters['id']!),
      ),
      GoRoute(
        path: '/properties/:propertyId/maintenance/create',
        builder: (context, state) => CreateMaintenanceRequestScreen(
            propertyId: state.pathParameters['propertyId']!),
      ),

      // Admin routes
      GoRoute(
        path: '/admin',
        builder: (context, state) => const AdminDashboardScreen(),
      ),
      GoRoute(
        path: '/admin/pending-properties',
        builder: (context, state) => const PendingPropertiesScreen(),
      ),
      GoRoute(
        path: '/admin/users',
        builder: (context, state) => const UserManagementScreen(),
      ),
      GoRoute(
        path: '/admin/reports',
        builder: (context, state) => const AdminReportsScreen(),
      ),
    ],
  );
});

class HomeFlexApp extends ConsumerWidget {
  const HomeFlexApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeMode = ref.watch(themeProvider);
    final router = ref.watch(_routerProvider);
    // Kick off silent session restore exactly once.
    ref.listen(_bootProvider, (_, _) {});

    return MaterialApp.router(
      title: 'HomeFlex',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light(),
      darkTheme: AppTheme.dark(),
      themeMode: themeMode,
      routerConfig: router,
    );
  }
}
