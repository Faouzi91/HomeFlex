import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const allowedRoles: string[] = route.data['roles'] || [];
    const user = this.auth.getCurrentUser();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    if (allowedRoles.length === 0) return true;

    if (allowedRoles.includes(user.role)) return true;

    // unauthorized - redirect to home
    this.router.navigate(['/']);
    return false;
  }
}
