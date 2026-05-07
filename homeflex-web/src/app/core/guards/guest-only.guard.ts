import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { redirectAuthenticatedUser, waitForSession } from './session-guard.utils';
import { SessionStore } from '../state/session.store';

export const guestOnlyGuard: CanActivateFn = () => {
  const session = inject(SessionStore);
  const router = inject(Router);

  return waitForSession(session, () =>
    session.isAuthenticated() ? redirectAuthenticatedUser(router, session) : true,
  );
};
