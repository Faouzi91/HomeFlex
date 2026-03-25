/* header.component.ts
   - Use two separate flags: isUserAdmin (role) and isAdminRoute (URL).
   - Defer subscription updates with setTimeout(...) to avoid NG0900.
   - Stop propagation is handled in the template (see below).
*/

import {
  Component,
  HostListener,
  OnInit,
  ChangeDetectorRef,
} from "@angular/core";
import { Router, NavigationEnd } from "@angular/router";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { TranslateService } from "@ngx-translate/core";
import { User } from "src/app/models/user.model";
import { filter } from "rxjs/operators";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { TranslateModule } from "@ngx-translate/core";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
  standalone: true,
  imports: [CommonModule, IonicModule, TranslateModule]
})
export class HeaderComponent implements OnInit {
  user: User | null = null;
  selectedLang = "en";

  // UI state
  userMenuOpen = false;
  mobileMenuOpen = false;

  // Separated concerns
  isUserAdmin = false; // derived *only* from auth.currentUser$
  isAdminRoute = false; // derived *only* from router url

  isMobile = false;

  dropdownTop = 0;
  dropdownRight = 0;

  constructor(
    private auth: AuthService,
    private router: Router,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef
  ) {
    // AUTH subscription -> only sets isUserAdmin (role). Defer update to next tick.
    this.auth.currentUser$.subscribe((u) => {
      // defer to avoid updating bindings during CD cycle (prevents NG0900)
      setTimeout(() => {
        this.user = u;
        this.isUserAdmin = u?.role === "ADMIN";
        this.cdr.markForCheck();
      });
    });

    this.selectedLang = this.translate.currentLang || "en";

    // ROUTER subscription -> only sets isAdminRoute (URL). Use NavigationEnd.
    this.router.events
      .pipe(
        filter(
          (event): event is NavigationEnd => event instanceof NavigationEnd
        )
      )
      .subscribe((event: NavigationEnd) => {
        const url = event.urlAfterRedirects || event.url || "";

        // compute route-only flag
        const newIsAdminRoute =
          url === "/admin" ||
          url.startsWith("/admin/") ||
          url.startsWith("/admin?") ||
          url.includes("/admin/");

        // defer update to next tick to avoid ExpressionChanged errors
        setTimeout(() => {
          // update route flag
          this.isAdminRoute = newIsAdminRoute;

          // close mobile menu when navigation completes (expected UX),
          // but we do NOT prevent reopening it on the new route
          if (this.mobileMenuOpen) {
            this.mobileMenuOpen = false;
          }

          this.cdr.markForCheck();
        });
      });
  }

  ngOnInit() {
    this.isMobile = window.innerWidth < 1024;

    // set initial route flag (first paint)
    const url = this.router.url || "";
    this.isAdminRoute =
      url === "/admin" ||
      url.startsWith("/admin/") ||
      url.startsWith("/admin?");
  }

  @HostListener("window:resize")
  onResize() {
    const nowMobile = window.innerWidth < 1024;
    if (this.isMobile !== nowMobile) {
      this.isMobile = nowMobile;

      // close mobile nav on desktop to keep consistent
      if (!this.isMobile && this.mobileMenuOpen) {
        this.mobileMenuOpen = false;
      }

      this.cdr.markForCheck();
    }
  }

  // document click closes menus when clicking outside
  @HostListener("document:click", ["$event"])
  closeMenus(event: any) {
    const target = event.target as HTMLElement;

    const insideUserDropdown =
      !!target.closest(".user-dropdown") || !!target.closest(".user-menu");
    if (!insideUserDropdown && this.userMenuOpen) {
      this.userMenuOpen = false;
    }

    const clickedToggle = !!target.closest(".mobile-menu-btn");
    const clickedMobileNav = !!target.closest(".mobile-nav");

    if (this.mobileMenuOpen && !clickedToggle && !clickedMobileNav) {
      this.mobileMenuOpen = false;
    }
  }

  // navigate: guard against non-admin trying to go to admin urls (UX only; guards still enforce)
  navigate(path: string) {
    if (path.startsWith("/admin") && !this.isUserAdmin) {
      this.router.navigate(["/"]);
      return;
    }

    // close small UI panels and navigate
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.router.navigate([path]);
  }

  logout() {
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.auth.logout();
    this.router.navigate(["/auth/login"]);
  }

  toggleUserMenu(event?: MouseEvent) {
    if (event) event.stopPropagation(); // prevents document:click handler running immediately
    if (!this.userMenuOpen) this.calculateDropdownPosition();
    this.userMenuOpen = !this.userMenuOpen;
  }

  calculateDropdownPosition() {
    const avatarButton = document.querySelector(".user-avatar") as HTMLElement;
    if (avatarButton) {
      const rect = avatarButton.getBoundingClientRect();
      this.dropdownTop = rect.bottom + 8;
      this.dropdownRight = window.innerWidth - rect.right;
      if (this.dropdownRight < 16) this.dropdownRight = 16;
    }
  }

  closeUserMenu() {
    this.userMenuOpen = false;
  }

  // Mobile menu toggle: stopPropagation from template; only works when isMobile true
  toggleMobileMenu(event?: MouseEvent) {
    if (event) event.stopPropagation();
    if (!this.isMobile) return;
    this.mobileMenuOpen = !this.mobileMenuOpen;
    this.cdr.markForCheck();
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
