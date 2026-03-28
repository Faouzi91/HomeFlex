import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BookingService, Booking } from 'src/app/core/services/booking/booking.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-booking-detail',
  imports: [CommonModule, IonicModule, TranslateModule, RouterModule],
  templateUrl: './booking-detail.component.html',
  styleUrls: ['./booking-detail.component.scss'],
})
export class BookingDetailComponent implements OnInit {
  booking: Booking | null = null;
  loading = false;
  isLandlord = false;

  constructor(
    private route: ActivatedRoute,
    private bookingService: BookingService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    this.isLandlord = !!user && user.role === 'LANDLORD';
    if (!user) {
      console.warn('Unauthenticated access to booking detail');
      this.router.navigate(['/auth/login']);
      return;
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.loadBooking(id);
  }

  loadBooking(id: string): void {
    this.loading = true;
    this.bookingService.getBookingById(id).subscribe({
      next: (b) => {
        // Manually convert date strings to Date objects
        if (b.startDate) b.startDate = new Date(b.startDate);
        if (b.endDate) b.endDate = new Date(b.endDate);
        if (b.requestedDate) b.requestedDate = new Date(b.requestedDate);
        this.booking = b;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load booking', err);
        this.loading = false;
      },
    });
  }

  approve(): void {
    if (!this.booking) return;
    this.bookingService.approveBooking(this.booking.id).subscribe({
      next: () => this.loadBooking(this.booking!.id),
      error: (err) => console.error('Approve error', err),
    });
  }

  reject(): void {
    if (!this.booking) return;
    const reason = prompt('Rejection reason') || '';
    this.bookingService.rejectBooking(this.booking.id, reason).subscribe({
      next: () => this.loadBooking(this.booking!.id),
      error: (err) => console.error('Reject error', err),
    });
  }

  cancel(): void {
    if (!this.booking) return;
    this.bookingService.cancelBooking(this.booking.id).subscribe({
      next: () => this.router.navigate(['/bookings']),
      error: (err) => console.error('Cancel error', err),
    });
  }
}
