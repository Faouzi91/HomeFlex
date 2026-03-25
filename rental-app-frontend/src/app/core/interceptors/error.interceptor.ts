// ====================================
// error.interceptor.ts
// ====================================
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable, throwError, from } from "rxjs";
import { catchError, switchMap } from "rxjs/operators";
import { Router } from "@angular/router";
import { ToastController } from "@ionic/angular";

export const errorInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> => {
  const router = inject(Router);
  const toastController = inject(ToastController);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = "An error occurred";

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = error.error.message;
      } else {
        // Server-side error
        switch (error.status) {
          case 400:
            errorMessage = "Bad request";
            break;
          case 401:
            errorMessage = "Unauthorized. Please login again.";
            router.navigate(["/auth/login"]);
            break;
          case 403:
            errorMessage = "Access denied";
            break;
          case 404:
            errorMessage = "Resource not found";
            break;
          case 500:
            errorMessage = "Server error. Please try again later.";
            break;
          default:
            errorMessage = error.error?.message || `Error ${error.status}`;
        }
      }

      // Toast is async: create/present it, then rethrow original error.
      return from(
        toastController
          .create({
            message: errorMessage,
            duration: 3000,
            position: "top",
            color: "danger",
          })
          .then((toast) => toast.present())
      ).pipe(switchMap(() => throwError(() => error)));
    })
  );
};
