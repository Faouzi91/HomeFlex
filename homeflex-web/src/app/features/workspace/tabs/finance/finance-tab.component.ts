import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { ApiClient } from '../../../../core/api/api.client';
import { Receipt } from '../../../../core/models/api.types';

@Component({
  selector: 'app-finance-tab',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './finance-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FinanceTabComponent implements OnInit {
  private readonly api = inject(ApiClient);

  protected readonly receipts = signal<Receipt[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getMyReceipts().subscribe({
      next: (data) => {
        this.receipts.set(data ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load receipts.');
        this.loading.set(false);
      },
    });
  }

  protected statusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PAID': return 'bg-emerald-50 text-emerald-700';
      case 'PENDING': return 'bg-amber-50 text-amber-700';
      case 'REFUNDED': return 'bg-blue-50 text-blue-700';
      default: return 'bg-slate-100 text-slate-600';
    }
  }
}
