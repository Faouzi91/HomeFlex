import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { ChatApi } from '../../../../core/api/services/chat.api';
import { ChatRoom, Message } from '../../../../core/models/api.types';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import { formatDateTime } from '../../../../core/utils/formatters';
import { initials } from '../../../../core/utils/formatters';

@Component({
  selector: 'app-messages-tab',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './messages-tab.component.html',
})
export class MessagesTabComponent {
  private readonly chatApi = inject(ChatApi);
  protected readonly session = inject(SessionStore);
  private readonly store = inject(WorkspaceStore);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly rooms = signal<ChatRoom[]>([]);
  protected readonly messages = signal<Message[]>([]);
  protected readonly selectedRoomId = signal('');
  protected readonly loading = signal(true);
  protected readonly sendingMessage = signal(false);

  protected readonly selectedRoom = computed(() =>
    this.rooms().find((r) => r.id === this.selectedRoomId()),
  );

  protected readonly messageForm = this.fb.group({
    text: ['', Validators.required],
  });

  constructor() {
    this.chatApi
      .getRooms()
      .pipe(
        catchError(() => of({ data: [] as ChatRoom[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.rooms.set(res.data);
        this.loading.set(false);
      });
  }

  protected openRoom(roomId: string): void {
    this.selectedRoomId.set(roomId);

    const room = this.rooms().find((r) => r.id === roomId);
    const unread = room?.unreadCount ?? 0;

    this.chatApi
      .getMessages(roomId)
      .pipe(
        catchError(() => of({ data: [] as Message[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.messages.set(res.data);
        if (unread > 0) {
          this.chatApi
            .markRoomAsRead(roomId)
            .pipe(
              catchError(() => of(void 0)),
              takeUntilDestroyed(this.destroyRef),
            )
            .subscribe(() => {
              this.rooms.update((rs) =>
                rs.map((r) => (r.id === roomId ? { ...r, unreadCount: 0 } : r)),
              );
              this.store.decrementUnreadMessages(unread);
            });
        }
      });
  }

  protected sendMessage(): void {
    if (this.messageForm.invalid || !this.selectedRoomId() || this.sendingMessage()) return;
    const text = this.messageForm.value.text!.trim();
    if (!text) return;

    this.sendingMessage.set(true);
    this.chatApi
      .sendMessage(this.selectedRoomId(), text)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (msg) => {
          this.messages.update((prev) => [...prev, msg]);
          this.messageForm.reset();
          this.sendingMessage.set(false);
        },
        error: () => this.sendingMessage.set(false),
      });
  }

  protected isMine(message: Message): boolean {
    return message.senderId === this.session.user()?.id;
  }

  protected roomInitials(room: ChatRoom): string {
    const other = this.session.isLandlord() ? room.tenant : room.landlord;
    return initials(other.firstName, other.lastName);
  }

  protected roomName(room: ChatRoom): string {
    const other = this.session.isLandlord() ? room.tenant : room.landlord;
    return `${other.firstName} ${other.lastName}`;
  }

  protected dt(val: string): string {
    return formatDateTime(val);
  }
}
