import { Component, DestroyRef, computed, effect, inject, signal } from '@angular/core';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import { Booking, PropertyLease, VehicleBooking } from '../../../../core/models/api.types';
import {
  formatCurrency,
  formatDate,
  rentalPhase,
  daysUntilCheckIn,
  daysRemaining,
} from '../../../../core/utils/formatters';
import { BookingDetailPanelComponent } from './booking-detail-panel/booking-detail-panel.component';
import { VehicleBookingDetailPanelComponent } from './vehicle-booking-detail-panel/vehicle-booking-detail-panel.component';

type SubTab = 'properties' | 'vehicles' | 'received';

@Component({
  selector: 'app-bookings-tab',
  standalone: true,
  imports: [
    NgClass,
    NgTemplateOutlet,
    BookingDetailPanelComponent,
    VehicleBookingDetailPanelComponent,
  ],
  templateUrl: './bookings-tab.component.html',
})
export class BookingsTabComponent {
  private readonly bookingApi = inject(BookingApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly leaseApi = inject(LeaseApi);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly session = inject(SessionStore);
  protected readonly workspaceStore = inject(WorkspaceStore);

  protected readonly propertyBookings = signal<Booking[]>([]);
  protected readonly vehicleBookings = signal<VehicleBooking[]>([]);
  protected readonly leases = signal<PropertyLease[]>([]);
  protected readonly receivedBookings = signal<Booking[]>([]);
  protected readonly loading = signal(true);
  protected readonly activeSubTab = signal<SubTab>('properties');

  // Detail panel
  protected readonly selectedBooking = signal<Booking | null>(null);
  protected readonly selectedVehicleBooking = signal<VehicleBooking | null>(null);
  private readonly receivedBookingsLoaded = signal(false);

  // ── Landlord groupings ───────────────────────────────────────────────────────

  protected readonly activeOccupants = computed(() => {
    const today = new Date();
    return this.receivedBookings().filter((b) => {
      if (!b.startDate || !b.endDate) return false;
      const start = new Date(b.startDate);
      const end = new Date(b.endDate);
      return (b.status === 'APPROVED' || b.status === 'ACTIVE') && today >= start && today <= end;
    });
  });

  protected readonly upcomingApproved = computed(() => {
    const today = new Date();
    return this.receivedBookings().filter((b) => {
      if (!b.startDate) return false;
      const start = new Date(b.startDate);
      return b.status === 'APPROVED' && today < start;
    });
  });

  protected readonly pendingApproval = computed(() =>
    this.receivedBookings().filter((b) => b.status === 'PENDING_APPROVAL'),
  );

  protected readonly pastReceived = computed(() =>
    this.receivedBookings().filter(
      (b) =>
        b.status === 'COMPLETED' ||
        b.status === 'CANCELLED' ||
        b.status === 'REJECTED' ||
        b.status === 'PAYMENT_FAILED' ||
        (b.endDate && new Date(b.endDate) < new Date() && b.status === 'APPROVED'),
    ),
  );

  // Pending badge count (used in the pill)
  protected readonly pendingCount = computed(
    () => this.receivedBookings().filter((b) => b.status === 'PENDING_APPROVAL').length,
  );

  constructor() {
    forkJoin({
      bookings: this.bookingApi.getMine().pipe(catchError(() => of({ data: [] as Booking[] }))),
      vehicles: this.vehicleApi
        .getMyBookings()
        .pipe(catchError(() => of({ data: [] as VehicleBooking[] }))),
      leases: this.leaseApi.getMine().pipe(catchError(() => of([] as PropertyLease[]))),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => {
        this.propertyBookings.set(res.bookings.data);
        this.vehicleBookings.set(res.vehicles.data);
        this.leases.set(res.leases);
        this.loading.set(false);

        // After bookings loaded, honour any deep-linked ?booking= param
        this.route.queryParams.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
          const id = params['booking'];
          if (!id) return;
          const allBookings = [...this.propertyBookings(), ...this.receivedBookings()];
          const found = allBookings.find((b) => b.id === id);
          if (found) {
            this.openPanel(found);
          } else {
            // Not in cached list (e.g. landlord clicked notification before tab loaded) — fetch directly
            this.bookingApi
              .getById(id)
              .pipe(
                catchError(() => of(null)),
                takeUntilDestroyed(this.destroyRef),
              )
              .subscribe((b) => {
                if (b) this.openPanel(b);
              });
          }
          // Clear the query param so back-navigation doesn't re-open
          this.router.navigate([], { queryParams: {}, replaceUrl: true });
        });
      });

    effect(() => {
      const isHost = this.session.isLandlord() || this.session.isAdmin();

      if (!isHost) {
        this.receivedBookingsLoaded.set(false);
        this.receivedBookings.set([]);
        return;
      }

      this.activeSubTab.set('received');

      const properties = this.workspaceStore.myProperties();
      if (!properties.length || this.receivedBookingsLoaded()) {
        return;
      }

      this.receivedBookingsLoaded.set(true);
      this.fetchReceivedForProperties(properties.map((p) => p.id));
    });
  }

  private fetchReceivedForProperties(propertyIds: string[]): void {
    if (!propertyIds.length) {
      this.receivedBookingsLoaded.set(false);
      return;
    }

    forkJoin(
      propertyIds.map((id) =>
        this.bookingApi.getByProperty(id).pipe(catchError(() => of({ data: [] as Booking[] }))),
      ),
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((results) => this.receivedBookings.set(results.flatMap((r) => r.data)));
  }

  // ── Panel ────────────────────────────────────────────────────────────────────

  protected openPanel(booking: Booking): void {
    this.selectedBooking.set(booking);
  }

  protected closePanel(): void {
    this.selectedBooking.set(null);
    this.selectedVehicleBooking.set(null);
  }

  protected openVehiclePanel(booking: VehicleBooking): void {
    this.selectedVehicleBooking.set(booking);
  }

  protected onVehicleBookingChanged(updated: VehicleBooking): void {
    this.vehicleBookings.update((list) => list.map((b) => (b.id === updated.id ? updated : b)));
    this.selectedVehicleBooking.set(updated);
  }

  protected onBookingChanged(updated: Booking): void {
    // Refresh the booking in all lists
    const patch = (list: Booking[]) => list.map((b) => (b.id === updated.id ? updated : b));
    this.propertyBookings.update(patch);
    this.receivedBookings.update(patch);
    this.selectedBooking.set(updated);
  }

  // ── Helpers ──────────────────────────────────────────────────────────────────

  // Dispute is now handled via DisputeModalComponent inside BookingDetailPanelComponent

  protected signLease(leaseId: string): void {
    this.leaseApi
      .sign(leaseId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.leases.update((ls) =>
          ls.map((l) => (l.id === leaseId ? { ...l, status: 'SIGNED' as const } : l)),
        );
      });
  }

  protected phase(b: Booking) {
    return rentalPhase(b);
  }

  protected checkInDays(b: Booking) {
    return daysUntilCheckIn(b);
  }

  protected remainingDays(b: Booking) {
    return daysRemaining(b);
  }

  protected date(v: string | null): string {
    return formatDate(v);
  }

  protected price(v: number | null, cur = 'XAF'): string {
    return v != null ? formatCurrency(v, cur) : '—';
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      DRAFT: 'bg-slate-100 text-slate-600',
      PAYMENT_PENDING: 'bg-amber-50 text-amber-700',
      PAYMENT_FAILED: 'bg-rose-50 text-rose-700',
      PENDING_APPROVAL: 'bg-blue-50 text-blue-700',
      APPROVED: 'bg-emerald-50 text-emerald-700',
      ACTIVE: 'bg-emerald-100 text-emerald-800',
      CANCELLED: 'bg-rose-50 text-rose-700',
      REJECTED: 'bg-rose-50 text-rose-700',
      COMPLETED: 'bg-slate-100 text-slate-600',
      PENDING_MODIFICATION: 'bg-violet-50 text-violet-700',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }

  protected leaseStatusClass(status: string): string {
    const map: Record<string, string> = {
      SIGNED: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      EXPIRED: 'bg-slate-100 text-slate-500',
      CANCELLED: 'bg-rose-50 text-rose-700',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }
}
