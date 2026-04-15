import {
  Component,
  computed,
  effect,
  ElementRef,
  HostListener,
  inject,
  signal,
} from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SessionStore } from '../../../core/state/session.store';
import { NotificationApi } from '../../../core/api/services/notification.api';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

type NavItem = {
  label: string;
  href: string;
  exact?: boolean;
};

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, TranslateModule],
  templateUrl: './app-header.component.html',
  styleUrl: './app-header.component.scss',
})
export class AppHeaderComponent {
  protected readonly session = inject(SessionStore);
  private readonly translate = inject(TranslateService);
  private readonly notificationApi = inject(NotificationApi);
  private readonly router = inject(Router);
  private readonly el = inject(ElementRef);

  protected readonly greeting = computed(() => this.session.user()?.firstName ?? 'Account');
  protected readonly userInitials = computed(() => {
    const user = this.session.user();
    if (!user) return '?';
    return `${user.firstName?.charAt(0) ?? ''}${user.lastName?.charAt(0) ?? ''}`.toUpperCase();
  });
  protected readonly menuOpen = signal(false);
  protected readonly langMenuOpen = signal(false);
  protected readonly currencyMenuOpen = signal(false);
  protected readonly profileMenuOpen = signal(false);
  protected readonly currentLang = signal(this.initLanguage());
  protected readonly currentCurrency = computed(() => this.session.currencyPreference());
  protected readonly unreadCount = signal(0);

  constructor() {
    this.translate.onLangChange.subscribe((event) => {
      this.currentLang.set(event.lang);
    });
    this.translate.onDefaultLangChange.subscribe((event) => {
      if (!this.translate.currentLang) {
        this.currentLang.set(event.lang);
      }
    });

    // Sync language from user's backend preference when authenticated
    effect(() => {
      const user = this.session.user();
      if (user?.languagePreference && user.languagePreference !== this.currentLang()) {
        this.translate.use(user.languagePreference);
        this.currentLang.set(user.languagePreference);
        document.dir = user.languagePreference === 'ar' ? 'rtl' : 'ltr';
        try {
          localStorage.setItem('homeflex_lang', user.languagePreference);
        } catch {}
      }
    });

    // Load unread notification count when user logs in
    effect(() => {
      if (this.session.user()) {
        this.loadUnreadCount();
      } else {
        this.unreadCount.set(0);
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.el.nativeElement.contains(event.target)) {
      this.langMenuOpen.set(false);
      this.currencyMenuOpen.set(false);
      this.profileMenuOpen.set(false);
    }
  }

  private loadUnreadCount(): void {
    this.notificationApi
      .getAll(true)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (res) => this.unreadCount.set(res.data?.length ?? 0),
        error: () => this.unreadCount.set(0),
      });
  }

  private initLanguage(): string {
    try {
      const saved = localStorage.getItem('homeflex_lang');
      if (saved && ['en', 'fr', 'es', 'ar'].includes(saved)) {
        this.translate.use(saved);
        document.dir = saved === 'ar' ? 'rtl' : 'ltr';
        return saved;
      }
    } catch {}
    return this.translate.currentLang || this.translate.defaultLang || 'en';
  }

  protected readonly currencies = [
    { code: 'XAF', symbol: 'FCFA', label: 'CFA Franc' },
    { code: 'USD', symbol: '$', label: 'US Dollar' },
    { code: 'EUR', symbol: '€', label: 'Euro' },
    { code: 'GBP', symbol: '£', label: 'British Pound' },
  ];

  protected readonly languages = [
    { code: 'en', label: 'English', flag: '🇺🇸' },
    { code: 'fr', label: 'Français', flag: '🇫🇷' },
    { code: 'es', label: 'Español', flag: '🇪🇸' },
    { code: 'ar', label: 'العربية', flag: '🇸🇦' },
  ];

  protected readonly navItems: NavItem[] = [
    { label: 'Home', href: '/', exact: true },
    { label: 'Properties', href: '/properties' },
    { label: 'Vehicles', href: '/vehicles' },
  ];

  protected switchLanguage(lang: string): void {
    this.translate.use(lang);
    this.currentLang.set(lang);
    document.dir = lang === 'ar' ? 'rtl' : 'ltr';
    this.langMenuOpen.set(false);
    try {
      localStorage.setItem('homeflex_lang', lang);
    } catch {}
  }

  protected switchCurrency(currencyCode: string): void {
    this.session.setCurrency(currencyCode);
    this.currencyMenuOpen.set(false);
  }

  protected toggleLangMenu(): void {
    this.langMenuOpen.update((v) => !v);
    this.currencyMenuOpen.set(false);
    this.profileMenuOpen.set(false);
  }

  protected toggleCurrencyMenu(): void {
    this.currencyMenuOpen.update((v) => !v);
    this.langMenuOpen.set(false);
    this.profileMenuOpen.set(false);
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update((v) => !v);
    this.langMenuOpen.set(false);
    this.currencyMenuOpen.set(false);
  }

  protected toggleMenu(): void {
    this.menuOpen.update((value) => !value);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected closeAllDropdowns(): void {
    this.langMenuOpen.set(false);
    this.currencyMenuOpen.set(false);
    this.profileMenuOpen.set(false);
  }

  protected logout(): void {
    this.closeAllDropdowns();
    this.closeMenu();
    this.session.logout().subscribe(() => {
      this.router.navigateByUrl('/');
    });
  }
}
