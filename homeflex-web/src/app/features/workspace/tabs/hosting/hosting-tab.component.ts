import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass } from '@angular/common';
import { catchError, of } from 'rxjs';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { Booking, Property } from '../../../../core/models/api.types';
import { WorkspaceStore } from '../../workspace.store';
import { formatCurrency, formatDate } from '../../../../core/utils/formatters';

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
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly store = inject(WorkspaceStore);

  protected readonly selectedPropertyId = signal('');
  protected readonly hostBookings = signal<Booking[]>([]);
  protected readonly availability = signal<any[]>([]);
  protected readonly loadingBookings = signal(false);

  protected readonly activeSection = signal<
    'properties' | 'vehicles' | 'availability' | 'bookings'
  >('properties');

  protected readonly pendingBookings = computed(() =>
    this.hostBookings().filter((b) => b.status === 'PENDING'),
  );

  protected readonly rangeForm = this.fb.group({
    start: ['', Validators.required],
    end: ['', Validators.required],
  });

  protected selectProperty(p: Property): void {
    this.selectedPropertyId.set(p.id);
    this.loadHostBookings(p.id);
    this.loadAvailability(p.id);
  }

  protected loadHostBookings(propertyId: string): void {
    this.loadingBookings.set(true);
    this.bookingApi
      .getByProperty(propertyId)
      .pipe(
        catchError(() => of({ data: [] as Booking[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.hostBookings.set(res.data);
        this.loadingBookings.set(false);
      });
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
      .subscribe(() => this.loadAvailability(this.selectedPropertyId()));
  }

  protected unblockRange(): void {
    if (this.rangeForm.invalid || !this.selectedPropertyId()) return;
    const { start, end } = this.rangeForm.value;
    this.propertyApi
      .unblockRange(this.selectedPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadAvailability(this.selectedPropertyId()));
  }

  protected approveBooking(id: string): void {
    this.bookingApi
      .approve(id, 'Approved')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadHostBookings(this.selectedPropertyId()));
  }

  protected rejectBooking(id: string): void {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;
    this.bookingApi
      .reject(id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadHostBookings(this.selectedPropertyId()));
  }

  protected generateLease(bookingId: string): void {
    this.leaseApi
      .generate(bookingId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadHostBookings(this.selectedPropertyId()));
  }

  protected deleteProperty(id: string): void {
    if (!confirm('Delete this property? This cannot be undone.')) return;
    this.propertyApi
      .delete(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.selectedPropertyId() === id) this.selectedPropertyId.set('');
        this.store.refreshProperties();
      });
  }

  protected date(v: string | null): string {
    return v ? formatDate(v) : '—';
  }

  protected price(v: number, cur = 'XAF'): string {
    return formatCurrency(v, cur);
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      APPROVED: 'bg-emerald-50 text-emerald-700',
      CONFIRMED: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      CANCELLED: 'bg-rose-50 text-rose-700',
      REJECTED: 'bg-rose-50 text-rose-700',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }
}
