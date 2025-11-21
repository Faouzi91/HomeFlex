import { Injectable } from "@angular/core";
import { Router, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthService } from "../services/auth/auth.service";

/**
 * PublicAccessGuard prevents ADMIN users from accessing public or tenant/landlord specific routes.
 * If the logged-in user is an ADMIN, they are redirected to the admin dashboard.
 * Otherwise (Guest, Tenant, Landlord), access is granted.
 */
@Injectable({
  providedIn: "root",
})
export class PublicAccessGuard {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    const user = this.authService.getCurrentUser();

    // Check if the user is logged in and is an Admin
    if (user && user.role === "ADMIN") {
      // If Admin, redirect them to the Admin dashboard and block access to the public page
      return this.router.createUrlTree(["/admin"]);
    }

    // For all other users (Guest, Tenant, Landlord), allow access
    return true;
  }
}
