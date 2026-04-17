import { Injectable, inject, signal } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { ChatApi } from '../../core/api/services/chat.api';
import { NotificationApi } from '../../core/api/services/notification.api';
import { PropertyApi } from '../../core/api/services/property.api';
import { VehicleApi } from '../../core/api/services/vehicle.api';
import { ChatRoom, NotificationItem, Property, Vehicle } from '../../core/models/api.types';
import { SessionStore } from '../../core/state/session.store';

@Injectable({ providedIn: 'root' })
export class WorkspaceStore {
  private readonly propertyApi = inject(PropertyApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly notificationApi = inject(NotificationApi);
  private readonly chatApi = inject(ChatApi);
  private readonly session = inject(SessionStore);

  readonly myProperties = signal<Property[]>([]);
  readonly myVehicles = signal<Vehicle[]>([]);
  readonly unreadNotificationCount = signal(0);
  readonly unreadMessageCount = signal(0);
  readonly loaded = signal(false);

  load(): void {
    if (this.loaded()) return;
    const isHost = this.session.isLandlord() || this.session.isAdmin();

    forkJoin({
      notifications: this.notificationApi
        .getAll()
        .pipe(catchError(() => of({ data: [] as NotificationItem[] }))),
      rooms: this.chatApi.getRooms().pipe(catchError(() => of({ data: [] as ChatRoom[] }))),
      properties: isHost
        ? this.propertyApi.getMine().pipe(catchError(() => of({ data: [] as Property[] })))
        : of({ data: [] as Property[] }),
      vehicles: isHost
        ? this.vehicleApi
            .getMine()
            .pipe(
              catchError(() =>
                of({ data: [] as Vehicle[], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
              ),
            )
        : of({ data: [] as Vehicle[], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
    }).subscribe((res) => {
      this.myProperties.set(res.properties.data);
      this.myVehicles.set(res.vehicles.data);
      this.unreadNotificationCount.set(res.notifications.data.filter((n) => !n.isRead).length);
      this.unreadMessageCount.set(res.rooms.data.reduce((acc, r) => acc + (r.unreadCount ?? 0), 0));
      this.loaded.set(true);
    });
  }

  reset(): void {
    this.myProperties.set([]);
    this.myVehicles.set([]);
    this.unreadNotificationCount.set(0);
    this.unreadMessageCount.set(0);
    this.loaded.set(false);
  }

  refreshProperties(): void {
    if (!this.session.isLandlord() && !this.session.isAdmin()) return;
    this.propertyApi
      .getMine()
      .pipe(catchError(() => of({ data: [] as Property[] })))
      .subscribe((res) => this.myProperties.set(res.data));
  }

  refreshVehicles(): void {
    if (!this.session.isLandlord() && !this.session.isAdmin()) return;
    this.vehicleApi
      .getMine()
      .pipe(
        catchError(() =>
          of({ data: [] as Vehicle[], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
        ),
      )
      .subscribe((res) => this.myVehicles.set(res.data));
  }

  decrementUnreadNotifications(count = 1): void {
    this.unreadNotificationCount.update((n) => Math.max(0, n - count));
  }

  clearUnreadNotifications(): void {
    this.unreadNotificationCount.set(0);
  }
}
