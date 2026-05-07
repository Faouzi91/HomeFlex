import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '../state/session.store';
import { waitForSession } from './session-guard.utils';

export const landlordGuard: CanActivateFn = () => {
  const session = inject(SessionStore);
  const router = inject(Router);

  return waitForSession(session, () =>
    session.isLandlord() || session.isAdmin()
      ? true
      : router.createUrlTree(['/workspace/overview']),
  );
};
