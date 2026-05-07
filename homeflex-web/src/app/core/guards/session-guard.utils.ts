import { Router, UrlTree } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, map, Observable } from 'rxjs';
import { SessionStore } from '../state/session.store';

export function waitForSession(
  session: SessionStore,
  resolve: () => boolean | UrlTree,
): boolean | UrlTree | Observable<boolean | UrlTree> {
  session.init();

  if (!session.loading()) {
    return resolve();
  }

  return toObservable(session.loading).pipe(
    filter((loading) => !loading),
    map(() => resolve()),
  );
}

export function redirectAuthenticatedUser(router: Router, session: SessionStore): UrlTree {
  return router.createUrlTree([session.isAdmin() ? '/admin' : '/workspace']);
}
