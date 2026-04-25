import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DecimalPipe } from '@angular/common';
import { Analytics } from '../../../../core/models/api.types';

interface KvEntry { key: string; value: number; }

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './dashboard.page.html',
})
export class AdminDashboardPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly analytics = signal<Analytics | null>(null);

  protected readonly typeEntries = computed<KvEntry[]>(() =>
    this.sortedEntries(this.analytics()?.propertiesByType),
  );
  protected readonly cityEntries = computed<KvEntry[]>(() =>
    this.sortedEntries(this.analytics()?.propertiesByCity).slice(0, 6),
  );
  protected readonly statusEntries = computed<KvEntry[]>(() =>
    this.sortedEntries(this.analytics()?.bookingsByStatus),
  );

  protected readonly maxTypeCount = computed(() =>
    Math.max(1, ...this.typeEntries().map((e) => e.value)),
  );
  protected readonly maxCityCount = computed(() =>
    Math.max(1, ...this.cityEntries().map((e) => e.value)),
  );
  protected readonly maxStatusCount = computed(() =>
    Math.max(1, ...this.statusEntries().map((e) => e.value)),
  );

  constructor() {
    this.adminApi
      .getAnalytics()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data) => this.analytics.set(data));
  }

  protected pct(value: number, max: number): number {
    return max === 0 ? 0 : Math.round((value / max) * 100);
  }

  protected statusBarColor(status: string): string {
    const s = status.toUpperCase();
    if (s === 'APPROVED' || s === 'COMPLETED' || s === 'ACTIVE') return 'bg-emerald-500';
    if (s === 'PENDING_APPROVAL' || s === 'PAYMENT_PENDING') return 'bg-amber-400';
    if (s === 'CANCELLED' || s === 'REJECTED' || s === 'PAYMENT_FAILED') return 'bg-red-400';
    return 'bg-slate-400';
  }

  private sortedEntries(record?: Record<string, number>): KvEntry[] {
    if (!record) return [];
    return Object.entries(record)
      .map(([key, value]) => ({ key, value }))
      .sort((a, b) => b.value - a.value);
  }
}
