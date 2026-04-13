import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthApi } from '../api/services/auth.api';
import { UserApi } from '../api/services/user.api';
import { SessionStore } from './session.store';

describe('SessionStore', () => {
  it('sets the authenticated user after login', () => {
    const authApi = {
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
    } as Partial<AuthApi>;

    const userApi = {} as Partial<UserApi>;

    TestBed.configureTestingModule({
      providers: [
        SessionStore,
        { provide: AuthApi, useValue: authApi },
        { provide: UserApi, useValue: userApi },
      ],
    });

    const store = TestBed.inject(SessionStore);
    store.login('tenant@test.com', 'password123').subscribe();

    expect(store.isAuthenticated()).toBe(true);
    expect(store.role()).toBe('TENANT');
  });
});
