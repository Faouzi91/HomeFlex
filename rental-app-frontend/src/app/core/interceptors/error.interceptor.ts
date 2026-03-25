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
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { Router } from "@angular/router";
import { ToastController } from "@ionic/angular";

export const errorInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<any> => {
  const router = inject(Router);
  const toastController = inject(ToastController);

  return next(request).pipe(
    catchError(async (error: HttpErrorResponse) => {
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
            errorMessage = "Unauthorized";
            router.navigate(["/auth/login"]);
            break;
          case 403:
            errorMessage = "Forbidden";
            break;
          case 404:
            errorMessage = "Not found";
            break;
          case 500:
            errorMessage = "Internal server error";
            break;
          default:
            errorMessage = `Error ${error.status}`;
        }
      }

      // Show toast notification
      const toast = await toastController.create({
        message: errorMessage,
        duration: 3000,
        position: "top",
        color: "danger",
      });
      await toast.present();

      return throwError(() => error);
    })
  );
};

        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = error.error.message;
        } else {
          // Server-side error
          switch (error.status) {
            case 400:
              errorMessage = error.error?.message || "Bad request";
              break;
            case 401:
              errorMessage = "Unauthorized. Please login again.";
              this.router.navigate(["/auth/login"]);
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
              errorMessage = error.error?.message || "Something went wrong";
          }
        }

        this.showToast(errorMessage);
        return throwError(() => error);
      })
    );
  }

  private async showToast(message: string): Promise<void> {
    const toast = await this.toastController.create({
      message: message,
      duration: 3000,
      color: "danger",
      position: "top",
    });
    toast.present();
  }
}
