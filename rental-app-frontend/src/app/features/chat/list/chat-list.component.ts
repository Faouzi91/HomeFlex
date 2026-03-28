import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatService, ChatRoom } from 'src/app/core/services/chat/chat.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';

@Component({
  standalone: true,
  selector: 'app-chat-list',
  imports: [CommonModule, IonicModule],
  templateUrl: './chat-list.component.html',
  styleUrls: ['./chat-list.component.scss'],
})
export class ChatListComponent implements OnInit {
  rooms: ChatRoom[] = [];
  loading = false;

  constructor(
    private chatService: ChatService,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    if (!user) {
      console.warn('Unauthenticated access to chat list');
      this.router.navigate(['/auth/login']);
      return;
    }
    this.loadRooms();
  }

  loadRooms(): void {
    this.loading = true;
    this.chatService.getMyChatRooms().subscribe({
      next: (res: ChatRoom[]) => {
        this.rooms = res;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load chat rooms', err);
        this.loading = false;
      },
    });
  }

  openRoom(id: string): void {
    this.router.navigate(['/chat', id]);
  }
}
