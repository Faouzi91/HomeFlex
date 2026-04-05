import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth/auth.service';
import { environment } from 'src/app/environments/environment';

let isRefreshing = false;

/**
 * Attaches `withCredentials: true` to every API request so the browser
 * sends the httpOnly auth cookies automatically. On a 401, attempts
 * a single token refresh before logging the user out.
 */
export const authInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> => {
  // Only attach credentials to our own API
  if (request.url.startsWith(environment.apiUrl)) {
    request = request.clone({ withCredentials: true });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !request.url.includes('auth/refresh')) {
        return handle401Error(request, next);
      }
      return throwError(() => error);
    })
  );
};

function handle401Error(request: HttpRequest<any>, next: HttpHandlerFn): Observable<any> {
  const authService = inject(AuthService);

  if (!isRefreshing) {
    isRefreshing = true;

    return authService.refreshToken().pipe(
      catchError((err) => {
        isRefreshing = false;
        authService.logout();
        return throwError(() => err);
      }),
      switchMap(() => {
        isRefreshing = false;
        // Retry the original request — cookies are updated by the refresh response
        return next(request.clone({ withCredentials: true }));
      })
    );
  } else {
    return throwError(() => new Error('Token refresh in progress'));
  }
}
