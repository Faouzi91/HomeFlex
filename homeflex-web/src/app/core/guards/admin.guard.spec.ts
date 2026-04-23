import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { adminGuard } from './admin.guard';
import { SessionStore } from '../state/session.store';
import { User } from '../models/api.types';

function mockSession(role: string | null) {
  const user =
    role !== null
      ? ({ role, email: 'test@test.com', isActive: true } as Partial<User> as User)
      : null;
  return {
    init: jasmine.createSpy('init'),
    user: signal<User | null>(user),
    isAuthenticated: signal(role !== null),
    isAdmin: signal(role === 'ADMIN'),
    loading: signal(false),
    pending: signal(false),
    initialized: signal(true),
  };
}

describe('adminGuard', () => {
  it('allows access when the user is ADMIN', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: mockSession('ADMIN') }],
    });

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never));

    expect(result).toBe(true);
  });

  it('redirects a TENANT to /admin/login', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: mockSession('TENANT') }],
    });

    const router = TestBed.inject(Router);
    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never));

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin/login');
  });

  it('redirects an unauthenticated visitor to /admin/login', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: mockSession(null) }],
    });

    const router = TestBed.inject(Router);
    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never));

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin/login');
  });
});
