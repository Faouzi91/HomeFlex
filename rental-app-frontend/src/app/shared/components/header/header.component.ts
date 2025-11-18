import { Component, HostListener } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { TranslateService } from "@ngx-translate/core";
import { User } from "src/app/models/user.model";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent {
  user: User | null = null;
  selectedLang = "en";
  userMenuOpen = false;
  mobileMenuOpen = false;

  dropdownTop = 0;
  dropdownRight = 0;

  constructor(
    private auth: AuthService,
    private router: Router,
    private translate: TranslateService
  ) {
    this.auth.currentUser$.subscribe((u) => (this.user = u));
    this.selectedLang = this.translate.currentLang || "en";
  }

  @HostListener("document:click", ["$event"])
  closeMenus(event: any) {
    if (!event.target.closest(".user-menu")) {
      this.userMenuOpen = false;
    }
  }

  navigate(path: string) {
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.router.navigate([path]);
  }

  logout() {
    this.userMenuOpen = false;
    this.auth.logout();
    this.router.navigate(["/auth/login"]);
  }

  toggleUserMenu(): void {
    if (!this.userMenuOpen) {
      // Calculate dropdown position when opening
      this.calculateDropdownPosition();
    }
    this.userMenuOpen = !this.userMenuOpen;
  }

  calculateDropdownPosition(): void {
    // Get the avatar button element
    const avatarButton = document.querySelector(".user-avatar") as HTMLElement;
    if (avatarButton) {
      const rect = avatarButton.getBoundingClientRect();

      // Position dropdown below avatar with some spacing
      this.dropdownTop = rect.bottom + 8; // 8px gap

      // Align dropdown to the right of the avatar
      // Account for dropdown width (16rem = 256px)
      this.dropdownRight = window.innerWidth - rect.right;

      // Ensure dropdown doesn't go off-screen on mobile
      if (this.dropdownRight < 16) {
        this.dropdownRight = 16; // 16px minimum margin from right edge
      }
    }
  }

  closeUserMenu(): void {
    this.userMenuOpen = false;
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  changeLanguage(event: any) {
    const lang = event.target.value;
    this.translate.use(lang);
    localStorage.setItem("app_language", lang);
  }

  getUserInitials(): string {
    if (!this.user) return "";
    return `${this.user.firstName?.[0] || ""}${
      this.user.lastName?.[0] || ""
    }`.toUpperCase();
  }
}
