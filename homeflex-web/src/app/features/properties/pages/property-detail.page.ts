import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, of, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiClient } from '../../../core/api/api.client';
import { Booking, Property, Review } from '../../../core/models/api.types';
import { SessionStore } from '../../../core/state/session.store';
import {
  formatCurrency,
  formatDateTime,
  initials,
  propertyImage,
} from '../../../core/utils/formatters';
import { ListingCardComponent } from '../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-property-detail-page',
  imports: [ReactiveFormsModule, ListingCardComponent],
  templateUrl: './property-detail.page.html',
  styleUrl: './property-detail.page.scss',
})
export class PropertyDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClient);
  protected readonly session = inject(SessionStore);
  private readonly destroyRef = inject(DestroyRef);

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

          this.api.trackPropertyView(id).subscribe({ error: () => void 0 });

          return forkJoin({
            property: this.api.getProperty(id),
            reviews: this.api.getReviews(id),
            average: this.api.getAverageRating(id),
            similar: this.api.getSimilarProperties(id),
            favorite: this.session.isAuthenticated()
              ? this.api.isFavorite(id)
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
      });
  }

  protected coverImage(): string {
    const property = this.property();
    return property ? propertyImage(property) : '';
  }

  protected price(): string {
    const property = this.property();
    return property ? formatCurrency(property.price, property.currency) : '--';
  }

  protected toggleFavorite(): void {
    const property = this.property();
    if (!property) {
      return;
    }

    const request = this.favorite()
      ? this.api.removeFavorite(property.id)
      : this.api.addFavorite(property.id);

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
    this.api
      .createPropertyBooking({
        propertyId: property.id,
        bookingType: form.bookingType ?? 'VIEWING',
        requestedDate: form.requestedDate ?? null,
        startDate: form.startDate ?? null,
        endDate: form.endDate ?? null,
        numberOfOccupants: form.numberOfOccupants ?? null,
        message: form.message ?? null,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((booking: Booking) => {
        this.bookingMessage.set(`Request sent with status ${booking.status}.`);
        this.bookingForm.patchValue({ message: '' });
      });
  }

  protected submitReview(): void {
    const propertyId = this.propertyId();
    if (!propertyId || this.reviewForm.invalid) {
      return;
    }

    const form = this.reviewForm.getRawValue();
    this.api
      .createReview({
        propertyId,
        rating: Number(form.rating),
        comment: form.comment ?? '',
      })
      .pipe(
        switchMap(() =>
          forkJoin({
            reviews: this.api.getReviews(propertyId),
            average: this.api.getAverageRating(propertyId),
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

    this.api
      .createChatRoom({
        propertyId: property.id,
        tenantId: user.id,
        landlordId: property.landlord.id,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((room) => {
        this.router.navigate(['/workspace'], { queryParams: { chat: room.id } });
      });
  }

  protected reviewerInitials(review: Review): string {
    return initials(review.reviewer.firstName, review.reviewer.lastName);
  }

  protected dateTime(value: string): string {
    return formatDateTime(value);
  }
}
