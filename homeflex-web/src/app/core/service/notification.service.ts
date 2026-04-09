import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info' | 'warning';

export interface Notification {
  id: string;
  type: NotificationType;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  readonly notifications = signal<Notification[]>([]);
  readonly loading = signal<boolean>(false);

  show(message: string, type: NotificationType = 'info', duration: number = 5000): void {
    const id = Math.random().toString(36).substring(2);
    const notification: Notification = { id, type, message };

    this.notifications.update((prev) => [...prev, notification]);

    if (duration > 0) {
      setTimeout(() => this.dismiss(id), duration);
    }
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  info(message: string): void {
    this.show(message, 'info');
  }

  warning(message: string): void {
    this.show(message, 'warning');
  }

  dismiss(id: string): void {
    this.notifications.update((prev) => prev.filter((n) => n.id !== id));
  }

  setLoading(state: boolean): void {
    this.loading.set(state);
  }
}
