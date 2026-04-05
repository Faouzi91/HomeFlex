import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Subject, Observable } from 'rxjs';
import { environment } from 'src/app/environments/environment';

/**
 * WebSocket service using SockJS + STOMP.
 *
 * Authentication is handled via the httpOnly session cookie — SockJS uses
 * an HTTP-based transport (XHR/iframe) which includes cookies automatically.
 * No Bearer token header is needed.
 */
@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient?: Client;
  private connectionStatus = new Subject<boolean>();
  public connectionStatus$ = this.connectionStatus.asObservable();

  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;

  connect(): void {
    this.stompClient = new Client({
      // SockJS transport automatically includes cookies (withCredentials)
      webSocketFactory: () => new SockJS(environment.wsUrl),
      debug: (str) => {
        if (environment.production) return;
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = () => {
      this.reconnectAttempts = 0;
      this.connectionStatus.next(true);
    };

    this.stompClient.onDisconnect = () => {
      this.connectionStatus.next(false);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      this.connectionStatus.next(false);
    };

    this.stompClient.onWebSocketClose = () => {
      this.reconnectAttempts++;
      this.connectionStatus.next(false);

      if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.warn('WebSocket: max reconnect attempts reached, stopping auto-reconnect');
        this.stompClient?.deactivate();
      }
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient) {
      this.reconnectAttempts = this.maxReconnectAttempts; // prevent auto-reconnect
      this.stompClient.deactivate();
      this.connectionStatus.next(false);
    }
  }

  subscribe(destination: string): Observable<any> {
    return new Observable((observer) => {
      if (!this.stompClient || !this.stompClient.connected) {
        observer.error('WebSocket not connected');
        return;
      }

      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          observer.next(JSON.parse(message.body));
        } catch {
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
