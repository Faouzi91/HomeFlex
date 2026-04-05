import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VehicleService } from 'src/app/core/services/vehicle/vehicle.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { ToastService } from 'src/app/core/services/toast/toast.service';
import { Vehicle } from 'src/app/models/vehicle.model';

@Component({
  selector: 'app-vehicle-detail',
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule],
  templateUrl: './vehicle-detail.component.html',
  styleUrls: ['./vehicle-detail.component.scss'],
})
export class VehicleDetailComponent implements OnInit {
  vehicle: Vehicle | null = null;
  loading = true;
  currentImageIndex = 0;

  // Booking form
  showBookingForm = false;
  bookingStartDate = '';
  bookingEndDate = '';
  bookingMessage = '';
  isAvailable: boolean | null = null;
  checkingAvailability = false;
  submittingBooking = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private vehicleService: VehicleService,
    private authService: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/vehicles']);
      return;
    }

    this.vehicleService.getById(id).subscribe({
      next: (vehicle) => {
        this.vehicle = vehicle;
        this.loading = false;
        this.vehicleService.incrementViewCount(id).subscribe();
      },
      error: () => {
        this.loading = false;
        this.toast.error('Vehicle not found.');
        this.router.navigate(['/vehicles']);
      },
    });
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getCurrentImage(): string {
    return (
      this.vehicle?.images?.[this.currentImageIndex]?.imageUrl ||
      '/assets/images/placeholder-vehicle.jpg'
    );
  }

  nextImage(): void {
    if (this.vehicle?.images && this.currentImageIndex < this.vehicle.images.length - 1) {
      this.currentImageIndex++;
    }
  }

  prevImage(): void {
    if (this.currentImageIndex > 0) {
      this.currentImageIndex--;
    }
  }

  openBookingForm(): void {
    if (!this.isAuthenticated) {
      this.toast.error('Please log in to book a vehicle.');
      this.router.navigate(['/auth/login']);
      return;
    }
    this.showBookingForm = true;
    this.isAvailable = null;
  }

  get todayDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  get dateError(): string | null {
    if (!this.bookingStartDate || !this.bookingEndDate) return null;
    if (this.bookingStartDate < this.todayDate) return 'Start date cannot be in the past.';
    if (this.bookingEndDate < this.bookingStartDate)
      return 'End date must be on or after start date.';
    return null;
  }

  get datesValid(): boolean {
    return !!this.bookingStartDate && !!this.bookingEndDate && !this.dateError;
  }

  checkAvailability(): void {
    if (!this.vehicle || !this.datesValid) return;

    this.checkingAvailability = true;
    this.vehicleService
      .checkAvailability(this.vehicle.id, this.bookingStartDate, this.bookingEndDate)
      .subscribe({
        next: (available) => {
          this.isAvailable = available;
          this.checkingAvailability = false;
        },
        error: () => {
          this.checkingAvailability = false;
          this.toast.error('Could not check availability.');
        },
      });
  }

  submitBooking(): void {
    if (!this.vehicle || !this.datesValid) return;

    this.submittingBooking = true;
    this.vehicleService
      .createBooking({
        vehicleId: this.vehicle.id,
        startDate: this.bookingStartDate,
        endDate: this.bookingEndDate,
        message: this.bookingMessage || undefined,
      })
      .subscribe({
        next: () => {
          this.submittingBooking = false;
          this.showBookingForm = false;
          this.toast.success('Booking request sent successfully!');
          this.bookingStartDate = '';
          this.bookingEndDate = '';
          this.bookingMessage = '';
        },
        error: (err) => {
          this.submittingBooking = false;
          this.toast.error(err?.error?.message || 'Failed to create booking.');
        },
      });
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'XAF',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  }

  goBack(): void {
    this.router.navigate(['/vehicles']);
  }
}
