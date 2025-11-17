// ====================================
// error.interceptor.ts
// ====================================
import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { Router } from "@angular/router";
import { ToastController } from "@ionic/angular";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private toastController: ToastController
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = "An error occurred";

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
