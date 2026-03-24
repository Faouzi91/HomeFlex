import { Injectable, signal } from "@angular/core";
import { AuthService } from "../services/auth/auth.service";
import { User } from "../../models/user.model";

@Injectable({ providedIn: "root" })
export class AuthState {
  readonly currentUser = signal<User | null>(null);
  readonly isAuthenticated = signal<boolean>(false);

  constructor(private authService: AuthService) {
    this.hydrate();
  }

  hydrate(): void {
    const user = this.authService.getCurrentUser();
    this.currentUser.set(user);
    this.isAuthenticated.set(!!user && this.authService.isAuthenticated());
  }

  clear(): void {
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }
}
