import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslateModule } from '@ngx-translate/core';
import { PropertyApi } from '../../../core/api/services/property.api';
import { Property, PropertySearchParams } from '../../../core/models/api.types';
import { ListingCardComponent } from '../../../shared/ui/listing-card/listing-card.component';
import { MapComponent } from '../../../shared/ui/map/map.component';

@Component({
  selector: 'app-properties-page',
  imports: [ReactiveFormsModule, ListingCardComponent, MapComponent, TranslateModule],
  templateUrl: './properties.page.html',
  styleUrl: './properties.page.scss',
})
export class PropertiesPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly propertyApi = inject(PropertyApi);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly properties = signal<Property[]>([]);
  protected readonly viewMode = signal<'grid' | 'map'>('grid');
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

    this.propertyApi
      .search(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.properties.set(response.data));
  }

  protected onPropertySelected(property: Property): void {
    this.router.navigate(['/properties', property.id]);
  }
}
