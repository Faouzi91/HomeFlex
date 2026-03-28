import { Routes } from '@angular/router';

export const FAVORITES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./favorites.component').then((m) => m.FavoritesComponent),
  },
];
