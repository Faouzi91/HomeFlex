import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  signal,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { InsuranceApi } from '../../../../core/api/services/insurance.api';
import { InsurancePlan } from '../../../../core/models/api.types';

@Component({
  selector: 'app-insurance-tab',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './insurance-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InsuranceTabComponent {
  private readonly insuranceApi = inject(InsuranceApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly allPlans = signal<InsurancePlan[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly purchasing = signal<string | null>(null);
  protected readonly purchaseError = signal<string | null>(null);
  protected readonly purchaseSuccess = signal<string | null>(null);

  protected readonly tenantPlans = computed(() =>
    this.allPlans().filter((p) => p.type === 'TENANT'),
  );
  protected readonly landlordPlans = computed(() =>
    this.allPlans().filter((p) => p.type === 'LANDLORD'),
  );

  constructor() {
    forkJoin({
      tenant: this.insuranceApi
        .getPlans('TENANT')
        .pipe(catchError(() => of([] as InsurancePlan[]))),
      landlord: this.insuranceApi
        .getPlans('LANDLORD')
        .pipe(catchError(() => of([] as InsurancePlan[]))),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ tenant, landlord }) => {
          this.allPlans.set([...(tenant ?? []), ...(landlord ?? [])]);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Failed to load insurance plans.');
          this.loading.set(false);
        },
      });
  }

  protected selectPlan(plan: InsurancePlan): void {
    this.purchaseError.set(null);
    this.purchaseSuccess.set(null);
    this.purchasing.set(plan.id);
    // purchaseInsurancePolicy requires a bookingId — guide the user to do it at checkout
    this.purchasing.set(null);
    this.purchaseSuccess.set(
      `Plan "${plan.name}" noted. Select it when completing a booking checkout.`,
    );
  }
}
