import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';
import { SessionStore } from '../core/state/session.store';
import { NotificationService } from '../core/service/notification.service';
import { LoaderComponent } from '../shared/ui/loader/loader.component';
import { AlertComponent } from '../shared/ui/alert/alert.component';
import { AppFooterComponent } from './components/footer/app-footer.component';
import { AppHeaderComponent } from './components/header/app-header.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterLink,
    AppHeaderComponent,
    AppFooterComponent,
    LoaderComponent,
    AlertComponent,
  ],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.scss',
})
export class AppShellComponent {
  protected readonly session = inject(SessionStore);
  protected readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  /** Hide the consumer shell chrome (header, footer, support widget) on admin routes */
  protected readonly isAdminRoute = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map((e) => (e as NavigationEnd).urlAfterRedirects.startsWith('/admin')),
      startWith(this.router.url.startsWith('/admin')),
    ),
    { initialValue: false },
  );

  constructor() {
    this.session.init();
  }
}
