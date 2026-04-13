import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { Vehicle, VehicleSearchParams } from '../../../../core/models/api.types';
import { ListingCardComponent } from '../../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-vehicles-page',
  imports: [ReactiveFormsModule, ListingCardComponent],
  templateUrl: './vehicles.page.html',
  styleUrl: './vehicles.page.scss',
})
export class VehiclesPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly vehicles = signal<Vehicle[]>([]);
  protected readonly filters = this.fb.group({
    brand: [''],
    model: [''],
    city: [''],
    transmission: [''],
    fuelType: [''],
    minPrice: [''],
    maxPrice: [''],
  });

  constructor() {
    this.route.queryParams.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      if (params['city']) this.filters.patchValue({ city: params['city'] });
      if (params['transmission']) this.filters.patchValue({ transmission: params['transmission'] });
      this.search();
    });
  }

  protected search(): void {
    const raw = this.filters.getRawValue();
    const payload: VehicleSearchParams = {
      brand: raw.brand || undefined,
      model: raw.model || undefined,
      city: raw.city || undefined,
      transmission: raw.transmission || undefined,
      fuelType: raw.fuelType || undefined,
      minPrice: raw.minPrice ? Number(raw.minPrice) : null,
      maxPrice: raw.maxPrice ? Number(raw.maxPrice) : null,
      size: 24,
    };

    this.vehicleApi
      .search(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.vehicles.set(response.data));
  }
}
