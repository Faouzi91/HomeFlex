import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass } from '@angular/common';
import { catchError, forkJoin, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { Booking, PayoutSummary, Property, Vehicle } from '../../../../core/models/api.types';
import { WorkspaceStore } from '../../workspace.store';
import { formatCurrency, formatDate, initials } from '../../../../core/utils/formatters';

@Component({
  selector: 'app-hosting-tab',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, NgClass],
  templateUrl: './hosting-tab.component.html',
})
export class HostingTabComponent {
  private readonly bookingApi = inject(BookingApi);
  private readonly leaseApi = inject(LeaseApi);
  private readonly propertyApi = inject(PropertyApi);
  private readonly payoutApi = inject(PayoutApi);
  private readonly http = inject(HttpClient);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly store = inject(WorkspaceStore);

  protected readonly stripeConfigured = signal<boolean | null>(null);

  // ── Section state ─────────────────────────────────────────────────────────

  protected readonly activeSection = signal<
    'properties' | 'vehicles' | 'availability' | 'bookings' | 'payments'
  >('properties');

  protected setSection(id: string): void {
    this.activeSection.set(
      id as 'properties' | 'vehicles' | 'availability' | 'bookings' | 'payments',
    );
    if (id === 'payments' && !this.payoutSummary()) this.loadPayoutSummary();
    if (id === 'bookings' && !this.bookingsLoaded()) this.loadAllHostBookings();
  }

  // ── Pagination ────────────────────────────────────────────────────────────

  protected readonly PAGE_SIZE = 8;
  protected readonly propertiesPage = signal(0);
  protected readonly vehiclesPage = signal(0);
  protected readonly bookingsPage = signal(0);

  // ── Property filters ──────────────────────────────────────────────────────

  protected readonly propSearch = signal('');
  protected readonly propStatusFilter = signal('');

  protected setPropSearch(q: string): void {
    this.propSearch.set(q);
    this.propertiesPage.set(0);
  }
  protected setPropStatus(s: string): void {
    this.propStatusFilter.set(s);
    this.propertiesPage.set(0);
  }

  protected readonly filteredProperties = computed(() => {
    const q = this.propSearch().toLowerCase();
    const st = this.propStatusFilter();
    return this.store
      .myProperties()
      .filter(
        (p) =>
          (!q ||
            p.title.toLowerCase().includes(q) ||
            p.city.toLowerCase().includes(q) ||
            p.country.toLowerCase().includes(q)) &&
          (!st || p.status === st),
      );
  });

  protected readonly pagedProperties = computed(() => {
    const s = this.propertiesPage() * this.PAGE_SIZE;
    return this.filteredProperties().slice(s, s + this.PAGE_SIZE);
  });

  protected readonly propTotalPages = computed(() =>
    Math.max(1, Math.ceil(this.filteredProperties().length / this.PAGE_SIZE)),
  );

  // ── Vehicle filters ───────────────────────────────────────────────────────

  protected readonly vehicleSearch = signal('');
  protected readonly vehicleStatusFilter = signal('');

  protected setVehicleSearch(q: string): void {
    this.vehicleSearch.set(q);
    this.vehiclesPage.set(0);
  }
  protected setVehicleStatus(s: string): void {
    this.vehicleStatusFilter.set(s);
    this.vehiclesPage.set(0);
  }

  protected readonly filteredVehicles = computed(() => {
    const q = this.vehicleSearch().toLowerCase();
    const st = this.vehicleStatusFilter();
    return this.store
      .myVehicles()
      .filter(
        (v) =>
          (!q ||
            `${v.brand} ${v.model}`.toLowerCase().includes(q) ||
            (v.pickupCity ?? '').toLowerCase().includes(q)) &&
          (!st || v.status === st),
      );
  });

  protected readonly pagedVehicles = computed(() => {
    const s = this.vehiclesPage() * this.PAGE_SIZE;
    return this.filteredVehicles().slice(s, s + this.PAGE_SIZE);
  });

  protected readonly vehicleTotalPages = computed(() =>
    Math.max(1, Math.ceil(this.filteredVehicles().length / this.PAGE_SIZE)),
  );

  // ── Booking filters ───────────────────────────────────────────────────────

  protected readonly bookingSearch = signal('');
  protected readonly bookingStatusFilter = signal('');

  protected setBookingSearch(q: string): void {
    this.bookingSearch.set(q);
    this.bookingsPage.set(0);
  }
  protected setBookingStatus(s: string): void {
    this.bookingStatusFilter.set(s);
    this.bookingsPage.set(0);
  }

  protected readonly filteredBookings = computed(() => {
    const q = this.bookingSearch().toLowerCase();
    const st = this.bookingStatusFilter();
    return this.hostBookings().filter(
      (b) =>
        (!q ||
          `${b.tenant.firstName} ${b.tenant.lastName}`.toLowerCase().includes(q) ||
          b.property.title.toLowerCase().includes(q)) &&
        (!st || b.status === st),
    );
  });

  protected readonly pagedBookings = computed(() => {
    const s = this.bookingsPage() * this.PAGE_SIZE;
    return this.filteredBookings().slice(s, s + this.PAGE_SIZE);
  });

  protected readonly bookingTotalPages = computed(() =>
    Math.max(1, Math.ceil(this.filteredBookings().length / this.PAGE_SIZE)),
  );

  // ── Detail panels ─────────────────────────────────────────────────────────

  protected readonly detailProperty = signal<Property | null>(null);
  protected readonly detailVehicle = signal<Vehicle | null>(null);
  protected readonly detailAvailability = signal<any[]>([]);
  protected readonly detailLoadingAvail = signal(false);

  protected readonly detailPropertyBookings = computed(() => {
    const p = this.detailProperty();
    if (!p) return [];
    return this.hostBookings()
      .filter((b) => b.property.id === p.id)
      .slice(0, 5);
  });

  protected openPropertyDetail(p: Property, event: Event): void {
    event.stopPropagation();
    this.detailVehicle.set(null);
    this.detailProperty.set(p);
    this.loadDetailAvailability(p.id);
    if (!this.bookingsLoaded()) this.loadAllHostBookings();
  }

  protected openVehicleDetail(v: Vehicle, event: Event): void {
    event.stopPropagation();
    this.detailProperty.set(null);
    this.detailVehicle.set(v);
  }

  protected closeDetail(): void {
    this.detailProperty.set(null);
    this.detailVehicle.set(null);
    this.detailAvailability.set([]);
  }

  protected loadDetailAvailability(propertyId: string): void {
    this.detailLoadingAvail.set(true);
    const start = new Date().toISOString().split('T')[0];
    const end = new Date(Date.now() + 60 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    this.propertyApi
      .getAvailability(propertyId, start, end)
      .pipe(
        catchError(() => of({ data: [] as any[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.detailAvailability.set(res.data);
        this.detailLoadingAvail.set(false);
      });
  }

  // ── Availability section ──────────────────────────────────────────────────

  protected readonly selectedPropertyId = signal('');
  protected readonly availability = signal<any[]>([]);

  protected readonly rangeForm = this.fb.group({
    start: ['', Validators.required],
    end: ['', Validators.required],
  });

  protected selectProperty(p: Property): void {
    this.selectedPropertyId.set(p.id);
    this.loadAvailability(p.id);
  }

  protected selectPropertyForAvailability(p: Property): void {
    this.setSection('availability');
    this.selectProperty(p);
  }

  protected loadAvailability(propertyId: string): void {
    const start = new Date().toISOString().split('T')[0];
    const end = new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    this.propertyApi
      .getAvailability(propertyId, start, end)
      .pipe(
        catchError(() => of({ data: [] as any[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => this.availability.set(res.data));
  }

  protected blockRange(): void {
    if (this.rangeForm.invalid || !this.selectedPropertyId()) return;
    const { start, end } = this.rangeForm.value;
    this.propertyApi
      .blockRange(this.selectedPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.loadAvailability(this.selectedPropertyId());
        const dp = this.detailProperty();
        if (dp?.id === this.selectedPropertyId()) this.loadDetailAvailability(dp.id);
      });
  }

  protected unblockRange(): void {
    if (this.rangeForm.invalid || !this.selectedPropertyId()) return;
    const { start, end } = this.rangeForm.value;
    this.propertyApi
      .unblockRange(this.selectedPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.loadAvailability(this.selectedPropertyId());
        const dp = this.detailProperty();
        if (dp?.id === this.selectedPropertyId()) this.loadDetailAvailability(dp.id);
      });
  }

  // ── Bookings table ────────────────────────────────────────────────────────

  protected readonly hostBookings = signal<Booking[]>([]);
  protected readonly loadingBookings = signal(false);
  protected readonly bookingsLoaded = signal(false);
  protected readonly rejectingId = signal<string | null>(null);
  protected readonly rejectReason = signal('');

  protected readonly pendingBookings = computed(() =>
    this.hostBookings().filter((b) => b.status === 'PENDING'),
  );

  protected loadAllHostBookings(): void {
    const props = this.store.myProperties();
    if (!props.length) return;
    this.loadingBookings.set(true);
    forkJoin(
      props.map((p) =>
        this.bookingApi.getByProperty(p.id).pipe(catchError(() => of({ data: [] as Booking[] }))),
      ),
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((results) => {
        this.hostBookings.set(results.flatMap((r) => r.data));
        this.loadingBookings.set(false);
        this.bookingsLoaded.set(true);
      });
  }

  protected approveBooking(id: string): void {
    this.bookingApi
      .approve(id, 'Approved')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadAllHostBookings());
  }

  protected startReject(id: string): void {
    this.rejectingId.set(id);
    this.rejectReason.set('');
  }

  protected cancelReject(): void {
    this.rejectingId.set(null);
  }

  protected submitReject(id: string): void {
    const reason = this.rejectReason().trim();
    if (!reason) return;
    this.bookingApi
      .reject(id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.rejectingId.set(null);
        this.rejectReason.set('');
        this.loadAllHostBookings();
      });
  }

  protected generateLease(bookingId: string): void {
    this.leaseApi
      .generate(bookingId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadAllHostBookings());
  }

  // ── Property / Vehicle actions ────────────────────────────────────────────

  protected deleteProperty(id: string): void {
    if (!confirm('Delete this property? This cannot be undone.')) return;
    this.propertyApi
      .delete(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.selectedPropertyId() === id) this.selectedPropertyId.set('');
        if (this.detailProperty()?.id === id) this.closeDetail();
        this.store.refreshProperties();
      });
  }

  // ── Payments / Stripe Connect ─────────────────────────────────────────────

  protected readonly payoutSummary = signal<PayoutSummary | null>(null);
  protected readonly payoutLoading = signal(false);
  protected readonly stripeOnboardingLoading = signal(false);
  protected readonly stripeOnboardingError = signal<string | null>(null);

  protected loadPayoutSummary(): void {
    this.http
      .get<{ stripeConfigured: boolean }>('/api/v1/config')
      .pipe(catchError(() => of({ stripeConfigured: false })), takeUntilDestroyed(this.destroyRef))
      .subscribe((c) => this.stripeConfigured.set(c.stripeConfigured));
    this.payoutLoading.set(true);
    this.payoutApi
      .getSummary()
      .pipe(
        catchError(() => of(null)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((s) => {
        this.payoutSummary.set(s);
        this.payoutLoading.set(false);
      });
  }

  protected connectStripe(): void {
    this.stripeOnboardingLoading.set(true);
    this.stripeOnboardingError.set(null);
    const base = window.location.origin + '/workspace';
    this.payoutApi
      .onboardConnectAccount(
        base + '?tab=hosting&section=payments',
        base + '?tab=hosting&section=payments',
      )
      .pipe(
        catchError(() => {
          this.stripeOnboardingError.set(
            'Could not start onboarding — check that Stripe is configured in the backend environment.',
          );
          this.stripeOnboardingLoading.set(false);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        if (res?.onboardingUrl) {
          window.location.href = res.onboardingUrl;
        } else if (res !== null) {
          this.stripeOnboardingError.set('No onboarding URL returned. Please try again.');
          this.stripeOnboardingLoading.set(false);
        }
      });
  }

  protected reconnectStripe(): void {
    this.connectStripe();
  }

  // ── Formatters ────────────────────────────────────────────────────────────

  protected date(v: string | null): string {
    return v ? formatDate(v) : '—';
  }

  protected price(v: number, cur = 'XAF'): string {
    return formatCurrency(v, cur);
  }

  protected formatAmount(value: number): string {
    return formatCurrency(value, 'XAF');
  }

  protected initials(firstName?: string, lastName?: string): string {
    return initials(firstName, lastName);
  }

  protected titleCase(s: string): string {
    return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : '';
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      APPROVED: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200',
      CONFIRMED: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200',
      PENDING: 'bg-amber-50 text-amber-700 ring-1 ring-amber-200',
      CANCELLED: 'bg-rose-50 text-rose-700 ring-1 ring-rose-200',
      REJECTED: 'bg-rose-50 text-rose-700 ring-1 ring-rose-200',
      COMPLETED: 'bg-slate-100 text-slate-600 ring-1 ring-slate-200',
      DRAFT: 'bg-slate-100 text-slate-500 ring-1 ring-slate-200',
      AVAILABLE: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200',
      UNAVAILABLE: 'bg-rose-50 text-rose-700 ring-1 ring-rose-200',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }
}
