// ====================================
// chat.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, Subject } from "rxjs";
import { Client, Message as StompMessage } from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import { environment } from "../../../environments/environment";
import { AuthService } from "../auth/auth.service";

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
  private stompClient?: Client;
  private messageSubject = new Subject<Message>();
  public messages$ = this.messageSubject.asObservable();

  constructor(private http: HttpClient, private authService: AuthService) {}

  connectWebSocket(): void {
    const token = this.authService.getToken();
    if (!token) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => console.log("STOMP:", str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = () => {
      console.log("WebSocket connected");
    };

    this.stompClient.onStompError = (frame) => {
      console.error("STOMP error:", frame);
    };

    this.stompClient.activate();
  }

  subscribeToRoom(roomId: string): void {
    if (!this.stompClient) return;

    this.stompClient.subscribe(
      `/topic/chat.${roomId}`,
      (message: StompMessage) => {
        const receivedMessage = JSON.parse(message.body);
        this.messageSubject.next(receivedMessage);
      }
    );
  }

  disconnectWebSocket(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
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
