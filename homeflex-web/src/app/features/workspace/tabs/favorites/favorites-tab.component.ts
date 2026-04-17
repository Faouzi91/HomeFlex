import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { Property } from '../../../../core/models/api.types';
import { formatCurrency } from '../../../../core/utils/formatters';

type Filter = 'ALL' | 'RENT' | 'SALE';

@Component({
  selector: 'app-favorites-tab',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './favorites-tab.component.html',
})
export class FavoritesTabComponent {
  private readonly favoriteApi = inject(FavoriteApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly favorites = signal<Property[]>([]);
  protected readonly activeFilter = signal<Filter>('ALL');
  protected readonly loading = signal(true);

  protected readonly filtered = computed(() => {
    const f = this.activeFilter();
    if (f === 'ALL') return this.favorites();
    return this.favorites().filter((p) => p.listingType === f);
  });

  constructor() {
    this.favoriteApi
      .getAll()
      .pipe(
        catchError(() => of({ data: [] as Property[] })),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => {
        this.favorites.set(res.data);
        this.loading.set(false);
      });
  }

  protected price(p: Property): string {
    return formatCurrency(p.price, p.currency);
  }

  protected thumb(p: Property): string | null {
    return p.images?.[0]?.thumbnailUrl ?? p.images?.[0]?.imageUrl ?? null;
  }
}
