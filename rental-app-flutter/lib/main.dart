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

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final apiClient = ApiClient();
  await apiClient.init();

  runApp(
    const ProviderScope(
      child: HomeFlexApp(),
    ),
  );
}

final _router = GoRouter(
  initialLocation: '/',
  routes: [
    // Auth wrapper — splash / session restore
    GoRoute(
      path: '/',
      builder: (context, state) => const AuthWrapper(),
    ),

    // Public auth routes
    GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
    GoRoute(path: '/register', builder: (context, state) => const RegisterScreen()),
    GoRoute(path: '/forgot-password', builder: (context, state) => const ForgotPasswordScreen()),
    GoRoute(path: '/reset-password', builder: (context, state) => const ResetPasswordScreen()),
    GoRoute(
      path: '/verify-email',
      builder: (context, state) =>
          EmailVerificationScreen(token: state.uri.queryParameters['token']),
    ),

    // Main app shell with bottom navigation
    StatefulShellRoute.indexedStack(
      builder: (context, state, navigationShell) =>
          MainScreen(navigationShell: navigationShell),
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
                      builder: (context, state) =>
                          CreateBookingScreen(propertyId: state.pathParameters['id']!),
                    ),
                    GoRoute(
                      path: 'edit',
                      builder: (context, state) =>
                          PropertyFormScreen(propertyId: state.pathParameters['id']!),
                    ),
                    GoRoute(
                      path: 'bookings',
                      builder: (context, state) =>
                          PropertyBookingsScreen(propertyId: state.pathParameters['id']!),
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
                      builder: (context, state) =>
                          CreateVehicleBookingScreen(vehicleId: state.pathParameters['id']!),
                    ),
                    GoRoute(
                      path: 'edit',
                      builder: (context, state) =>
                          VehicleFormScreen(vehicleId: state.pathParameters['id']!),
                    ),
                    GoRoute(
                      path: 'condition',
                      builder: (context, state) =>
                          VehicleConditionScreen(vehicleId: state.pathParameters['id']!),
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
    GoRoute(path: '/favorites', builder: (context, state) => const FavoritesScreen()),
    GoRoute(path: '/notifications', builder: (context, state) => const NotificationListScreen()),
    GoRoute(path: '/my-properties', builder: (context, state) => const MyPropertiesScreen()),
    GoRoute(
      path: '/properties/create',
      builder: (context, state) => const PropertyFormScreen(),
    ),
    GoRoute(path: '/kyc', builder: (context, state) => const KycVerificationScreen()),
    GoRoute(path: '/my-vehicles', builder: (context, state) => const MyVehiclesScreen()),
    GoRoute(
      path: '/vehicles/create',
      builder: (context, state) => const VehicleFormScreen(),
    ),

    // Admin routes
    GoRoute(path: '/admin', builder: (context, state) => const AdminDashboardScreen()),
    GoRoute(
        path: '/admin/pending-properties',
        builder: (context, state) => const PendingPropertiesScreen()),
    GoRoute(
        path: '/admin/users',
        builder: (context, state) => const UserManagementScreen()),
    GoRoute(
        path: '/admin/reports',
        builder: (context, state) => const AdminReportsScreen()),
  ],
);

class HomeFlexApp extends ConsumerWidget {
  const HomeFlexApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeMode = ref.watch(themeProvider);

    return MaterialApp.router(
      title: 'HomeFlex',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light(),
      darkTheme: AppTheme.dark(),
      themeMode: themeMode,
      routerConfig: _router,
    );
  }
}

class AuthWrapper extends ConsumerStatefulWidget {
  const AuthWrapper({super.key});

  @override
  ConsumerState<AuthWrapper> createState() => _AuthWrapperState();
}

class _AuthWrapperState extends ConsumerState<AuthWrapper> {
  bool _initialized = false;

  @override
  void initState() {
    super.initState();
    _restoreSession();
  }

  Future<void> _restoreSession() async {
    await ref.read(authProvider.notifier).fetchCurrentUser();
    if (mounted) {
      setState(() => _initialized = true);
      final user = ref.read(authProvider).user;
      if (user != null) {
        context.go('/properties');
      } else {
        context.go('/login');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.home_work, size: 80, color: Theme.of(context).primaryColor),
            const SizedBox(height: 24),
            const CircularProgressIndicator(),
            const SizedBox(height: 16),
            const Text('Loading...', style: TextStyle(color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}

class MainScreen extends StatelessWidget {
  final StatefulNavigationShell navigationShell;
  const MainScreen({super.key, required this.navigationShell});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: navigationShell,
      bottomNavigationBar: NavigationBar(
        selectedIndex: navigationShell.currentIndex,
        onDestinationSelected: (index) {
          navigationShell.goBranch(
            index,
            initialLocation: index == navigationShell.currentIndex,
          );
        },
        destinations: const [
          NavigationDestination(icon: Icon(Icons.home_outlined), selectedIcon: Icon(Icons.home), label: 'Properties'),
          NavigationDestination(icon: Icon(Icons.directions_car_outlined), selectedIcon: Icon(Icons.directions_car), label: 'Vehicles'),
          NavigationDestination(icon: Icon(Icons.calendar_today_outlined), selectedIcon: Icon(Icons.calendar_today), label: 'Bookings'),
          NavigationDestination(icon: Icon(Icons.chat_outlined), selectedIcon: Icon(Icons.chat), label: 'Chat'),
          NavigationDestination(icon: Icon(Icons.person_outlined), selectedIcon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}
