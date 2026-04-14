import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { EMPTY, Observable, catchError, finalize, map, of, tap } from 'rxjs';
import { AuthApi } from '../api/services/auth.api';
import { UserApi } from '../api/services/user.api';
import { User } from '../models/api.types';
import { NotificationService } from '../service/notification.service';

@Injectable({ providedIn: 'root' })
export class SessionStore {
  private readonly authApi = inject(AuthApi);
  private readonly userApi = inject(UserApi);
  private readonly notifications = inject(NotificationService);

  readonly user = signal<User | null>(null);
  readonly loading = signal(true);
  readonly pending = signal(false);
  readonly error = signal<string | null>(null);
  readonly initialized = signal(false);
  readonly currencyPreference = signal<string>('XAF');

  readonly isAuthenticated = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role ?? 'GUEST');
  readonly isTenant = computed(() => this.role() === 'TENANT');
  readonly isLandlord = computed(() => this.role() === 'LANDLORD');
  readonly isAdmin = computed(() => this.role() === 'ADMIN');
  readonly roleLabel = computed(() => {
    const roleMap: Record<string, string> = {
      ADMIN: 'Platform Admin',
      LANDLORD: 'Host',
      TENANT: 'Renter',
      GUEST: 'Guest',
    };
    return roleMap[this.role()] ?? this.role();
  });
  readonly displayName = computed(() => {
    const current = this.user();
    return current ? `${current.firstName} ${current.lastName}`.trim() : 'Guest';
  });

  setCurrency(currency: string): void {
    this.currencyPreference.set(currency);
    try {
      localStorage.setItem('homeflex_currency', currency);
    } catch {}
  }

  init(): void {
    if (this.initialized()) {
      return;
    }

    try {
      const savedCurrency = localStorage.getItem('homeflex_currency');
      if (savedCurrency) {
        this.currencyPreference.set(savedCurrency);
      }
    } catch {}

    this.initialized.set(true);
    this.loading.set(true);

    this.userApi
      .getMe()
      .pipe(
        tap((user) => this.user.set(user)),
        catchError((error) => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            return this.authApi.refresh().pipe(
              map((response) => response.user),
              tap((user) => this.user.set(user)),
              catchError(() => {
                this.user.set(null);
                return of(null);
              }),
            );
          }

          this.user.set(null);
          return of(null);
        }),
        finalize(() => this.loading.set(false)),
      )
      .subscribe();
  }

  login(email: string, password: string): Observable<void> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.authApi.login({ email, password }).pipe(
      tap((response) => {
        this.user.set(response.user);
        this.notifications.success('Welcome back to HomeFlex!');
      }),
      map(() => void 0),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  socialLogin(provider: string, token: string): Observable<void> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.authApi.socialLogin(provider, token).pipe(
      tap((response) => {
        this.user.set(response.user);
        this.notifications.success('Social login successful!');
      }),
      map(() => void 0),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  register(payload: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string | null;
    role: string;
  }): Observable<void> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.authApi.register(payload).pipe(
      tap((response) => {
        this.user.set(response.user);
        this.notifications.success('Account created successfully!');
      }),
      map(() => void 0),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  forgotPassword(email: string): Observable<string> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.authApi.forgotPassword(email).pipe(
      map((response) => response.data),
      tap(() => this.notifications.info('Reset instructions sent to your email.')),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  logout(): Observable<void> {
    this.pending.set(true);
    this.notifications.setLoading(true);

    return this.authApi.logout().pipe(
      tap(() => {
        this.user.set(null);
        this.notifications.info('You have been logged out.');
      }),
      map(() => void 0),
      catchError(() => {
        this.user.set(null);
        return of(void 0);
      }),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  updateProfile(payload: {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string | null;
    languagePreference?: string;
    emailNotificationsEnabled?: boolean;
    pushNotificationsEnabled?: boolean;
    smsNotificationsEnabled?: boolean;
  }): Observable<User> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.userApi.updateProfile(payload).pipe(
      tap((user) => {
        this.user.set(user);
        this.notifications.success('Profile updated successfully.');
      }),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  changePassword(payload: { currentPassword: string; newPassword: string }): Observable<string> {
    this.pending.set(true);
    this.notifications.setLoading(true);
    this.error.set(null);

    return this.userApi.changePassword(payload).pipe(
      map((response) => response.data),
      tap(() => this.notifications.success('Password changed successfully.')),
      catchError((error) => this.handleError(error)),
      finalize(() => {
        this.pending.set(false);
        this.notifications.setLoading(false);
      }),
    );
  }

  private handleError(error: unknown): Observable<never> {
    let message = 'Something went wrong. Please try again.';

    if (error instanceof HttpErrorResponse) {
      const payload = error.error as { message?: string } | string | null;
      if (typeof payload === 'string') {
        message = payload;
      } else if (payload?.message) {
        message = payload.message;
      } else if (error.status === 401) {
        message = 'Your session could not be verified.';
      } else if (error.status === 403) {
        message = 'This action is not allowed for your role.';
      }
    }

    this.error.set(message);
    this.notifications.error(message);
    return EMPTY;
  }
}
