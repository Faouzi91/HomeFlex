// ====================================
// properties.routes.ts - Standalone routing
// ====================================
import { Routes } from '@angular/router';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { PublicAccessGuard } from 'src/app/core/guards/public-access.guard';

export const PROPERTY_ROUTES: Routes = [
  {
    path: '',
    canActivate: [PublicAccessGuard],
    loadComponent: () =>
      import('./property-list/property-list.component').then((m) => m.PropertyListComponent),
  },
  {
    path: 'my-properties',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./my-properties/my-properties.component').then((m) => m.MyPropertiesComponent),
  },
  {
    path: 'add',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./add-property/add-property.component').then((m) => m.AddPropertyComponent),
  },
  {
    path: ':id',
    canActivate: [PublicAccessGuard],
    loadComponent: () =>
      import('./property-detail/property-detail.component').then((m) => m.PropertyDetailComponent),
  },
];
