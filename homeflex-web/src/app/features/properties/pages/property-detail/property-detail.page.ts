import { Component, DestroyRef, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser, SlicePipe } from '@angular/common';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, of, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import * as L from 'leaflet';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { ReviewApi } from '../../../../core/api/services/review.api';
import { ChatApi } from '../../../../core/api/services/chat.api';
import { Booking, Property, Review } from '../../../../core/models/api.types';
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
  private readonly propertyApi = inject(PropertyApi);
  private readonly bookingApi = inject(BookingApi);
  private readonly favoriteApi = inject(FavoriteApi);
  private readonly reviewApi = inject(ReviewApi);
  private readonly chatApi = inject(ChatApi);
  protected readonly session = inject(SessionStore);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly convertCurrencyPipe = inject(ConvertCurrencyPipe);

  protected readonly property = signal<Property | null>(null);
  protected readonly reviews = signal<Review[]>([]);
  protected readonly similar = signal<Property[]>([]);
  protected readonly favorite = signal(false);
  protected readonly averageRating = signal<number | null>(null);
  protected readonly bookingMessage = signal('');

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

  protected readonly propertyId = computed(() => this.property()?.id ?? '');

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const id = params.get('id');
          if (!id) {
            return of(null);
          }

          this.propertyApi.trackView(id).subscribe({ error: () => void 0 });

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
        if (!response) {
          return;
        }

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

    const map = L.map('map', {
      center: [prop.latitude, prop.longitude],
      zoom: 15,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);

    L.marker([prop.latitude, prop.longitude], { icon: iconDefault })
      .addTo(map)
      .bindPopup(`<b>${prop.title}</b><br>${prop.address}`)
      .openPopup();
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

  protected toggleFavorite(): void {
    const property = this.property();
    if (!property) {
      return;
    }

    const request = this.favorite()
      ? this.favoriteApi.remove(property.id)
      : this.favoriteApi.add(property.id);

    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.favorite.set(!this.favorite());
    });
  }

  protected submitBooking(): void {
    const property = this.property();
    if (!property || this.bookingForm.invalid) {
      return;
    }

    const form = this.bookingForm.getRawValue();
    this.bookingApi
      .create({
        propertyId: property.id,
        bookingType: form.bookingType ?? 'VIEWING',
        requestedDate: form.requestedDate ?? null,
        startDate: form.startDate ?? null,
        endDate: form.endDate ?? null,
        numberOfOccupants: form.numberOfOccupants ?? null,
        message: form.message ?? null,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (booking: Booking) => {
          this.bookingMessage.set(`Request sent with status ${booking.status}.`);
          this.bookingForm.patchValue({ message: '' });
        },
        error: (err) => {
          const msg = err.error?.message || err.error?.error || 'Failed to submit booking request.';
          this.bookingMessage.set(msg);
        },
      });
  }

  protected submitReview(): void {
    const propertyId = this.propertyId();
    if (!propertyId || this.reviewForm.invalid) {
      return;
    }

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
    if (!property || !user) {
      return;
    }

    this.chatApi
      .createRoom({
        propertyId: property.id,
        tenantId: user.id,
        landlordId: property.landlord.id,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((room) => {
        this.router.navigate(['/workspace'], { queryParams: { tab: 'messages', chat: room.id } });
      });
  }

  protected reviewerInitials(review: Review): string {
    return initials(review.reviewer.firstName, review.reviewer.lastName);
  }

  protected dateTime(value: string): string {
    return formatDateTime(value);
  }
}
