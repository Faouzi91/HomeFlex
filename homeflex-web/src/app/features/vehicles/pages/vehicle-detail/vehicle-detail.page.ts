import { Component, DestroyRef, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser, SlicePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, of, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { loadStripe, Stripe, StripeElements } from '@stripe/stripe-js';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { User, Vehicle } from '../../../../core/models/api.types';
import { SessionStore } from '../../../../core/state/session.store';
import { NotificationService } from '../../../../core/service/notification.service';
import { formatCurrency, formatDate, vehicleImage } from '../../../../core/utils/formatters';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-vehicle-detail-page',
  imports: [ReactiveFormsModule, SlicePipe],
  templateUrl: './vehicle-detail.page.html',
  styleUrl: './vehicle-detail.page.scss',
})
export class VehicleDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly vehicleApi = inject(VehicleApi);
  protected readonly session = inject(SessionStore);
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly notifications = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly convertCurrencyPipe = inject(ConvertCurrencyPipe);
  private readonly platformId = inject(PLATFORM_ID);

  protected readonly vehicle = signal<Vehicle | null>(null);
  protected readonly owner = signal<User | null>(null);
  protected readonly availability = signal<boolean | null>(null);
  protected readonly availabilityMessage = signal('');
  protected readonly bookingMessage = signal('');
  protected readonly paymentProcessing = signal(false);

  // Stripe state
  private stripe: Stripe | null = null;
  private stripeElementsInstance: StripeElements | null = null;
  protected readonly pendingPaymentSecret = signal<string | null>(null);
  protected readonly stripeElementsMounted = signal(false);

  protected readonly bookingForm = this.fb.group({
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    message: [''],
  });

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const id = params.get('id');
          if (!id) {
            return of(null);
          }

          this.vehicleApi.trackView(id).subscribe({ error: () => void 0 });
          return this.vehicleApi.getById(id);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((vehicle) => {
        this.vehicle.set(vehicle);
        this.owner.set(vehicle?.owner ?? null);
      });

    if (isPlatformBrowser(this.platformId)) {
      this.initStripe();
    }
  }

  private async initStripe(): Promise<void> {
    this.http.get<{ stripePublicKey: string }>('/api/v1/config').subscribe(async (config) => {
      this.stripe = await loadStripe(config.stripePublicKey);
    });
  }

  protected coverImage(): string {
    const vehicle = this.vehicle();
    return vehicle ? vehicleImage(vehicle) : '';
  }

  protected price(): string {
    const vehicle = this.vehicle();
    if (!vehicle) return '--';
    const pref = this.session.currencyPreference();
    return this.convertCurrencyPipe.transform(vehicle.dailyPrice, vehicle.currency, pref) || '--';
  }

  protected checkAvailability(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate } = this.bookingForm.getRawValue();
    this.vehicleApi
      .getAvailability(vehicle.id, startDate ?? '', endDate ?? '')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (available) => {
          this.availability.set(available);
          this.availabilityMessage.set(
            available
              ? 'Vehicle is available for those dates.'
              : 'Vehicle is already reserved in that window.',
          );
        },
        error: (err) => {
          this.availability.set(false);
          this.availabilityMessage.set(err.error?.message || 'Failed to check availability.');
        },
      });
  }

  protected reserveVehicle(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate, message } = this.bookingForm.getRawValue();
    const payload = {
      vehicleId: vehicle.id,
      startDate: startDate ?? '',
      endDate: endDate ?? '',
      message: message ?? '',
    };

    this.vehicleApi
      .createDraft(payload)
      .pipe(
        switchMap((booking) => {
          return this.vehicleApi.initiatePayment(vehicle.id, booking.id).pipe(
            switchMap((res) => of({ booking, clientSecret: res.clientSecret })),
            catchError((err) => {
              // If payment initiation fails, still show the drafted booking message
              return of({ booking, clientSecret: null, error: err });
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (res: any) => {
          if (res.clientSecret) {
            this.pendingPaymentSecret.set(res.clientSecret);
            this.bookingMessage.set('Vehicle booking drafted! Complete payment below to confirm.');
            setTimeout(() => this.mountPaymentElement(res.clientSecret), 50);
          } else {
            this.bookingMessage.set(
              'Vehicle booking drafted, but payment initiation failed. Please try again from your bookings tab.',
            );
          }
          this.availability.set(true);
        },
        error: (err) => {
          this.availability.set(false);
          const msg = err.error?.message || 'Failed to create booking.';
          this.availabilityMessage.set(msg);
          this.notifications.error(msg);
        },
      });
  }

  private mountPaymentElement(clientSecret: string): void {
    if (!this.stripe || !isPlatformBrowser(this.platformId)) return;

    this.stripeElementsInstance = this.stripe.elements({ clientSecret });
    const paymentElement = this.stripeElementsInstance.create('payment');
    paymentElement.mount('#stripe-payment-element');
    this.stripeElementsMounted.set(true);
  }

  protected async confirmPayment(): Promise<void> {
    if (!this.stripe || !this.stripeElementsInstance) return;

    this.paymentProcessing.set(true);
    const { error } = await this.stripe.confirmPayment({
      elements: this.stripeElementsInstance,
      confirmParams: {
        return_url: `${window.location.origin}/workspace?tab=bookings`,
      },
      redirect: 'if_required',
    });

    this.paymentProcessing.set(false);
    if (error) {
      this.bookingMessage.set(`Payment failed: ${error.message}`);
      this.notifications.error(error.message || 'Payment failed.');
    } else {
      this.pendingPaymentSecret.set(null);
      this.stripeElementsInstance = null;
      this.stripeElementsMounted.set(false);
      this.bookingMessage.set('Payment confirmed! Your vehicle booking is now active.');
      this.notifications.success('Payment confirmed.');
      setTimeout(
        () => this.router.navigate(['/workspace'], { queryParams: { tab: 'bookings' } }),
        2000,
      );
    }
  }

  protected dismissPayment(): void {
    this.pendingPaymentSecret.set(null);
    this.stripeElementsInstance = null;
    this.stripeElementsMounted.set(false);
    this.bookingMessage.set('Booking pending payment. Complete from your bookings tab.');
  }

  protected date(value: string): string {
    return formatDate(value);
  }
}
