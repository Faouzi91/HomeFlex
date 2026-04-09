import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiClient } from '../../../core/api/api.client';
import { Property, PropertySearchParams } from '../../../core/models/api.types';
import { ListingCardComponent } from '../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-properties-page',
  imports: [ReactiveFormsModule, ListingCardComponent],
  templateUrl: './properties.page.html',
  styleUrl: './properties.page.scss',
})
export class PropertiesPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClient);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly properties = signal<Property[]>([]);
  protected readonly filters = this.fb.group({
    q: [''],
    city: [''],
    propertyType: [''],
    minPrice: [''],
    maxPrice: [''],
    bedrooms: [''],
    bathrooms: [''],
  });

  constructor() {
    this.search();
  }

  protected search(): void {
    const raw = this.filters.getRawValue();
    const payload: PropertySearchParams = {
      q: raw.q || undefined,
      city: raw.city || undefined,
      propertyType: raw.propertyType || undefined,
      minPrice: raw.minPrice ? Number(raw.minPrice) : null,
      maxPrice: raw.maxPrice ? Number(raw.maxPrice) : null,
      bedrooms: raw.bedrooms ? Number(raw.bedrooms) : null,
      bathrooms: raw.bathrooms ? Number(raw.bathrooms) : null,
      size: 24,
    };

    this.api
      .searchProperties(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.properties.set(response.data));
  }
}
