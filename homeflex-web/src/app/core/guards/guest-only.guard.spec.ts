import { TestBed } from '@angular/core/testing';
import { Router, UrlTree, provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { vi } from 'vitest';
import { guestOnlyGuard } from './guest-only.guard';
import { SessionStore } from '../state/session.store';
import { User } from '../models/api.types';

function createSession(role: User['role'] | null, loading = false) {
  const user =
    role !== null
      ? ({ role, email: 'user@homeflex.test', isActive: true } as Partial<User> as User)
      : null;

  return {
    init: vi.fn(),
    user: signal<User | null>(user),
    isAuthenticated: signal(role !== null),
    isAdmin: signal(role === 'ADMIN'),
    loading: signal(loading),
  };
}

describe('guestOnlyGuard', () => {
  it('allows guests to open auth pages', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: createSession(null) }],
    });

    const result = TestBed.runInInjectionContext(() => guestOnlyGuard({} as never, {} as never));

    expect(result).toBe(true);
  });

  it('redirects authenticated admins to /admin', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: createSession('ADMIN') }],
    });

    const router = TestBed.inject(Router);
    const result = TestBed.runInInjectionContext(() => guestOnlyGuard({} as never, {} as never));

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin');
  });

  it('redirects authenticated non-admins to /workspace', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: createSession('TENANT') }],
    });

    const router = TestBed.inject(Router);
    const result = TestBed.runInInjectionContext(() => guestOnlyGuard({} as never, {} as never));

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/workspace');
  });
});
