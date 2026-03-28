// ====================================
// websocket.service.ts
// ====================================
import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Subject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient?: Client;
  private connectionStatus = new Subject<boolean>();
  public connectionStatus$ = this.connectionStatus.asObservable();

  constructor(private authService: AuthService) {}

  connect(): void {
    const token = this.authService.getToken();
    if (!token) {
      console.error('No auth token available');
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        if (environment.production) return;
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = () => {
      console.log('WebSocket connected');
      this.connectionStatus.next(true);
    };

    this.stompClient.onDisconnect = () => {
      console.log('WebSocket disconnected');
      this.connectionStatus.next(false);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      this.connectionStatus.next(false);
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connectionStatus.next(false);
    }
  }

  subscribe(destination: string): Observable<any> {
    return new Observable((observer) => {
      if (!this.stompClient) {
        observer.error('WebSocket not connected');
        return;
      }

      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          observer.next(JSON.parse(message.body));
        } catch {
          // Guard against non-JSON payloads coming from proxies or text frames.
          observer.next(message.body);
        }
      });

      return () => subscription.unsubscribe();
    });
  }

  send(destination: string, body: any): void {
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('Cannot send message: WebSocket not connected');
      return;
    }

    this.stompClient.publish({
      destination: destination,
      body: JSON.stringify(body),
    });
  }

  isConnected(): boolean {
    return this.stompClient?.connected || false;
  }
}
