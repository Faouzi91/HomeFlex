import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { User, LoginRequest, AuthResponse, RegisterRequest } from 'src/app/models/user.model';

/**
 * Cookie-based authentication service.
 *
 * Tokens are stored in httpOnly cookies set by the backend —
 * the browser attaches them automatically via `withCredentials: true`.
 * Only the user profile is kept in memory/localStorage.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private userKey = 'current_user';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem(this.userKey);
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        this.currentUserSubject.next(user);
      } catch (e) {
        localStorage.removeItem(this.userKey);
      }
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials, {
        withCredentials: true,
      })
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, data, {
        withCredentials: true,
      })
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  googleLogin(idToken: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(
        `${environment.apiUrl}/auth/google`,
        { idToken },
        { withCredentials: true }
      )
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem(this.userKey, JSON.stringify(response.user));
    this.currentUserSubject.next(response.user);
  }

  logout(): void {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true }).subscribe({
      complete: () => {
        localStorage.removeItem(this.userKey);
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        localStorage.removeItem(this.userKey);
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
      },
    });
  }

  /**
   * Authentication is determined by the presence of a stored user profile.
   * The actual token validity is enforced server-side via the httpOnly cookie.
   */
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  updateCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, {}, { withCredentials: true })
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  forgotPassword(email: string): Observable<{ message: string } | any> {
    return this.http.post<{ message: string }>(`${environment.apiUrl}/auth/forgot-password`, {
      email,
    });
  }

  resetPassword(token: string, newPassword: string): Observable<{ message: string } | any> {
    return this.http.post<{ message: string }>(`${environment.apiUrl}/auth/reset-password`, {
      token,
      newPassword,
    });
  }
}
