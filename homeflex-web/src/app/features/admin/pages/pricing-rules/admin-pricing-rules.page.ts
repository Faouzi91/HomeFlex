import { Component, DestroyRef, inject, signal, computed } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { AdminPricingRule } from '../../../../core/models/api.types';

@Component({
  selector: 'app-admin-pricing-rules',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './admin-pricing-rules.page.html',
})
export class AdminPricingRulesPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly rules = signal<AdminPricingRule[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly deletingId = signal<string | null>(null);
  protected readonly typeFilter = signal<string>('ALL');
  protected readonly query = signal<string>('');

  protected readonly filtered = computed(() => {
    let list = this.rules();
    const t = this.typeFilter();
    const q = this.query().toLowerCase();
    if (t !== 'ALL') list = list.filter((r) => r.ruleType === t);
    if (q) {
      list = list.filter(
        (r) =>
          (r.propertyTitle ?? '').toLowerCase().includes(q) ||
          (r.label ?? '').toLowerCase().includes(q),
      );
    }
    return list;
  });

  protected readonly ruleTypes = ['ALL', 'WEEKEND', 'SEASONAL', 'LONG_STAY'];

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.adminApi
      .listAllPricingRules()
      .pipe(
        catchError((err) => {
          this.error.set(err?.error?.message ?? 'Failed to load pricing rules');
          return of([] as AdminPricingRule[]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((list) => {
        this.rules.set(list);
        this.loading.set(false);
      });
  }

  protected deleteRule(rule: AdminPricingRule): void {
    if (!confirm(`Delete pricing rule "${rule.label || rule.ruleType}"?`)) return;
    this.deletingId.set(rule.id);
    this.adminApi
      .deletePricingRule(rule.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.rules.update((list) => list.filter((r) => r.id !== rule.id));
          this.deletingId.set(null);
        },
        error: () => this.deletingId.set(null),
      });
  }

  protected typeBadgeClass(t: string): string {
    switch (t) {
      case 'WEEKEND':
        return 'bg-amber-50 text-amber-700';
      case 'SEASONAL':
        return 'bg-sky-50 text-sky-700';
      case 'LONG_STAY':
        return 'bg-emerald-50 text-emerald-700';
      default:
        return 'bg-slate-50 text-slate-600';
    }
  }

  protected multiplierClass(m: number): string {
    if (m > 1) return 'text-rose-600';
    if (m < 1) return 'text-emerald-600';
    return 'text-slate-600';
  }
}
