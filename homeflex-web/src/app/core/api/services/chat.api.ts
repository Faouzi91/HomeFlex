import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiListResponse, ChatRoom, Message } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class ChatApi extends BaseApi {
  createRoom(payload: {
    propertyId: string;
    tenantId: string;
    landlordId: string;
  }): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(`${this.baseUrl}/chat/rooms`, payload);
  }

  getRooms(): Observable<ApiListResponse<ChatRoom>> {
    return this.http.get<ApiListResponse<ChatRoom>>(`${this.baseUrl}/chat/rooms`);
  }

  getMessages(roomId: string): Observable<ApiListResponse<Message>> {
    return this.http.get<ApiListResponse<Message>>(`${this.baseUrl}/chat/rooms/${roomId}/messages`);
  }

  sendMessage(roomId: string, message: string): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/chat/rooms/${roomId}/messages`, { message });
  }

  markMessageAsRead(messageId: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/chat/messages/${messageId}/read`, {});
  }

  markRoomAsRead(roomId: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/chat/rooms/${roomId}/read`, {});
  }
}
