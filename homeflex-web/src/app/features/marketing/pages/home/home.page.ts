import { Component, DestroyRef, inject, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { StatsApi } from '../../../../core/api/services/stats.api';
import { Property, Vehicle } from '../../../../core/models/api.types';
import { compactNumber } from '../../../../core/utils/formatters';
import { ListingCardComponent } from '../../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink, ListingCardComponent, ReactiveFormsModule],
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss',
})
export class HomePageComponent {
  private readonly propertyApi = inject(PropertyApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly statsApi = inject(StatsApi);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly searchTab = signal<'properties' | 'vehicles'>('properties');

  protected readonly propertyForm = this.fb.group({
    destination: [''],
    checkIn: [''],
    checkOut: [''],
    guests: [1],
    propertyType: [''],
  });

  protected readonly vehicleForm = this.fb.group({
    pickupCity: [''],
    pickupDate: [''],
    returnDate: [''],
    transmission: [''],
  });

  protected readonly citySuggestions = signal<string[]>([]);
  protected readonly showCitySuggestions = signal(false);
  protected readonly properties = signal<Property[]>([]);
  protected readonly vehicles = signal<Vehicle[]>([]);
  protected readonly stats = signal<Record<string, number>>({});

  constructor() {
    forkJoin({
      properties: this.propertyApi.search({ size: 4 }),
      vehicles: this.vehicleApi.search({ size: 4 }),
      stats: this.statsApi.get(),
      cities: this.propertyApi.getCities(),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ properties, vehicles, stats, cities }) => {
          this.properties.set(properties.data);
          this.vehicles.set(vehicles.data);
          this.stats.set(stats.data);
          this.citySuggestions.set(cities);
        },
        error: () => {},
      });
  }

  protected heroStats(): Array<{ label: string; value: string }> {
    const stats = this.stats();
    return [
      {
        label: 'Live listings',
        value: compactNumber((stats['properties'] ?? 0) + (stats['vehicles'] ?? 0)),
      },
      { label: 'Confirmed bookings', value: compactNumber(stats['bookings'] ?? 0) },
      { label: 'Active members', value: compactNumber(stats['users'] ?? 0) },
    ];
  }

  protected compact(value: number): string {
    return compactNumber(value);
  }

  protected filteredCities(): string[] {
    const query = this.propertyForm.get('destination')?.value?.toLowerCase() || '';
    if (!query) return this.citySuggestions().slice(0, 6);
    return this.citySuggestions()
      .filter((c) => c.toLowerCase().includes(query))
      .slice(0, 6);
  }

  protected filteredVehicleCities(): string[] {
    const query = this.vehicleForm.get('pickupCity')?.value?.toLowerCase() || '';
    if (!query) return this.citySuggestions().slice(0, 6);
    return this.citySuggestions()
      .filter((c) => c.toLowerCase().includes(query))
      .slice(0, 6);
  }

  protected selectCity(city: string): void {
    this.propertyForm.patchValue({ destination: city });
    this.showCitySuggestions.set(false);
  }

  protected selectVehicleCity(city: string): void {
    this.vehicleForm.patchValue({ pickupCity: city });
    this.showCitySuggestions.set(false);
  }

  protected onPropertySearch(): void {
    const f = this.propertyForm.getRawValue();
    const queryParams: Record<string, string> = {};
    if (f.destination) queryParams['query'] = f.destination;
    if (f.checkIn) queryParams['startDate'] = f.checkIn;
    if (f.checkOut) queryParams['endDate'] = f.checkOut;
    if (f.propertyType) queryParams['propertyType'] = f.propertyType;
    if (f.guests && f.guests > 1) queryParams['guests'] = String(f.guests);
    this.router.navigate(['/properties'], { queryParams });
  }

  protected onVehicleSearch(): void {
    const f = this.vehicleForm.getRawValue();
    const queryParams: Record<string, string> = {};
    if (f.pickupCity) queryParams['city'] = f.pickupCity;
    if (f.pickupDate) queryParams['startDate'] = f.pickupDate;
    if (f.returnDate) queryParams['endDate'] = f.returnDate;
    if (f.transmission) queryParams['transmission'] = f.transmission;
    this.router.navigate(['/vehicles'], { queryParams });
  }

  protected incrementGuests(): void {
    const current = this.propertyForm.get('guests')?.value ?? 1;
    if (current < 20) this.propertyForm.patchValue({ guests: current + 1 });
  }

  protected decrementGuests(): void {
    const current = this.propertyForm.get('guests')?.value ?? 1;
    if (current > 1) this.propertyForm.patchValue({ guests: current - 1 });
  }

  protected todayDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  protected tomorrowDate(): string {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  }
}
