// ====================================
// chat.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, Subject, Subscription } from "rxjs";
import { environment } from "../../../environments/environment";
import { WebSocketService } from "../websocket/websocket.service";

export interface ChatRoom {
  id: string;
  propertyId: string;
  propertyTitle: string;
  tenant: any;
  landlord: any;
  lastMessageAt?: Date;
  unreadCount: number;
}

export interface Message {
  id: string;
  chatRoomId: string;
  senderId: string;
  senderName: string;
  messageText: string;
  isRead: boolean;
  createdAt: Date;
}

@Injectable({
  providedIn: "root",
})
export class ChatService {
  private apiUrl = `${environment.apiUrl}/chat`;
  private roomSubscriptions = new Map<string, Subscription>();
  private messageSubject = new Subject<Message>();
  public messages$ = this.messageSubject.asObservable();

  constructor(private http: HttpClient, private webSocketService: WebSocketService) {}

  connectWebSocket(): void {
    if (!this.webSocketService.isConnected()) {
      this.webSocketService.connect();
    }
  }

  subscribeToRoom(roomId: string): void {
    if (this.roomSubscriptions.has(roomId)) {
      return;
    }
    const subscription = this.webSocketService
      .subscribe(`/topic/chat.${roomId}`)
      .subscribe((message: Message) => this.messageSubject.next(message));
    this.roomSubscriptions.set(roomId, subscription);
  }

  disconnectWebSocket(): void {
    this.roomSubscriptions.forEach((sub) => sub.unsubscribe());
    this.roomSubscriptions.clear();
    this.webSocketService.disconnect();
  }

  createOrGetChatRoom(request: {
    propertyId: string;
    tenantId: string;
    landlordId: string;
  }): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(`${this.apiUrl}/rooms`, request);
  }

  getMyChatRooms(): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(`${this.apiUrl}/rooms`);
  }

  getChatMessages(roomId: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/rooms/${roomId}/messages`);
  }

  sendMessage(roomId: string, message: string): Observable<Message> {
    return this.http.post<Message>(`${this.apiUrl}/rooms/${roomId}/messages`, {
      message: message,
    });
  }

  markMessageAsRead(messageId: string): Observable<void> {
    return this.http.patch<void>(
      `${this.apiUrl}/messages/${messageId}/read`,
      {}
    );
  }
}
