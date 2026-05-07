import { TestBed } from '@angular/core/testing';
import { Router, UrlTree, provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { vi } from 'vitest';
import { workspaceGuard } from './workspace.guard';
import { SessionStore } from '../state/session.store';
import { User } from '../models/api.types';

function createSession(role: User['role'] | null) {
  const user =
    role !== null
      ? ({ role, email: 'user@homeflex.test', isActive: true } as Partial<User> as User)
      : null;

  return {
    init: vi.fn(),
    user: signal<User | null>(user),
    isAuthenticated: signal(role !== null),
    loading: signal(false),
  };
}

describe('workspaceGuard', () => {
  it('allows authenticated users to enter the workspace', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: createSession('TENANT') }],
    });

    const result = TestBed.runInInjectionContext(() =>
      workspaceGuard({} as never, { url: '/workspace/bookings' } as never),
    );

    expect(result).toBe(true);
  });

  it('redirects guests to login and preserves the target url', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: SessionStore, useValue: createSession(null) }],
    });

    const router = TestBed.inject(Router);
    const result = TestBed.runInInjectionContext(() =>
      workspaceGuard({} as never, { url: '/workspace/messages?room=abc' } as never),
    );

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      '/login?redirectUrl=%2Fworkspace%2Fmessages%3Froom%3Dabc',
    );
  });
});
