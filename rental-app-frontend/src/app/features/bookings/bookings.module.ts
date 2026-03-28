import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { RoleGuard } from 'src/app/core/guards/role.guard';
import { IonicModule } from '@ionic/angular';

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./list/bookings-list.component').then((m) => m.BookingsListComponent),
  },
  {
    path: ':id',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./detail/booking-detail.component').then((m) => m.BookingDetailComponent),
  },
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(routes), IonicModule],
})
export class BookingsModule {}
