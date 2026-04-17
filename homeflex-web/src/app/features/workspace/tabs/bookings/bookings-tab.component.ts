import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { DisputeApi } from '../../../../core/api/services/dispute.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { Booking, PropertyLease, VehicleBooking } from '../../../../core/models/api.types';
import { formatCurrency, formatDate } from '../../../../core/utils/formatters';

type SubTab = 'properties' | 'vehicles';

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
  private readonly destroyRef = inject(DestroyRef);

  protected readonly propertyBookings = signal<Booking[]>([]);
  protected readonly vehicleBookings = signal<VehicleBooking[]>([]);
  protected readonly leases = signal<PropertyLease[]>([]);
  protected readonly loading = signal(true);
  protected readonly activeSubTab = signal<SubTab>('properties');

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
