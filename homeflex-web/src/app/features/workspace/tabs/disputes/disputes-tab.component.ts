import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { DisputeApi } from '../../../../core/api/services/dispute.api';
import { Dispute } from '../../../../core/models/api.types';

@Component({
  selector: 'app-disputes-tab',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './disputes-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DisputesTabComponent {
  private readonly disputeApi = inject(DisputeApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly disputes = signal<Dispute[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.disputeApi
      .getMine()
      .pipe(
        catchError(() => {
          this.error.set('Failed to load disputes.');
          this.loading.set(false);
          return of([] as Dispute[]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((data) => {
        this.disputes.set(data ?? []);
        this.loading.set(false);
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
