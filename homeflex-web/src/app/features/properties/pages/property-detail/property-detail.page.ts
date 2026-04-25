import { Component, DestroyRef, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser, SlicePipe } from '@angular/common';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, forkJoin, of, switchMap } from 'rxjs';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import * as L from 'leaflet';
import { loadStripe, Stripe, StripeElements } from '@stripe/stripe-js';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { ReviewApi } from '../../../../core/api/services/review.api';
import { ChatApi } from '../../../../core/api/services/chat.api';
import { HttpClient } from '@angular/common/http';
import { Booking, Property, Review } from '../../../../core/models/api.types';
import { NotificationService } from '../../../../core/service/notification.service';
import { SessionStore } from '../../../../core/state/session.store';
import {
  formatCurrency,
  formatDateTime,
  initials,
  propertyImage,
} from '../../../../core/utils/formatters';
import { ListingCardComponent } from '../../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-property-detail-page',
  imports: [ReactiveFormsModule, ListingCardComponent, SlicePipe],
  templateUrl: './property-detail.page.html',
  styleUrl: './property-detail.page.scss',
})
export class PropertyDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly propertyApi = inject(PropertyApi);
  private readonly bookingApi = inject(BookingApi);
  private readonly favoriteApi = inject(FavoriteApi);
  private readonly reviewApi = inject(ReviewApi);
  private readonly chatApi = inject(ChatApi);
  protected readonly session = inject(SessionStore);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly convertCurrencyPipe = inject(ConvertCurrencyPipe);
  private readonly notifications = inject(NotificationService);

  protected readonly property = signal<Property | null>(null);
  protected readonly reviews = signal<Review[]>([]);
  protected readonly similar = signal<Property[]>([]);
  protected readonly favorite = signal(false);
  protected readonly averageRating = signal<number | null>(null);
  protected readonly bookingMessage = signal('');
  protected readonly pendingPaymentSecret = signal<string | null>(null);
  protected readonly paymentProcessing = signal(false);
  protected readonly stripeElementsMounted = signal(false);
  protected readonly blockedDates = signal<Set<string>>(new Set());

  private stripe: Stripe | null = null;
  private stripeElementsInstance: StripeElements | null = null;

  protected readonly bookingForm = this.fb.group({
    bookingType: ['VIEWING', Validators.required],
    requestedDate: [''],
    startDate: [''],
    endDate: [''],
    numberOfOccupants: [1],
    message: [''],
  });

  protected readonly reviewForm = this.fb.group({
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
    comment: ['', Validators.maxLength(1000)],
  });

  // Reactive form values as a signal for computed derivations
  private readonly formValue = toSignal(this.bookingForm.valueChanges, {
    initialValue: this.bookingForm.value,
  });

  protected readonly propertyId = computed(() => this.property()?.id ?? '');
  protected readonly isInstantBook = computed(() => this.property()?.instantBookEnabled ?? false);

  protected readonly nightsEstimate = computed(() => {
    const fv = this.formValue();
    if (!fv.startDate || !fv.endDate) return 0;
    const diff = new Date(fv.endDate).getTime() - new Date(fv.startDate).getTime();
    return Math.max(0, Math.ceil(diff / 86400000) + 1);
  });

  protected readonly priceEstimate = computed(() => {
    const prop = this.property();
    const nights = this.nightsEstimate();
    if (!prop || !nights) return null;
    return prop.price * nights;
  });

  protected readonly cleaningFeeEstimate = computed(() => this.property()?.cleaningFee ?? 0);
  protected readonly platformFeeEstimate = computed(() => {
    const base = this.priceEstimate();
    return base ? Math.round(base * 0.15 * 100) / 100 : 0;
  });
  protected readonly totalEstimate = computed(() => {
    const base = this.priceEstimate();
    if (!base) return null;
    return base + this.platformFeeEstimate() + this.cleaningFeeEstimate();
  });

  protected readonly dateRangeConflict = computed(() => {
    const fv = this.formValue();
    const blocked = this.blockedDates();
    if (!fv.startDate || !fv.endDate || blocked.size === 0) return false;
    const start = new Date(fv.startDate);
    const end = new Date(fv.endDate);
    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
      if (blocked.has(d.toISOString().split('T')[0])) return true;
    }
    return false;
  });

  protected readonly today = new Date().toISOString().split('T')[0];

  constructor() {
    // Pre-load Stripe; also handles retry-payment query params
    if (isPlatformBrowser(this.platformId)) {
      this.initStripe();
    }

    // Handle retry payment flow: ?retryBookingId=&clientSecret=
    this.route.queryParams
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
        const secret = params['clientSecret'];
        if (secret && isPlatformBrowser(this.platformId)) {
          this.pendingPaymentSecret.set(secret);
          this.bookingMessage.set('Payment failed previously — complete payment below to confirm.');
          // Wait for DOM + Stripe; initStripe() sets this.stripe asynchronously
          setTimeout(() => this.mountPaymentElement(secret), 300);
          this.router.navigate([], { queryParams: {}, replaceUrl: true });
        }
      });

    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const id = params.get('id');
          if (!id) return of(null);

          this.propertyApi.trackView(id).subscribe({ error: () => void 0 });

          // Load property data + availability in parallel
          const start = this.today;
          const end = new Date(Date.now() + 365 * 86400000).toISOString().split('T')[0];
          this.propertyApi
            .getAvailability(id, start, end)
            .pipe(
              catchError(() => of({ data: [] })),
              takeUntilDestroyed(this.destroyRef),
            )
            .subscribe((res) => {
              this.blockedDates.set(new Set((res.data ?? []).map((d: any) => d.date)));
            });

          return forkJoin({
            property: this.propertyApi.getById(id),
            reviews: this.reviewApi.getByProperty(id),
            average: this.reviewApi.getPropertyAverage(id),
            similar: this.propertyApi.getSimilar(id),
            favorite: this.session.isAuthenticated()
              ? this.favoriteApi.check(id)
              : of({ data: false }),
          });
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => {
        if (!response) return;

        this.property.set(response.property);
        this.reviews.set(response.reviews.data);
        this.averageRating.set(response.average.data ?? null);
        this.similar.set(
          response.similar.data.filter((item) => item.id !== response.property.id).slice(0, 3),
        );
        this.favorite.set(response.favorite.data);

        if (isPlatformBrowser(this.platformId)) {
          setTimeout(() => this.initMap(), 0);
        }
      });
  }

  private initMap(): void {
    const prop = this.property();
    if (!prop || !prop.latitude || !prop.longitude) return;

    const iconDefault = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41],
    });

    const map = L.map('map', { center: [prop.latitude, prop.longitude], zoom: 15 });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);
    L.marker([prop.latitude, prop.longitude], { icon: iconDefault })
      .addTo(map)
      .bindPopup(`<b>${prop.title}</b><br>${prop.address}`)
      .openPopup();
  }

  private initStripe(): void {
    this.http
      .get<{ stripePublishableKey: string }>('/api/v1/config')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(async (config) => {
        if (config.stripePublishableKey) {
          this.stripe = await loadStripe(config.stripePublishableKey);
          // Mount immediately if a retry-payment secret was queued before Stripe loaded
          const pending = this.pendingPaymentSecret();
          if (pending && !this.stripeElementsMounted()) {
            setTimeout(() => this.mountPaymentElement(pending), 50);
          }
        }
      });
  }

  private async mountPaymentElement(clientSecret: string): Promise<void> {
    if (!this.stripe) return;

    this.stripeElementsInstance = this.stripe.elements({
      clientSecret,
      appearance: { theme: 'stripe' },
    });

    const paymentElement = this.stripeElementsInstance.create('payment');
    paymentElement.mount('#stripe-payment-element');
    this.stripeElementsMounted.set(true);
  }

  protected coverImage(): string {
    const property = this.property();
    return property ? propertyImage(property) : '';
  }

  protected price(): string {
    const property = this.property();
    if (!property) return '--';
    const pref = this.session.currencyPreference();
    return this.convertCurrencyPipe.transform(property.price, property.currency, pref) || '--';
  }

  protected estimateFormatted(): string {
    const est = this.priceEstimate();
    const prop = this.property();
    if (!est || !prop) return '';
    return formatCurrency(est, prop.currency);
  }

  protected fmtFee(amount: number): string {
    const prop = this.property();
    if (!prop) return '';
    return formatCurrency(amount, prop.currency);
  }

  protected totalFormatted(): string {
    const total = this.totalEstimate();
    const prop = this.property();
    if (!total || !prop) return '';
    return formatCurrency(total, prop.currency);
  }

  protected toggleFavorite(): void {
    const property = this.property();
    if (!property) return;

    const request = this.favorite()
      ? this.favoriteApi.remove(property.id)
      : this.favoriteApi.add(property.id);

    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.favorite.set(!this.favorite());
      this.notifications.success(
        this.favorite() ? 'Added to favorites.' : 'Removed from favorites.',
      );
    });
  }

  protected submitBooking(): void {
    const property = this.property();
    if (!property || this.bookingForm.invalid || this.dateRangeConflict()) return;

    const form = this.bookingForm.getRawValue();
    const payload = {
      propertyId: property.id,
      bookingType: form.bookingType ?? 'VIEWING',
      requestedDate: form.requestedDate ?? null,
      startDate: form.startDate ?? null,
      endDate: form.endDate ?? null,
      numberOfOccupants: form.numberOfOccupants ?? null,
      message: form.message ?? null,
    };

    this.bookingApi
      .create(payload)
      .pipe(
        switchMap((booking) => {
          if (payload.bookingType === 'RENTAL') {
            return this.bookingApi
              .initiatePayment(booking.id)
              .pipe(switchMap((res) => of({ booking, clientSecret: res.clientSecret })));
          }
          return of({ booking, clientSecret: null });
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: ({ booking, clientSecret }) => {
          if (clientSecret) {
            this.pendingPaymentSecret.set(clientSecret);
            this.bookingMessage.set('Booking drafted! Complete payment below to confirm.');
            // Mount Stripe Elements after DOM renders the payment container
            setTimeout(() => this.mountPaymentElement(clientSecret), 50);
          } else {
            this.bookingMessage.set('Booking request sent — waiting for landlord approval.');
          }
          this.bookingForm.patchValue({ message: '' });
        },
        error: (err) => {
          const msg = err.error?.message || err.error?.error || 'Failed to submit booking request.';
          this.bookingMessage.set(msg);
          this.notifications.error(msg);
        },
      });
  }

  protected async confirmPayment(): Promise<void> {
    if (!this.stripe || !this.stripeElementsInstance) return;

    this.paymentProcessing.set(true);
    const { error } = await this.stripe.confirmPayment({
      elements: this.stripeElementsInstance,
      confirmParams: {
        return_url: `${window.location.origin}/workspace/bookings`,
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
      this.bookingMessage.set('Payment confirmed! Your booking is now active.');
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

  protected submitReview(): void {
    const propertyId = this.propertyId();
    if (!propertyId || this.reviewForm.invalid) return;

    const form = this.reviewForm.getRawValue();
    this.reviewApi
      .create({
        propertyId,
        rating: Number(form.rating),
        comment: form.comment ?? '',
      })
      .pipe(
        switchMap(() =>
          forkJoin({
            reviews: this.reviewApi.getByProperty(propertyId),
            average: this.reviewApi.getPropertyAverage(propertyId),
          }),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(({ reviews, average }) => {
        this.reviews.set(reviews.data);
        this.averageRating.set(average.data);
        this.reviewForm.patchValue({ rating: 5, comment: '' });
      });
  }

  protected startChat(): void {
    const property = this.property();
    const user = this.session.user();
    if (!property || !user) return;

    this.chatApi
      .createRoom({ propertyId: property.id, tenantId: user.id, landlordId: property.landlord.id })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((room) => {
        this.router.navigate(['/workspace/messages'], { queryParams: { room: room.id } });
      });
  }

  protected reviewerInitials(review: Review): string {
    return initials(review.reviewer.firstName, review.reviewer.lastName);
  }

  protected dateTime(value: string): string {
    return formatDateTime(value);
  }
}
