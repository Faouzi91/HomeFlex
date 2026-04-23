import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { NotificationApi } from '../../../../core/api/services/notification.api';
import { NotificationItem } from '../../../../core/models/api.types';
import { WorkspaceStore } from '../../workspace.store';
import { formatDateTime } from '../../../../core/utils/formatters';
import { getNotificationNavigationTarget } from '../../../../core/utils/notification-routing';

@Component({
  selector: 'app-notifications-tab',
  standalone: true,
  imports: [NgTemplateOutlet],
  templateUrl: './notifications-tab.component.html',
})
export class NotificationsTabComponent {
  private readonly notificationApi = inject(NotificationApi);
  private readonly store = inject(WorkspaceStore);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  protected readonly notifications = signal<NotificationItem[]>([]);
  protected readonly loading = signal(true);

  protected readonly today = computed(() =>
    this.notifications().filter((n) => this.isToday(n.createdAt)),
  );

  protected readonly earlier = computed(() =>
    this.notifications().filter((n) => !this.isToday(n.createdAt)),
  );

  protected readonly unreadCount = computed(
    () => this.notifications().filter((n) => !n.isRead).length,
  );

  constructor() {
    this.notificationApi
      .getAll()
      .pipe(
        catchError(() => of({ data: [] as NotificationItem[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.notifications.set(res.data);
        this.loading.set(false);
      });
  }

  protected openNotification(n: NotificationItem): void {
    if (!n.isRead) this.markRead(n.id);
    const target = getNotificationNavigationTarget(n);
    if (target) this.router.navigate(target.path, target.extras ?? {});
  }

  protected markRead(id: string): void {
    this.notificationApi
      .markRead(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.notifications.update((items) =>
          items.map((n) => (n.id === id ? { ...n, isRead: true } : n)),
        );
        this.store.decrementUnreadNotifications();
      });
  }

  protected markAllRead(): void {
    this.notificationApi
      .markAllRead()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.notifications.update((items) => items.map((n) => ({ ...n, isRead: true })));
        this.store.clearUnreadNotifications();
      });
  }

  protected delete(id: string): void {
    const n = this.notifications().find((x) => x.id === id);
    this.notificationApi
      .delete(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (n && !n.isRead) this.store.decrementUnreadNotifications();
        this.notifications.update((items) => items.filter((x) => x.id !== id));
      });
  }

  protected dt(val: string): string {
    return formatDateTime(val);
  }

  protected typeIcon(type: string): string {
    const icons: Record<string, string> = {
      BOOKING:
        'M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5',
      MESSAGE:
        'M8.625 12a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H8.25m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H12m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25Z',
      PAYMENT:
        'M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Z',
    };
    return (
      icons[type] ??
      'M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0'
    );
  }

  private isToday(dateStr: string): boolean {
    const d = new Date(dateStr);
    const now = new Date();
    return (
      d.getFullYear() === now.getFullYear() &&
      d.getMonth() === now.getMonth() &&
      d.getDate() === now.getDate()
    );
  }
}
