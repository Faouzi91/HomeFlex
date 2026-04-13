import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiListResponse, ApiValueResponse, NotificationItem } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class NotificationApi extends BaseApi {
  getAll(unreadOnly = false): Observable<ApiListResponse<NotificationItem>> {
    return this.http.get<ApiListResponse<NotificationItem>>(`${this.baseUrl}/notifications`, {
      params: this.buildParams({ unreadOnly }),
    });
  }

  markRead(id: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/${id}/read`, {});
  }

  markAllRead(): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/read-all`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/notifications/${id}`);
  }

  registerFcmToken(token: string): Observable<ApiValueResponse<string>> {
    return this.http.post<ApiValueResponse<string>>(`${this.baseUrl}/notifications/fcm-token`, {
      token,
    });
  }
}
