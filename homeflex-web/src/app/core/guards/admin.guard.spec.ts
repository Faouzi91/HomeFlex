import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { signal } from '@angular/core';
import { adminGuard } from './admin.guard';
import { SessionStore } from '../state/session.store';
import { User } from '../models/api.types';

const mockRoute = {} as ActivatedRouteSnapshot;
const mockState = { url: '/admin/dashboard' } as RouterStateSnapshot;

function buildMockStore(user: Partial<User> | null, authenticated: boolean) {
  return {
    user: signal<User | null>(user as User | null),
    isAuthenticated: signal(authenticated),
    loading: signal(false),
    pending: signal(false),
    initialized: signal(true),
  };
}

function runGuard(): boolean | UrlTree {
  return TestBed.runInInjectionContext(() => adminGuard(mockRoute, mockState));
}

describe('adminGuard', () => {
  let router: Router;

  function configure(user: Partial<User> | null, authenticated: boolean) {
    TestBed.configureTestingModule({
      providers: [
        { provide: SessionStore, useValue: buildMockStore(user, authenticated) },
      ],
    });
    router = TestBed.inject(Router);
  }

  it('allows access when authenticated as ADMIN', () => {
    configure({ role: 'ADMIN' }, true);
    expect(runGuard()).toBe(true);
  });

  it('redirects TENANT to /admin/login', () => {
    configure({ role: 'TENANT' }, true);
    const result = runGuard();
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin/login');
  });

  it('redirects unauthenticated visitor to /admin/login', () => {
    configure(null, false);
    const result = runGuard();
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin/login');
  });
});
