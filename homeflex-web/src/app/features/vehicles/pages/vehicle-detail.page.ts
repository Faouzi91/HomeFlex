import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { of, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiClient } from '../../../core/api/api.client';
import { Vehicle } from '../../../core/models/api.types';
import { SessionStore } from '../../../core/state/session.store';
import { formatCurrency, formatDate, vehicleImage } from '../../../core/utils/formatters';

@Component({
  selector: 'app-vehicle-detail-page',
  imports: [ReactiveFormsModule],
  templateUrl: './vehicle-detail.page.html',
  styleUrl: './vehicle-detail.page.scss',
})
export class VehicleDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(ApiClient);
  protected readonly session = inject(SessionStore);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly vehicle = signal<Vehicle | null>(null);
  protected readonly availability = signal<boolean | null>(null);
  protected readonly availabilityMessage = signal('');

  protected readonly bookingForm = this.fb.group({
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    message: [''],
  });

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const id = params.get('id');
          if (!id) {
            return of(null);
          }

          this.api.trackVehicleView(id).subscribe({ error: () => void 0 });
          return this.api.getVehicle(id);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((vehicle) => {
        this.vehicle.set(vehicle);
      });
  }

  protected coverImage(): string {
    const vehicle = this.vehicle();
    return vehicle ? vehicleImage(vehicle) : '';
  }

  protected price(): string {
    const vehicle = this.vehicle();
    return vehicle ? formatCurrency(vehicle.dailyPrice, vehicle.currency) : '--';
  }

  protected checkAvailability(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate } = this.bookingForm.getRawValue();
    this.api
      .getVehicleAvailability(vehicle.id, startDate ?? '', endDate ?? '')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((available) => {
        this.availability.set(available);
        this.availabilityMessage.set(
          available
            ? 'Vehicle is available for those dates.'
            : 'Vehicle is already reserved in that window.',
        );
      });
  }

  protected reserveVehicle(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate, message } = this.bookingForm.getRawValue();
    this.api
      .createVehicleBooking({
        vehicleId: vehicle.id,
        startDate: startDate ?? '',
        endDate: endDate ?? '',
        message: message ?? '',
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.availabilityMessage.set('Vehicle booking created successfully.');
        this.availability.set(true);
      });
  }

  protected date(value: string): string {
    return formatDate(value);
  }
}
