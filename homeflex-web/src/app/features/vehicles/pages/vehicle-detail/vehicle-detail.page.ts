import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { of, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { User, Vehicle } from '../../../../core/models/api.types';
import { SessionStore } from '../../../../core/state/session.store';
import { formatCurrency, formatDate, vehicleImage } from '../../../../core/utils/formatters';
import { SlicePipe } from '@angular/common';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';

@Component({
  selector: 'app-vehicle-detail-page',
  imports: [ReactiveFormsModule, SlicePipe],
  templateUrl: './vehicle-detail.page.html',
  styleUrl: './vehicle-detail.page.scss',
})
export class VehicleDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly vehicleApi = inject(VehicleApi);
  protected readonly session = inject(SessionStore);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly convertCurrencyPipe = inject(ConvertCurrencyPipe);

  protected readonly vehicle = signal<Vehicle | null>(null);
  protected readonly owner = signal<User | null>(null);
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

          this.vehicleApi.trackView(id).subscribe({ error: () => void 0 });
          return this.vehicleApi.getById(id);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((vehicle) => {
        this.vehicle.set(vehicle);
        this.owner.set(vehicle?.owner ?? null);
      });
  }

  protected coverImage(): string {
    const vehicle = this.vehicle();
    return vehicle ? vehicleImage(vehicle) : '';
  }

  protected price(): string {
    const vehicle = this.vehicle();
    if (!vehicle) return '--';
    const pref = this.session.currencyPreference();
    return this.convertCurrencyPipe.transform(vehicle.dailyPrice, vehicle.currency, pref) || '--';
  }

  protected checkAvailability(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate } = this.bookingForm.getRawValue();
    this.vehicleApi
      .getAvailability(vehicle.id, startDate ?? '', endDate ?? '')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (available) => {
          this.availability.set(available);
          this.availabilityMessage.set(
            available
              ? 'Vehicle is available for those dates.'
              : 'Vehicle is already reserved in that window.',
          );
        },
        error: (err) => {
          this.availability.set(false);
          this.availabilityMessage.set(err.error?.message || 'Failed to check availability.');
        },
      });
  }

  protected reserveVehicle(): void {
    const vehicle = this.vehicle();
    if (!vehicle || this.bookingForm.invalid) {
      return;
    }

    const { startDate, endDate, message } = this.bookingForm.getRawValue();
    this.vehicleApi
      .createBooking({
        vehicleId: vehicle.id,
        startDate: startDate ?? '',
        endDate: endDate ?? '',
        message: message ?? '',
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.availabilityMessage.set('Vehicle booking created successfully.');
          this.availability.set(true);
        },
        error: (err) => {
          this.availability.set(false);
          this.availabilityMessage.set(err.error?.message || 'Failed to create booking.');
        },
      });
  }

  protected date(value: string): string {
    return formatDate(value);
  }
}
