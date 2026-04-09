import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ApiClient } from '../api/api.client';
import { SessionStore } from './session.store';

describe('SessionStore', () => {
  it('sets the authenticated user after login', () => {
    const api = {
      login: () =>
        of({
          user: {
            id: '1',
            email: 'tenant@test.com',
            firstName: 'Test',
            lastName: 'User',
            phoneNumber: null,
            profilePictureUrl: null,
            role: 'TENANT',
            isActive: true,
            isVerified: true,
            languagePreference: 'en',
            createdAt: new Date().toISOString(),
          },
        }),
    } as Partial<ApiClient>;

    TestBed.configureTestingModule({
      providers: [SessionStore, { provide: ApiClient, useValue: api }],
    });

    const store = TestBed.inject(SessionStore);
    store.login('tenant@test.com', 'password123').subscribe();

    expect(store.isAuthenticated()).toBe(true);
    expect(store.role()).toBe('TENANT');
  });
});
