import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { LandingComponent } from './features/landing/landing.component';
import { PublicAccessGuard } from './core/guards/public-access.guard';

export const APP_ROUTES: Routes = [
  {
    path: '',
    canActivate: [PublicAccessGuard],
    component: LandingComponent,
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'properties',
    loadChildren: () =>
      import('./features/properties/properties.routes').then((m) => m.PROPERTY_ROUTES),
  },
  {
    path: 'vehicles',
    loadChildren: () => import('./features/vehicles/vehicles.routes').then((m) => m.VEHICLE_ROUTES),
  },
  {
    path: 'bookings',
    loadChildren: () => import('./features/bookings/bookings.module').then((m) => m.BookingsModule),
    canActivate: [AuthGuard, PublicAccessGuard],
  },
  {
    path: 'chat',
    loadChildren: () => import('./features/chat/chat.module').then((m) => m.ChatModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'favorites',
    loadChildren: () =>
      import('./features/favorites/favorites.routes').then((m) => m.FAVORITES_ROUTES),
    canActivate: [AuthGuard, PublicAccessGuard],
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.module').then((m) => m.ProfileModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
    canActivate: [AuthGuard],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
