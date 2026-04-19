import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiClient } from '../../../../core/api/api.client';
import { Dispute } from '../../../../core/models/api.types';

@Component({
  selector: 'app-disputes-tab',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './disputes-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DisputesTabComponent implements OnInit {
  private readonly api = inject(ApiClient);

  protected readonly disputes = signal<Dispute[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getMyDisputes().subscribe({
      next: (data) => {
        this.disputes.set(Array.isArray(data) ? data : ((data as any)?.data ?? []));
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load disputes.');
        this.loading.set(false);
      },
    });
  }

  protected statusClass(status: string): string {
    switch (status) {
      case 'OPEN':
        return 'bg-red-50 text-red-700';
      case 'UNDER_REVIEW':
        return 'bg-amber-50 text-amber-700';
      case 'RESOLVED':
        return 'bg-emerald-50 text-emerald-700';
      case 'CLOSED':
        return 'bg-slate-100 text-slate-600';
      default:
        return 'bg-slate-100 text-slate-600';
    }
  }

  protected statusLabel(status: string): string {
    return status.replace('_', ' ');
  }
}
