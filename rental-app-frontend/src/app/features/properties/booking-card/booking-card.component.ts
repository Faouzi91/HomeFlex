import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule, ToastController } from '@ionic/angular';
import { BookingService } from 'src/app/core/services/booking/booking.service';
import { Property } from 'src/app/models/property.model';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-booking-card',
  standalone: true,
  imports: [CommonModule, FormsModule, IonicModule, TranslateModule],
  templateUrl: './booking-card.component.html',
  styleUrls: ['./booking-card.component.scss'],
})
export class BookingCardComponent implements OnInit {
  @Input() property!: Property;

  startDate: string = '';
  endDate: string = '';
  guests: number = 1;
  isLoading = false;

  constructor(
    private bookingService: BookingService,
    private toastController: ToastController,
    private router: Router
  ) {}

  ngOnInit() {
    // Set default start date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.startDate = tomorrow.toISOString().split('T')[0];
  }

  get totalPrice(): number {
    if (!this.startDate || !this.endDate) return 0;
    const start = new Date(this.startDate);
    const end = new Date(this.endDate);
    const days = (end.getTime() - start.getTime()) / (1000 * 3600 * 24);
    return days > 0 ? days * this.property.price : 0;
  }

  async onBook() {
    if (!this.startDate || !this.endDate) {
      this.presentToast('Please select dates', 'warning');
      return;
    }

    this.isLoading = true;
    this.bookingService
      .createBooking({
        propertyId: this.property.id,
        requestedDate: new Date(this.startDate),
        message: `Booking request for ${this.startDate} to ${this.endDate}`,
        bookingType: 'RENTAL', // default to RENTAL for now, or match enum in backend
      })
      .subscribe({
        next: async (booking) => {
          this.isLoading = false;
          await this.presentToast('Booking request sent!', 'success');
          this.router.navigate(['/bookings']); // Need to create this route later? Or just my-bookings
        },
        error: async (err) => {
          this.isLoading = false;
          await this.presentToast(err.error?.message || 'Booking failed', 'danger');
        },
      });
  }

  private async presentToast(message: string, color: 'success' | 'warning' | 'danger') {
    const toast = await this.toastController.create({
      message,
      duration: 2000,
      color,
      position: 'bottom',
    });
    await toast.present();
  }
}
