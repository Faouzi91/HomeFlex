import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { DisputeApi } from '../../../../core/api/services/dispute.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import { Booking, PropertyLease, VehicleBooking } from '../../../../core/models/api.types';
import { formatCurrency, formatDate } from '../../../../core/utils/formatters';

type SubTab = 'properties' | 'vehicles' | 'received';

@Component({
  selector: 'app-bookings-tab',
  standalone: true,
  imports: [NgClass],
  templateUrl: './bookings-tab.component.html',
})
export class BookingsTabComponent {
  private readonly bookingApi = inject(BookingApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly leaseApi = inject(LeaseApi);
  private readonly disputeApi = inject(DisputeApi);
  protected readonly session = inject(SessionStore);
  protected readonly workspaceStore = inject(WorkspaceStore);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  protected readonly propertyBookings = signal<Booking[]>([]);
  protected readonly vehicleBookings = signal<VehicleBooking[]>([]);
  protected readonly leases = signal<PropertyLease[]>([]);
  protected readonly receivedBookings = signal<Booking[]>([]);
  protected readonly loading = signal(true);
  protected readonly approvingId = signal('');
  protected readonly activeSubTab = signal<SubTab>('properties');

  protected readonly pendingReceived = computed(() =>
    this.receivedBookings().filter((b) => b.status === 'PENDING'),
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
      });

    if (this.session.isLandlord() || this.session.isAdmin()) {
      this.activeSubTab.set('received');
      this.loadReceivedBookings();
    }
  }

  private loadReceivedBookings(): void {
    const properties = this.workspaceStore.myProperties();
    if (!properties.length) {
      // Properties may not be loaded yet — wait briefly then retry once
      setTimeout(() => {
        const props = this.workspaceStore.myProperties();
        if (props.length) this.fetchReceivedForProperties(props.map((p) => p.id));
      }, 1500);
      return;
    }
    this.fetchReceivedForProperties(properties.map((p) => p.id));
  }

  private fetchReceivedForProperties(propertyIds: string[]): void {
    if (!propertyIds.length) return;
    forkJoin(
      propertyIds.map((id) =>
        this.bookingApi.getByProperty(id).pipe(catchError(() => of({ data: [] as Booking[] }))),
      ),
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((results) => this.receivedBookings.set(results.flatMap((r) => r.data)));
  }

  protected approveBooking(id: string): void {
    this.approvingId.set(id);
    this.bookingApi
      .approve(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.receivedBookings.update((bs) => bs.map((b) => (b.id === id ? updated : b)));
          this.approvingId.set('');
        },
        error: () => this.approvingId.set(''),
      });
  }

  protected rejectBooking(id: string): void {
    const reason = prompt('Rejection reason:');
    if (!reason) return;
    this.bookingApi
      .reject(id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((updated) =>
        this.receivedBookings.update((bs) => bs.map((b) => (b.id === id ? updated : b))),
      );
  }

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

  protected openPropertyBooking(b: Booking): void {
    if (b.property?.id) {
      this.router.navigate(['/properties', b.property.id], { queryParams: { booking: b.id } });
    }
  }

  protected openVehicleBooking(b: VehicleBooking): void {
    if (b.vehicleId) {
      this.router.navigate(['/vehicles', b.vehicleId], { queryParams: { booking: b.id } });
    }
  }

  protected openDispute(bookingId: string): void {
    const reason = prompt('Dispute reason (e.g. DAMAGE, DEPOSIT_RETURN):');
    const description = prompt('Describe the issue:');
    if (!reason || !description) return;
    this.disputeApi
      .open(bookingId, reason, description)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => alert('Dispute submitted. An admin will review it shortly.'));
  }

  protected date(v: string | null): string {
    return v ? formatDate(v) : '—';
  }

  protected price(v: number | null, cur = 'XAF'): string {
    return v != null ? formatCurrency(v, cur) : '—';
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      CONFIRMED: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      APPROVED: 'bg-emerald-50 text-emerald-700',
      CANCELLED: 'bg-rose-50 text-rose-700',
      REJECTED: 'bg-rose-50 text-rose-700',
      COMPLETED: 'bg-slate-100 text-slate-600',
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
