import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { FinanceApi } from '../../../../core/api/services/finance.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { SessionStore } from '../../../../core/state/session.store';
import { PayoutSummary, Receipt } from '../../../../core/models/api.types';

@Component({
  selector: 'app-finance-tab',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './finance-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FinanceTabComponent {
  private readonly financeApi = inject(FinanceApi);
  private readonly payoutApi = inject(PayoutApi);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly session = inject(SessionStore);

  protected readonly receipts = signal<Receipt[]>([]);
  protected readonly payout = signal<PayoutSummary | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly onboarding = signal(false);

  constructor() {
    const isHost = this.session.isLandlord() || this.session.isAdmin();

    forkJoin({
      receipts: this.financeApi.getMyReceipts().pipe(catchError(() => of([] as Receipt[]))),
      payout: isHost
        ? this.payoutApi.getSummary().pipe(catchError(() => of(null)))
        : of(null),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ receipts, payout }) => {
          this.receipts.set(receipts ?? []);
          this.payout.set(payout);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Failed to load receipts.');
          this.loading.set(false);
        },
      });
  }

  protected get stripeNotConnected(): boolean {
    const p = this.payout();
    return p !== null && !p.stripeAccountConnected;
  }

  protected onboardStripe(): void {
    this.onboarding.set(true);
    const base = window.location.origin + '/workspace/finance';
    this.payoutApi
      .onboardConnectAccount(base, base)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          window.location.href = res.onboardingUrl;
        },
        error: () => this.onboarding.set(false),
      });
  }

  protected statusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PAID':
        return 'bg-emerald-50 text-emerald-700';
      case 'PENDING':
        return 'bg-amber-50 text-amber-700';
      case 'REFUNDED':
        return 'bg-blue-50 text-blue-700';
      default:
        return 'bg-slate-100 text-slate-600';
    }
  }
}
