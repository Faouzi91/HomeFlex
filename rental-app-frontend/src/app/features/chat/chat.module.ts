import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { IonicModule } from '@ionic/angular';

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () => import('./list/chat-list.component').then((m) => m.ChatListComponent),
  },
  {
    path: ':id',
    canActivate: [AuthGuard],
    loadComponent: () => import('./room/chat-room.component').then((m) => m.ChatRoomComponent),
  },
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(routes), IonicModule],
})
export class ChatModule {}
