import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError, from } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';

export const errorInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> => {
  const router = inject(Router);
  const toastController = inject(ToastController);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = error.error.message;
      } else {
        // Extract message from backend ErrorResponse structure
        const serverMessage = error.error?.message || error.error?.error || null;

        switch (error.status) {
          case 0:
            errorMessage = 'Unable to connect to server. Please check your connection.';
            break;
          case 400:
            errorMessage = serverMessage || 'Bad request';
            break;
          case 401:
            // Don't toast on 401 — the auth interceptor handles refresh/logout
            return throwError(() => error);
          case 403:
            errorMessage = serverMessage || 'Access denied';
            break;
          case 404:
            errorMessage = serverMessage || 'Resource not found';
            break;
          case 409:
            errorMessage = serverMessage || 'Conflict — the resource was already modified';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = serverMessage || `Error ${error.status}`;
        }
      }

      return from(
        toastController
          .create({
            message: errorMessage,
            duration: 3000,
            position: 'top',
            color: 'danger',
          })
          .then((toast) => toast.present())
      ).pipe(switchMap(() => throwError(() => error)));
    })
  );
};
