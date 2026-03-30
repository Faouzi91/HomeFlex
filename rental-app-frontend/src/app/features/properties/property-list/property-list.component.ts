import { Component, inject, signal, effect, ViewChild, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonInfiniteScroll, IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { PropertyCardComponent } from '../property-card/property-card.component';
import { PropertyStore, PropertyFilters } from 'src/app/core/state/property.store';
import { ListingType, PropertyType } from 'src/app/models/property.model';

@Component({
  selector: 'app-property-list',
  standalone: true,
  imports: [IonicModule, PropertyCardComponent, TranslateModule, FormsModule],
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.scss'],
})
export class PropertyListComponent implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll!: IonInfiniteScroll;

  readonly store = inject(PropertyStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  viewMode = signal<'grid' | 'list'>('grid');
  showFilters = signal(false);

  propertyTypes = Object.values(PropertyType);
  listingTypes = Object.values(ListingType);

  bedroomOptions = [
    { key: 'studioBeds', value: 0 },
    { key: 'onePlusBeds', value: 1 },
    { key: 'twoPlusBeds', value: 2 },
    { key: 'threePlusBeds', value: 3 },
    { key: 'fourPlusBeds', value: 4 },
  ];

  priceRanges = [
    { key: 'under100k', min: 0, max: 100000 },
    { key: '100to200k', min: 100000, max: 200000 },
    { key: '200to500k', min: 200000, max: 500000 },
    { key: 'over500k', min: 500000, max: null as number | null },
  ];

  /** Local mutable copy of filters for ngModel bindings in the sidebar. */
  filterDraft: PropertyFilters = {
    sortBy: 'createdAt',
    sortDirection: 'desc',
  };

  constructor() {
    // Sync infinite scroll disabled state with store
    effect(() => {
      const hasMore = this.store.hasMore();
      if (this.infiniteScroll) {
        this.infiniteScroll.disabled = !hasMore;
      }
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const filters: PropertyFilters = {
        sortBy: params['sortBy'] || 'createdAt',
        sortDirection: params['sortDirection'] || 'desc',
        city: params['city'] || undefined,
        propertyType: params['propertyType'] || undefined,
        listingType: params['listingType'] || undefined,
        minPrice: params['minPrice'] ? Number(params['minPrice']) : undefined,
        maxPrice: params['maxPrice'] ? Number(params['maxPrice']) : undefined,
        bedrooms: params['bedrooms'] ? Number(params['bedrooms']) : undefined,
        bathrooms: params['bathrooms'] ? Number(params['bathrooms']) : undefined,
      };

      this.filterDraft = { ...filters };
      this.store.load(filters);
    });
  }

  loadMore(): void {
    this.store.loadMore();
  }

  toggleViewMode(): void {
    this.viewMode.update((v) => (v === 'grid' ? 'list' : 'grid'));
  }

  toggleFilters(): void {
    this.showFilters.update((v) => !v);
  }

  applyFilters(): void {
    this.updateQueryParams(this.filterDraft);
    this.showFilters.set(false);
  }

  clearFilters(): void {
    this.store.clearFilters();
    this.filterDraft = { sortBy: 'createdAt', sortDirection: 'desc' };
    this.updateQueryParams(this.filterDraft);
  }

  selectBedrooms(value: number): void {
    this.filterDraft.bedrooms = value;
    this.applyFilters();
  }

  selectPriceRange(min: number, max: number | null): void {
    this.filterDraft.minPrice = min;
    this.filterDraft.maxPrice = max ?? undefined;
    this.applyFilters();
  }

  setSortBy(sortBy: string, sortDirection: 'asc' | 'desc' = 'desc'): void {
    this.filterDraft.sortBy = sortBy;
    this.filterDraft.sortDirection = sortDirection;
    this.applyFilters();
  }

  getListingTypeKey(): string {
    const lt = this.store.filters()?.listingType;
    if (!lt) return 'property.forRent';
    const normalized = String(lt).toUpperCase();
    switch (normalized) {
      case 'RENT':
      case 'SHORT_TERM':
        return 'property.forRent';
      case 'SALE':
      case 'SELL':
        return 'property.forSale';
      default:
        return 'property.forRent';
    }
  }

  navigateToPropertyDetail(propertyId: string): void {
    this.router.navigate(['/properties', propertyId]);
  }

  private updateQueryParams(filters: PropertyFilters): void {
    const params: Record<string, string | number> = {};
    for (const [key, value] of Object.entries(filters)) {
      if (
        value !== null &&
        value !== undefined &&
        value !== '' &&
        key !== 'page' &&
        key !== 'size'
      ) {
        params[key] = value as string | number;
      }
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge',
    });
  }
}
