// ====================================
// core/interceptors/auth.interceptor.ts
// ====================================
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable, throwError } from "rxjs";
import { catchError, switchMap } from "rxjs/operators";
import { AuthService } from "../services/auth/auth.service";

let isRefreshing = false;
let refreshTokenSubject: string | null = null;

export const authInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !request.url.includes("auth/refresh")) {
        return handle401Error(request, next);
      }
      return throwError(() => error);
    })
  );
};

function handle401Error(
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> {
  const authService = inject(AuthService);

  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject = null;

    return authService.refreshToken().pipe(
      catchError((err) => {
        isRefreshing = false;
        authService.logout();
        return throwError(() => err);
      }),
      switchMap((response: any) => {
        isRefreshing = false;
        refreshTokenSubject = response.token;
        return next(
          request.clone({
            setHeaders: {
              Authorization: `Bearer ${response.token}`,
            },
          })
        );
      })
    );
  } else {
    // Wait for token refresh to complete
    return throwError(() => new Error("Token refresh in progress"));
  }
}
