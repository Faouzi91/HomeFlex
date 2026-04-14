import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { SessionStore } from '../state/session.store';

export const adminGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const session = inject(SessionStore);
  const router = inject(Router);

  if (session.isAuthenticated() && session.user()?.role === 'ADMIN') {
    return true;
  }

  // Not authenticated or not an admin — redirect to admin login
  return router.createUrlTree(['/admin/login']);
};
