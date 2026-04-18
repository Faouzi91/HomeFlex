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
    this.propertyBookings().filter(
      (b) => b.status === 'CONFIRMED' || b.status === 'PENDING' || b.status === 'IN_PROGRESS',
    ),
  );

  protected readonly activeVehicleBookings = computed(() =>
    this.vehicleBookings().filter(
      (b) => b.status === 'CONFIRMED' || b.status === 'PENDING' || b.status === 'IN_PROGRESS',
    ),
  );

  protected readonly upcomingBookings = computed(() =>
    this.activePropertyBookings().slice(0, 3),
  );

  constructor() {
    forkJoin({
      favorites: this.favoriteApi.getAll().pipe(catchError(() => of({ data: [] as Property[] }))),
      bookings: this.bookingApi.getMine().pipe(catchError(() => of({ data: [] as Booking[] }))),
      vehicles: this.vehicleApi
        .getMyBookings()
        .pipe(catchError(() => of({ data: [] as VehicleBooking[] }))),
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
      CONFIRMED: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      CANCELLED: 'bg-rose-50 text-rose-700',
      REJECTED: 'bg-rose-50 text-rose-700',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }
}
