import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ChatService, Message } from 'src/app/core/services/chat/chat.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { IonicModule } from '@ionic/angular';
import { Subscription } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-chat-room',
  imports: [CommonModule, FormsModule, IonicModule],
  templateUrl: './chat-room.component.html',
  styleUrls: ['./chat-room.component.scss'],
})
export class ChatRoomComponent implements OnInit, OnDestroy {
  roomId: string | null = null;
  messages: Message[] = [];
  messageText = '';
  private sub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private chat: ChatService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    if (!user) {
      console.warn('Unauthenticated access to chat room');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.roomId = this.route.snapshot.paramMap.get('id');
    if (!this.roomId) {
      console.warn('No room ID provided');
      this.router.navigate(['/chat']);
      return;
    }

    // Ensure websocket connected
    this.chat.connectWebSocket();
    this.chat.subscribeToRoom(this.roomId);

    // load history
    this.chat.getChatMessages(this.roomId).subscribe({
      next: (msgs: Message[]) => {
        this.messages = msgs;
      },
      error: (err: any) => console.error('Failed loading messages', err),
    });

    // listen for new real-time messages
    this.sub = this.chat.messages$.subscribe((message: Message) => {
      this.messages = [...this.messages, message];
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.chat.disconnectWebSocket();
  }

  send(): void {
    if (!this.roomId || !this.messageText.trim()) return;
    this.chat.sendMessage(this.roomId, this.messageText).subscribe({
      next: (m: Message) => {
        // this.messages will be updated via messages$ subscription
        this.messageText = '';
      },
      error: (err: any) => console.error('Send message failed', err),
    });
  }
}
