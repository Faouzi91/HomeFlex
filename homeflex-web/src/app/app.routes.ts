import { Routes } from '@angular/router';
import { HomePageComponent } from './features/marketing/pages/home/home.page';
import { LoginPageComponent } from './features/auth/pages/login/login.page';
import { PasswordResetPageComponent } from './features/auth/pages/password-reset/password-reset.page';
import { RegisterPageComponent } from './features/auth/pages/register/register.page';
import { PropertiesPageComponent } from './features/properties/pages/properties/properties.page';
import { PropertyDetailPageComponent } from './features/properties/pages/property-detail/property-detail.page';
import { VehicleDetailPageComponent } from './features/vehicles/pages/vehicle-detail/vehicle-detail.page';
import { VehiclesPageComponent } from './features/vehicles/pages/vehicles/vehicles.page';
import { SupportPageComponent } from './features/marketing/pages/support/support.page';
import { AdminLayoutComponent } from './features/admin/layout/admin-layout/admin-layout.component';
import { AdminDashboardPageComponent } from './features/admin/pages/dashboard/dashboard.page';
import { AdminLoginPageComponent } from './features/admin/pages/admin-login/admin-login.page';
import { AdminSettingsPageComponent } from './features/admin/pages/settings/admin-settings.page';
import { AdminUsersPageComponent } from './features/admin/pages/users/admin-users.page';
import { AdminPropertiesPageComponent } from './features/admin/pages/properties/admin-properties.page';
import { AdminReportsPageComponent } from './features/admin/pages/reports/admin-reports.page';
import { adminGuard } from './core/guards/admin.guard';
import { guestOnlyGuard } from './core/guards/guest-only.guard';
import { workspaceGuard } from './core/guards/workspace.guard';

export const routes: Routes = [
  { path: '', component: HomePageComponent, title: 'HomeFlex | Premium rentals' },
  { path: 'properties', component: PropertiesPageComponent, title: 'Properties | HomeFlex' },
  {
    path: 'properties/:id',
    component: PropertyDetailPageComponent,
    title: 'Property detail | HomeFlex',
  },
  { path: 'vehicles', component: VehiclesPageComponent, title: 'Vehicles | HomeFlex' },
  {
    path: 'vehicles/:id',
    component: VehicleDetailPageComponent,
    title: 'Vehicle detail | HomeFlex',
  },
  {
    path: 'login',
    component: LoginPageComponent,
    title: 'Login | HomeFlex',
    canActivate: [guestOnlyGuard],
  },
  {
    path: 'register',
    component: RegisterPageComponent,
    title: 'Register | HomeFlex',
    canActivate: [guestOnlyGuard],
  },
  {
    path: 'password-reset',
    component: PasswordResetPageComponent,
    title: 'Reset password | HomeFlex',
    canActivate: [guestOnlyGuard],
  },
  { path: 'auth', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'workspace',
    canActivate: [workspaceGuard],
    loadChildren: () =>
      import('./features/workspace/workspace.routes').then((m) => m.workspaceRoutes),
  },
  { path: 'support', component: SupportPageComponent, title: 'Support | HomeFlex' },

  // Admin routes
  {
    path: 'admin/login',
    component: AdminLoginPageComponent,
    title: 'Admin Login | HomeFlex',
    canActivate: [guestOnlyGuard],
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', component: AdminDashboardPageComponent, title: 'Dashboard | Admin' },
      { path: 'users', component: AdminUsersPageComponent, title: 'Users | Admin' },
      { path: 'properties', component: AdminPropertiesPageComponent, title: 'Properties | Admin' },
      { path: 'reports', component: AdminReportsPageComponent, title: 'Reports | Admin' },
      { path: 'settings', component: AdminSettingsPageComponent, title: 'Settings | Admin' },
    ],
  },

  { path: '**', redirectTo: '' },
];
