import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SessionStore } from '../../../core/state/session.store';

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
  protected readonly greeting = computed(() => this.session.user()?.firstName ?? 'Account');
  protected readonly menuOpen = signal(false);
  protected readonly currentLang = signal(this.translate.currentLang || 'en');

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
    { label: 'Workspace', href: '/workspace' },
  ];

  protected switchLanguage(lang: string): void {
    this.translate.use(lang);
    this.currentLang.set(lang);
    document.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }

  protected toggleMenu(): void {
    this.menuOpen.update((value) => !value);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
