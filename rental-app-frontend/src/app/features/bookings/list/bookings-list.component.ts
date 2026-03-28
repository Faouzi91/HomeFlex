import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService, Booking } from 'src/app/core/services/booking/booking.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  standalone: true,
  selector: 'app-bookings-list',
  imports: [CommonModule, IonicModule, TranslateModule],
  templateUrl: './bookings-list.component.html',
  styleUrls: ['./bookings-list.component.scss'],
})
export class BookingsListComponent implements OnInit {
  bookings: Booking[] = [];
  loading = false;
  isLandlord = false;

  constructor(
    private bookingService: BookingService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    this.isLandlord = !!user && user.role === 'LANDLORD';
    if (!user) {
      console.warn('Unauthenticated access to bookings');
      this.router.navigate(['/auth/login']);
      return;
    }
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    this.bookingService.getMyBookings().subscribe({
      next: (res) => {
        this.bookings = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load bookings', err);
        this.loading = false;
      },
    });
  }

  viewDetail(id: string): void {
    this.router.navigate(['/bookings', id]);
  }

  approve(id: string): void {
    this.bookingService.approveBooking(id).subscribe({
      next: () => this.loadBookings(),
      error: (err) => console.error('Approve error', err),
    });
  }

  reject(id: string): void {
    const reason = prompt('Rejection reason (optional)') || '';
    this.bookingService.rejectBooking(id, reason).subscribe({
      next: () => this.loadBookings(),
      error: (err) => console.error('Reject error', err),
    });
  }

  cancel(id: string): void {
    if (!confirm('Cancel this booking?')) return;
    this.bookingService.cancelBooking(id).subscribe({
      next: () => this.loadBookings(),
      error: (err) => console.error('Cancel error', err),
    });
  }
}
