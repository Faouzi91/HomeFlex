import { Component, DestroyRef, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ReviewApi } from '../../../../core/api/services/review.api';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import { Review } from '../../../../core/models/api.types';

type ActiveTab = 'property' | 'tenant';

@Component({
  selector: 'app-reviews-tab',
  templateUrl: './reviews-tab.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
})
export class ReviewsTabComponent {
  private readonly reviewApi = inject(ReviewApi);
  private readonly session = inject(SessionStore);
  private readonly workspaceStore = inject(WorkspaceStore);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly activeTab = signal<ActiveTab>('property');
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly propertyReviews = signal<Review[]>([]);
  protected readonly tenantReviews = signal<Review[]>([]);

  protected readonly replyingTo = signal<string | null>(null);
  protected readonly replyText = signal('');
  protected readonly replyLoading = signal(false);
  protected readonly replyError = signal<string | null>(null);

  protected readonly isLandlord = computed(
    () => this.session.isLandlord() || this.session.isAdmin(),
  );
  protected readonly currentUserId = computed(() => this.session.user()?.id ?? '');

  constructor() {
    // Load once the store has resolved properties
    effect(() => {
      const properties = this.workspaceStore.myProperties();
      const userId = this.currentUserId();
      if (!userId) return;

      this.loading.set(true);
      this.error.set(null);

      const propertyStreams = properties.map((p) =>
        this.reviewApi
          .getByProperty(p.id)
          .pipe(catchError(() => of({ data: [] as Review[] }))),
      );

      const tenantStream = this.reviewApi
        .getByTenant(userId)
        .pipe(catchError(() => of({ data: [] as Review[] })));

      const streams = properties.length > 0 ? [...propertyStreams, tenantStream] : [tenantStream];

      forkJoin(streams)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (results) => {
            if (properties.length > 0) {
              const propResults = results.slice(0, -1);
              const tenantResult = results[results.length - 1];
              this.propertyReviews.set(propResults.flatMap((r) => r.data ?? []));
              this.tenantReviews.set(tenantResult?.data ?? []);
            } else {
              this.propertyReviews.set([]);
              this.tenantReviews.set(results[0]?.data ?? []);
            }
            this.loading.set(false);
          },
          error: (err) => {
            this.error.set(err?.message ?? 'Failed to load reviews');
            this.loading.set(false);
          },
        });
    });
  }

  protected stars(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }

  protected reviewerInitials(review: Review): string {
    const r = review.reviewer;
    return `${r.firstName?.charAt(0) ?? ''}${r.lastName?.charAt(0) ?? ''}`.toUpperCase();
  }

  protected formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-GB', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  }

  protected openReply(reviewId: string, existing: string | null | undefined): void {
    this.replyingTo.set(reviewId);
    this.replyText.set(existing ?? '');
    this.replyError.set(null);
  }

  protected cancelReply(): void {
    this.replyingTo.set(null);
    this.replyText.set('');
    this.replyError.set(null);
  }

  protected submitReply(reviewId: string): void {
    const text = this.replyText().trim();
    if (!text) return;
    this.replyLoading.set(true);
    this.replyError.set(null);

    this.reviewApi
      .reply(reviewId, text)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.propertyReviews.update((reviews) =>
            reviews.map((r) => (r.id === reviewId ? updated : r)),
          );
          this.replyLoading.set(false);
          this.replyingTo.set(null);
          this.replyText.set('');
        },
        error: (err) => {
          this.replyError.set(err?.error?.message ?? 'Failed to post reply');
          this.replyLoading.set(false);
        },
      });
  }

  protected deleteReview(reviewId: string): void {
    this.reviewApi
      .delete(reviewId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.propertyReviews.update((r) => r.filter((x) => x.id !== reviewId));
          this.tenantReviews.update((r) => r.filter((x) => x.id !== reviewId));
        },
        error: () => {},
      });
  }
}
