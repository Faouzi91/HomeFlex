import { Component, HostListener, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { TranslateService } from '@ngx-translate/core';
import { User } from 'src/app/models/user.model';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [CommonModule, IonicModule, TranslateModule, FormsModule],
})
export class HeaderComponent implements OnInit {
  user: User | null = null;
  selectedLang = 'en';

  userMenuOpen = false;
  mobileMenuOpen = false;
  scrolled = false;
  isUserAdmin = false;
  isAdminRoute = false;

  dropdownTop = 0;
  dropdownRight = 0;

  private currentUrl = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef
  ) {
    this.auth.currentUser$.subscribe((u) => {
      setTimeout(() => {
        this.user = u;
        this.isUserAdmin = u?.role === 'ADMIN';
        this.cdr.markForCheck();
      });
    });

    this.selectedLang = this.translate.currentLang || 'en';

    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        const url = e.urlAfterRedirects || e.url || '';
        setTimeout(() => {
          this.currentUrl = url;
          this.isAdminRoute =
            url === '/admin' || url.startsWith('/admin/') || url.startsWith('/admin?');
          this.mobileMenuOpen = false;
          this.userMenuOpen = false;
          this.cdr.markForCheck();
        });
      });
  }

  ngOnInit() {
    this.currentUrl = this.router.url || '';
    this.isAdminRoute = this.currentUrl.startsWith('/admin');
  }

  @HostListener('window:scroll')
  onScroll() {
    this.scrolled = window.scrollY > 8;
  }

  @HostListener('document:click', ['$event'])
  onDocClick(e: Event) {
    const t = e.target as HTMLElement;
    if (this.userMenuOpen && !t.closest('.dropdown') && !t.closest('.avatar-btn')) {
      this.userMenuOpen = false;
    }
    if (this.mobileMenuOpen && !t.closest('.mobile-menu') && !t.closest('.mobile-toggle')) {
      this.mobileMenuOpen = false;
    }
  }

  isRoute(path: string): boolean {
    return this.currentUrl.startsWith(path.split('?')[0]);
  }

  navigate(path: string) {
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.router.navigateByUrl(path);
  }

  logout() {
    this.userMenuOpen = false;
    this.mobileMenuOpen = false;
    this.auth.logout();
  }

  toggleUserMenu(event?: MouseEvent) {
    event?.stopPropagation();
    if (!this.userMenuOpen) this.positionDropdown();
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeUserMenu() {
    this.userMenuOpen = false;
  }

  toggleMobileMenu(event?: MouseEvent) {
    event?.stopPropagation();
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  changeLanguage(event: any) {
    const lang = event.target.value;
    this.translate.use(lang);
    localStorage.setItem('app_language', lang);
  }

  getUserInitials(): string {
    if (!this.user) return '';
    return `${this.user.firstName?.[0] || ''}${this.user.lastName?.[0] || ''}`.toUpperCase();
  }

  private positionDropdown() {
    const el = document.querySelector('.avatar-btn') as HTMLElement;
    if (el) {
      const r = el.getBoundingClientRect();
      this.dropdownTop = r.bottom + 6;
      this.dropdownRight = Math.max(16, window.innerWidth - r.right);
    }
  }
}
