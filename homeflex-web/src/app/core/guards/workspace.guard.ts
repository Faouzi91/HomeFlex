import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '../state/session.store';
import { waitForSession } from './session-guard.utils';

export const workspaceGuard: CanActivateFn = (route, state) => {
  const session = inject(SessionStore);
  const router = inject(Router);

  return waitForSession(session, () =>
    session.isAuthenticated()
      ? true
      : router.createUrlTree(['/login'], {
          queryParams: state.url ? { redirectUrl: state.url } : undefined,
        }),
  );
};
