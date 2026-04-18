import {
  Component,
  computed,
  DestroyRef,
  effect,
  ElementRef,
  HostListener,
  inject,
  signal,
} from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { NotificationApi } from '../../../core/api/services/notification.api';
import { NotificationItem } from '../../../core/models/api.types';
import { SessionStore } from '../../../core/state/session.store';
import { WorkspaceStore } from '../../../features/workspace/workspace.store';

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
  private readonly workspaceStore = inject(WorkspaceStore);
  private readonly translate = inject(TranslateService);
  private readonly router = inject(Router);
  private readonly el = inject(ElementRef);
  private readonly notificationApi = inject(NotificationApi);
  private readonly destroyRef = inject(DestroyRef);

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
  protected readonly notifMenuOpen = signal(false);
  protected readonly recentNotifications = signal<NotificationItem[]>([]);
  protected readonly currentLang = signal(this.initLanguage());
  protected readonly currentCurrency = computed(() => this.session.currencyPreference());

  protected readonly unreadCount = computed(
    () => this.workspaceStore.unreadNotificationCount() + this.workspaceStore.unreadMessageCount(),
  );

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

    // Trigger workspace load (loads counts) when user authenticates
    effect(() => {
      if (this.session.user()) {
        this.workspaceStore.load();
      } else {
        this.workspaceStore.reset();
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.el.nativeElement.contains(event.target)) {
      this.langMenuOpen.set(false);
      this.currencyMenuOpen.set(false);
      this.profileMenuOpen.set(false);
      this.notifMenuOpen.set(false);
    }
  }

  protected toggleNotifMenu(): void {
    const next = !this.notifMenuOpen();
    this.notifMenuOpen.set(next);
    this.langMenuOpen.set(false);
    this.currencyMenuOpen.set(false);
    this.profileMenuOpen.set(false);
    if (next) this.loadRecentNotifications();
  }

  private loadRecentNotifications(): void {
    this.notificationApi
      .getAll()
      .pipe(
        catchError(() => of({ data: [] as NotificationItem[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => this.recentNotifications.set(res.data.slice(0, 6)));
  }

  protected openNotification(n: NotificationItem): void {
    this.notifMenuOpen.set(false);
    if (!n.isRead) {
      this.notificationApi
        .markRead(n.id)
        .pipe(
          catchError(() => of(void 0)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe(() => this.workspaceStore.decrementUnreadNotifications());
    }
    const target = this.routeFor(n);
    if (target) {
      this.router.navigate(target.path, target.extras ?? {});
    } else {
      this.router.navigate(['/workspace/notifications']);
    }
  }

  protected openAllNotifications(): void {
    this.notifMenuOpen.set(false);
    this.router.navigate(['/workspace/notifications']);
  }

  private routeFor(n: NotificationItem): { path: any[]; extras?: any } | null {
    const type = (n.type ?? '').toUpperCase();
    const relType = (n.relatedEntityType ?? '').toUpperCase();
    const relId = n.relatedEntityId;
    if (type === 'MESSAGE' || relType === 'CHAT_ROOM' || relType === 'MESSAGE') {
      return {
        path: ['/workspace/messages'],
        extras: { queryParams: relId ? { room: relId } : {} },
      };
    }
    if (type === 'BOOKING' || relType === 'BOOKING' || relType === 'VEHICLE_BOOKING') {
      return {
        path: ['/workspace/bookings'],
        extras: { queryParams: relId ? { booking: relId } : {} },
      };
    }
    if (type === 'PAYMENT' || relType === 'PAYMENT') {
      return { path: ['/workspace/bookings'] };
    }
    if (relType === 'PROPERTY' && relId) return { path: ['/properties', relId] };
    if (relType === 'VEHICLE' && relId) return { path: ['/vehicles', relId] };
    return null;
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
    this.notifMenuOpen.set(false);
  }

  protected toggleCurrencyMenu(): void {
    this.currencyMenuOpen.update((v) => !v);
    this.langMenuOpen.set(false);
    this.profileMenuOpen.set(false);
    this.notifMenuOpen.set(false);
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update((v) => !v);
    this.langMenuOpen.set(false);
    this.currencyMenuOpen.set(false);
    this.notifMenuOpen.set(false);
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
    this.notifMenuOpen.set(false);
  }

  protected notifTime(iso: string): string {
    const diff = Date.now() - new Date(iso).getTime();
    const m = Math.floor(diff / 60000);
    if (m < 1) return 'just now';
    if (m < 60) return `${m}m`;
    const h = Math.floor(m / 60);
    if (h < 24) return `${h}h`;
    const d = Math.floor(h / 24);
    if (d < 7) return `${d}d`;
    return new Date(iso).toLocaleDateString();
  }

  protected logout(): void {
    this.closeAllDropdowns();
    this.closeMenu();
    this.session.logout().subscribe(() => {
      this.router.navigateByUrl('/');
    });
  }
}
