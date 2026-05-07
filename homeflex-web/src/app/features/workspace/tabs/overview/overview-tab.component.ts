import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe, NgClass } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { Booking, Property, VehicleBooking } from '../../../../core/models/api.types';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import { formatDate } from '../../../../core/utils/formatters';

@Component({
  selector: 'app-overview-tab',
  standalone: true,
  imports: [RouterLink, DecimalPipe, NgClass],
  templateUrl: './overview-tab.component.html',
})
export class OverviewTabComponent {
  protected readonly session = inject(SessionStore);
  protected readonly store = inject(WorkspaceStore);
  private readonly favoriteApi = inject(FavoriteApi);
  private readonly bookingApi = inject(BookingApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly favorites = signal<Property[]>([]);
  protected readonly propertyBookings = signal<Booking[]>([]);
  protected readonly vehicleBookings = signal<VehicleBooking[]>([]);
  protected readonly hostPendingCount = signal(0);
  protected readonly loading = signal(true);

  protected readonly greeting = computed(() => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 18) return 'Good afternoon';
    return 'Good evening';
  });

  protected readonly todayLabel = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  protected readonly profileCompleteness = computed(() => {
    const u = this.session.user();
    if (!u) return 0;
    const fields = [u.firstName, u.lastName, u.email, u.phoneNumber, u.profilePictureUrl];
    return Math.round((fields.filter(Boolean).length / fields.length) * 100);
  });

  protected readonly activePropertyBookings = computed(() =>
    this.propertyBookings().filter((b) =>
      ['APPROVED', 'ACTIVE', 'PENDING_APPROVAL', 'PAYMENT_PENDING'].includes(b.status),
    ),
  );

  protected readonly activeVehicleBookings = computed(() =>
    this.vehicleBookings().filter((b) =>
      ['APPROVED', 'ACTIVE', 'PENDING_APPROVAL', 'PAYMENT_PENDING'].includes(b.status),
    ),
  );

  protected readonly upcomingBookings = computed(() => this.activePropertyBookings().slice(0, 3));

  protected readonly isLandlord = this.session.isLandlord;

  constructor() {
    const isLandlord = this.session.isLandlord();

    forkJoin({
      favorites: this.favoriteApi.getAll().pipe(catchError(() => of({ data: [] as Property[] }))),
      bookings: this.bookingApi.getMine().pipe(catchError(() => of({ data: [] as Booking[] }))),
      vehicles: this.vehicleApi
        .getMyBookings()
        .pipe(catchError(() => of({ data: [] as VehicleBooking[] }))),
      // If landlord, we could fetch pending counts here, but WorkspaceStore might already have it
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => {
        this.favorites.set(res.favorites.data);
        this.propertyBookings.set(res.bookings.data);
        this.vehicleBookings.set(res.vehicles.data);
        this.loading.set(false);
      });
  }

  protected date(val: string | null): string {
    return val ? formatDate(val) : '—';
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
}
