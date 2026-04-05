import { Routes } from '@angular/router';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { PublicAccessGuard } from 'src/app/core/guards/public-access.guard';

export const VEHICLE_ROUTES: Routes = [
  {
    path: '',
    canActivate: [AuthGuard, PublicAccessGuard],
    loadComponent: () =>
      import('./vehicle-list/vehicle-list.component').then((m) => m.VehicleListComponent),
  },
  {
    path: ':id',
    canActivate: [AuthGuard, PublicAccessGuard],
    loadComponent: () =>
      import('./vehicle-detail/vehicle-detail.component').then((m) => m.VehicleDetailComponent),
  },
];
