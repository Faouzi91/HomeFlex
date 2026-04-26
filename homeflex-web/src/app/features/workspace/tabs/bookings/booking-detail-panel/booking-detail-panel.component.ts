import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DisputeModalComponent } from '../dispute-modal/dispute-modal.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DestroyRef } from '@angular/core';
import { BookingApi } from '../../../../../core/api/services/booking.api';
import { LeaseApi } from '../../../../../core/api/services/lease.api';
import { DisputeApi } from '../../../../../core/api/services/dispute.api';
import { SessionStore } from '../../../../../core/state/session.store';
import { Booking } from '../../../../../core/models/api.types';
import {
  daysRemaining,
  daysUntilCheckIn,
  formatCurrency,
  formatDate,
  nightsBooked,
  rentalPhase,
  initials,
} from '../../../../../core/utils/formatters';

@Component({
  selector: 'app-booking-detail-panel',
  imports: [NgClass, FormsModule, DisputeModalComponent],
  templateUrl: './booking-detail-panel.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookingDetailPanelComponent {
  private readonly bookingApi = inject(BookingApi);
  private readonly leaseApi = inject(LeaseApi);
  private readonly disputeApi = inject(DisputeApi);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly session = inject(SessionStore);

  readonly booking = input.required<Booking>();
  readonly closed = output<void>();
  readonly bookingChanged = output<Booking>();

  protected readonly activeImageIndex = signal(0);
  protected readonly actionLoading = signal<string | null>(null);
  protected readonly rejectReason = signal('');
  protected readonly showRejectInput = signal(false);
  protected readonly showCancelConfirm = signal(false);
  protected readonly showDisputeModal = signal(false);

  // Modification modal
  protected readonly showModifyModal = signal(false);
  protected readonly modifyStart = signal('');
  protected readonly modifyEnd = signal('');
  protected readonly modifyReason = signal('');
  protected readonly modifyError = signal<string | null>(null);

  // ── Derived: images ─────────────────────────────────────────────────────────

  protected readonly images = computed(() => {
    const imgs = this.booking().property.images ?? [];
    return [...imgs].sort((a, b) => a.displayOrder - b.displayOrder);
  });

  protected readonly mainImage = computed(
    () =>
      this.images()[this.activeImageIndex()]?.imageUrl ??
      'https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=800&q=80',
  );

  // ── Derived: rental timing ──────────────────────────────────────────────────

  protected readonly phase = computed(() => rentalPhase(this.booking()));
  protected readonly checkInDays = computed(() => daysUntilCheckIn(this.booking()));
  protected readonly remainingDays = computed(() => daysRemaining(this.booking()));
  protected readonly nights = computed(() => nightsBooked(this.booking()));

  protected readonly phaseLabel = computed(() => {
    switch (this.phase()) {
      case 'ACTIVE':
        return `Active — ${this.remainingDays()} day${this.remainingDays() !== 1 ? 's' : ''} remaining`;
      case 'UPCOMING':
        return `Check-in in ${this.checkInDays()} day${this.checkInDays() !== 1 ? 's' : ''}`;
      case 'PAST':
        return `Completed stay · ${this.nights()} nights`;
      default:
        return null;
    }
  });

  // ── Derived: capabilities ───────────────────────────────────────────────────

  protected readonly isLandlord = computed(
    () => this.session.isLandlord() || this.session.isAdmin(),
  );

  protected readonly canApprove = computed(
    () => this.isLandlord() && this.booking().status === 'PENDING_APPROVAL',
  );

  protected readonly canCancel = computed(() => {
    const s = this.booking().status;
    if (this.isLandlord()) return false;
    return (
      s === 'PENDING_APPROVAL' ||
      s === 'PAYMENT_PENDING' ||
      s === 'PAYMENT_FAILED' ||
      s === 'APPROVED' ||
      s === 'DRAFT'
    );
  });

  protected readonly canRetryPayment = computed(
    () => !this.isLandlord() && this.booking().status === 'PAYMENT_FAILED',
  );

  protected readonly isEarlyCheckout = computed(
    () => this.canCancel() && this.phase() === 'ACTIVE',
  );

  protected readonly canOpenDispute = computed(() => {
    const s = this.booking().status;
    return s === 'APPROVED' || s === 'ACTIVE' || s === 'COMPLETED';
  });

  protected readonly canRequestModification = computed(() => {
    const s = this.booking().status;
    return !this.isLandlord() && (s === 'APPROVED' || s === 'ACTIVE');
  });

  protected readonly canReviewModification = computed(
    () => this.isLandlord() && this.booking().status === 'PENDING_MODIFICATION',
  );

  // ── Tenant info display (for landlord view) ─────────────────────────────────

  protected readonly tenantInitials = computed(() =>
    initials(this.booking().tenant.firstName, this.booking().tenant.lastName),
  );

  // ── Actions ─────────────────────────────────────────────────────────────────

  protected approve(): void {
    this.actionLoading.set('approve');
    this.bookingApi
      .approve(this.booking().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.actionLoading.set(null);
          this.bookingChanged.emit(updated);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected submitReject(): void {
    const reason = this.rejectReason().trim();
    if (!reason) return;
    this.actionLoading.set('reject');
    this.bookingApi
      .reject(this.booking().id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.actionLoading.set(null);
          this.showRejectInput.set(false);
          this.bookingChanged.emit(updated);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected cancelBooking(): void {
    this.actionLoading.set('cancel');
    const call = this.isEarlyCheckout()
      ? this.bookingApi.earlyCheckout(this.booking().id)
      : this.bookingApi.cancel(this.booking().id);
    call.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (updated) => {
        this.actionLoading.set(null);
        this.showCancelConfirm.set(false);
        this.bookingChanged.emit(updated);
      },
      error: () => this.actionLoading.set(null),
    });
  }

  protected retryPayment(): void {
    const b = this.booking();
    this.actionLoading.set('retry');
    this.bookingApi
      .retryPayment(b.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.actionLoading.set(null);
          this.closed.emit();
          this.router.navigate(['/properties', b.property.id], {
            queryParams: { retryBookingId: b.id, clientSecret: res.clientSecret },
          });
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected signLease(): void {
    const b = this.booking();
    if (!b.id) return;
    this.leaseApi
      .getByBooking(b.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (lease) => {
          if (lease?.id) {
            this.leaseApi.sign(lease.id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe();
          }
        },
      });
  }

  protected openDispute(): void {
    this.showDisputeModal.set(true);
  }

  protected openModifyModal(): void {
    const b = this.booking();
    this.modifyStart.set(b.startDate ?? '');
    this.modifyEnd.set(b.endDate ?? '');
    this.modifyReason.set('');
    this.modifyError.set(null);
    this.showModifyModal.set(true);
  }

  protected closeModifyModal(): void {
    this.showModifyModal.set(false);
  }

  protected submitModification(): void {
    const start = this.modifyStart();
    const end = this.modifyEnd();
    if (!start || !end) return;
    this.actionLoading.set('modify');
    this.modifyError.set(null);
    this.bookingApi
      .requestModification(this.booking().id, {
        startDate: start,
        endDate: end,
        reason: this.modifyReason() || undefined,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.actionLoading.set(null);
          this.showModifyModal.set(false);
          this.bookingChanged.emit(updated);
        },
        error: (err) => {
          this.modifyError.set(err?.error?.message ?? 'Failed to submit modification');
          this.actionLoading.set(null);
        },
      });
  }

  protected approveModification(): void {
    this.actionLoading.set('approveModify');
    this.bookingApi
      .approveModification(this.booking().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.actionLoading.set(null);
          this.bookingChanged.emit(updated);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected rejectModification(): void {
    this.actionLoading.set('rejectModify');
    this.bookingApi
      .rejectModification(this.booking().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.actionLoading.set(null);
          this.bookingChanged.emit(updated);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected messageParty(): void {
    this.router.navigate(['/workspace/messages']);
    this.closed.emit();
  }

  protected nextImage(): void {
    const len = this.images().length;
    if (len) this.activeImageIndex.update((i) => (i + 1) % len);
  }

  protected prevImage(): void {
    const len = this.images().length;
    if (len) this.activeImageIndex.update((i) => (i - 1 + len) % len);
  }

  // ── Formatters ──────────────────────────────────────────────────────────────

  protected date(v: string | null) {
    return formatDate(v);
  }

  protected price(v: number | null, cur = 'XAF') {
    return formatCurrency(v, cur);
  }

  protected statusClass(s: string): string {
    const m: Record<string, string> = {
      DRAFT: 'bg-slate-100 text-slate-600 ring-1 ring-slate-200',
      PAYMENT_PENDING: 'bg-amber-50 text-amber-700 ring-1 ring-amber-200',
      PAYMENT_FAILED: 'bg-rose-50 text-rose-700 ring-1 ring-rose-200',
      PENDING_APPROVAL: 'bg-blue-50 text-blue-700 ring-1 ring-blue-200',
      APPROVED: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200',
      ACTIVE: 'bg-emerald-100 text-emerald-800 ring-1 ring-emerald-300',
      CANCELLED: 'bg-rose-50 text-rose-600 ring-1 ring-rose-200',
      REJECTED: 'bg-rose-50 text-rose-600 ring-1 ring-rose-200',
      COMPLETED: 'bg-slate-100 text-slate-600 ring-1 ring-slate-200',
      PENDING_MODIFICATION: 'bg-violet-50 text-violet-700 ring-1 ring-violet-200',
    };
    return m[s] ?? 'bg-slate-100 text-slate-600';
  }

  protected phaseClass(): string {
    switch (this.phase()) {
      case 'ACTIVE':
        return 'bg-emerald-500';
      case 'UPCOMING':
        return 'bg-brand-500';
      default:
        return 'bg-slate-400';
    }
  }
}
