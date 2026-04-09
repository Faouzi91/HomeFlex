import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SessionStore } from '../../../core/state/session.store';

type NavItem = {
  label: string;
  href: string;
  exact?: boolean;
};

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './app-header.component.html',
  styleUrl: './app-header.component.scss',
})
export class AppHeaderComponent {
  protected readonly session = inject(SessionStore);
  protected readonly greeting = computed(() => this.session.user()?.firstName ?? 'Account');
  protected readonly menuOpen = signal(false);
  protected readonly navItems: NavItem[] = [
    { label: 'Home', href: '/', exact: true },
    { label: 'Properties', href: '/properties' },
    { label: 'Vehicles', href: '/vehicles' },
    { label: 'Workspace', href: '/workspace' },
  ];

  protected toggleMenu(): void {
    this.menuOpen.update((value) => !value);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
