import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SessionStore } from '../core/state/session.store';
import { NotificationService } from '../core/service/notification.service';
import { LoaderComponent } from '../shared/ui/loader/loader.component';
import { AlertComponent } from '../shared/ui/alert/alert.component';
import { AppFooterComponent } from './components/footer/app-footer.component';
import { AppHeaderComponent } from './components/header/app-header.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AppHeaderComponent, AppFooterComponent, LoaderComponent, AlertComponent],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.scss',
})
export class AppShellComponent {
  protected readonly session = inject(SessionStore);
  protected readonly notificationService = inject(NotificationService);

  constructor() {
    this.session.init();
  }
}
