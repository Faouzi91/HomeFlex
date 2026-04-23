import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '../state/session.store';
import { waitForSession } from './session-guard.utils';

export const adminGuard: CanActivateFn = () => {
  const session = inject(SessionStore);
  const router = inject(Router);

  return waitForSession(session, () =>
    session.isAuthenticated() && session.isAdmin()
      ? true
      : router.createUrlTree(['/admin/login']),
  );
};
