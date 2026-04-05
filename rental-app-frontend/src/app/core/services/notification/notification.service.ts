// ====================================
// notification.service.ts
// ====================================
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from 'src/app/environments/environment';
import { ApiListResponse } from 'src/app/types/api.types';

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: string;
  relatedEntityType?: string;
  relatedEntityId?: string;
  isRead: boolean;
  createdAt: Date;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getNotifications(unreadOnly: boolean = false): Observable<Notification[]> {
    return this.http
      .get<ApiListResponse<Notification>>(`${this.apiUrl}`, {
        params: { unreadOnly: unreadOnly.toString() },
      })
      .pipe(map((r) => r.data));
  }

  markAsRead(id: string): Observable<void> {
    return this.http
      .patch<void>(`${this.apiUrl}/${id}/read`, {})
      .pipe(tap(() => this.updateUnreadCount()));
  }

  markAllAsRead(): Observable<void> {
    return this.http
      .patch<void>(`${this.apiUrl}/read-all`, {})
      .pipe(tap(() => this.unreadCountSubject.next(0)));
  }

  deleteNotification(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  registerFCMToken(token: string, deviceType: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/fcm-token`, { token, deviceType });
  }

  updateUnreadCount(): void {
    this.getNotifications(true).subscribe((notifications) => {
      this.unreadCountSubject.next(notifications.length);
    });
  }
}
