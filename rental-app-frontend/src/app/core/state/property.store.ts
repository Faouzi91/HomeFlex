import { computed, inject } from '@angular/core';
import { signalStore, withState, withComputed, withMethods, withHooks } from '@ngrx/signals';
import {
  withEntities,
  setAllEntities,
  addEntities,
  removeAllEntities,
} from '@ngrx/signals/entities';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { patchState } from '@ngrx/signals';
import { pipe, switchMap, tap, debounceTime, distinctUntilChanged, EMPTY } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Property, PropertySearchParams } from '../../models/property.model';
import { ApiPageResponse } from '../../types/api.types';
import { environment } from 'src/app/environments/environment';

export type PropertyEntityId = { id: string };

export interface PropertyFilters {
  q?: string;
  city?: string;
  propertyType?: string;
  listingType?: string;
  minPrice?: number;
  maxPrice?: number;
  bedrooms?: number;
  bathrooms?: number;
  sortBy: string;
  sortDirection: 'asc' | 'desc';
}

interface PropertyPagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

interface PropertyStoreState {
  filters: PropertyFilters;
  pagination: PropertyPagination;
  loading: boolean;
  error: string | null;
}

const initialFilters: PropertyFilters = {
  sortBy: 'createdAt',
  sortDirection: 'desc',
};

const initialState: PropertyStoreState = {
  filters: initialFilters,
  pagination: { page: 0, size: 20, totalElements: 0, totalPages: 0 },
  loading: false,
  error: null,
};

export const PropertyStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withEntities<Property>(),
  withComputed((store) => ({
    hasMore: computed(() => store.pagination().page < store.pagination().totalPages - 1),
    isEmpty: computed(() => store.entities().length === 0 && !store.loading()),
    hasActiveFilters: computed(() => {
      const f = store.filters();
      return !!(
        f.q ||
        f.city ||
        f.propertyType ||
        f.listingType ||
        f.minPrice ||
        f.maxPrice ||
        f.bedrooms ||
        f.bathrooms
      );
    }),
  })),
  withMethods((store, http = inject(HttpClient)) => {
    const apiUrl = `${environment.apiUrl}/properties`;

    function buildHttpParams(filters: PropertyFilters, page: number, size: number): HttpParams {
      let params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString())
        .set('sortBy', filters.sortBy)
        .set('sortDirection', filters.sortDirection);

      const optionalKeys: (keyof PropertyFilters)[] = [
        'q',
        'city',
        'propertyType',
        'listingType',
        'minPrice',
        'maxPrice',
        'bedrooms',
        'bathrooms',
      ];

      for (const key of optionalKeys) {
        const value = filters[key];
        if (value !== undefined && value !== null && value !== '') {
          params = params.set(key, String(value));
        }
      }

      return params;
    }

    function executeSearch(filters: PropertyFilters, page: number, append: boolean) {
      patchState(store, { loading: true, error: null });

      const params = buildHttpParams(filters, page, store.pagination().size);

      return http.get<ApiPageResponse<Property>>(`${apiUrl}/search`, { params }).pipe(
        tap({
          next: (response) => {
            if (append) {
              patchState(store, addEntities(response.data));
            } else {
              patchState(store, setAllEntities(response.data));
            }
            patchState(store, {
              pagination: {
                page: response.page,
                size: response.size,
                totalElements: response.totalElements,
                totalPages: response.totalPages,
              },
              loading: false,
            });
          },
          error: (err) => {
            patchState(store, { loading: false, error: err?.message ?? 'Search failed' });
          },
        })
      );
    }

    return {
      /** Debounced search triggered by filter changes. */
      search: rxMethod<PropertyFilters>(
        pipe(
          debounceTime(300),
          distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
          switchMap((filters) => {
            patchState(store, { filters });
            return executeSearch(filters, 0, false);
          })
        )
      ),

      /** Immediate search (e.g., on initial load or explicit apply). */
      load: rxMethod<PropertyFilters>(
        pipe(
          switchMap((filters) => {
            patchState(store, { filters });
            return executeSearch(filters, 0, false);
          })
        )
      ),

      /** Loads the next page for infinite scroll. */
      loadMore: rxMethod<void>(
        pipe(
          switchMap(() => {
            const { page, totalPages } = store.pagination();
            if (page >= totalPages - 1) return EMPTY;
            return executeSearch(store.filters(), page + 1, true);
          })
        )
      ),

      /** Updates filters and triggers a debounced search. */
      updateFilters(partial: Partial<PropertyFilters>): void {
        const merged = { ...store.filters(), ...partial };
        patchState(store, { filters: merged });
      },

      /** Resets everything back to defaults. */
      clearFilters(): void {
        patchState(store, removeAllEntities());
        patchState(store, {
          filters: { ...initialFilters },
          pagination: { ...initialState.pagination },
        });
      },
    };
  })
);
