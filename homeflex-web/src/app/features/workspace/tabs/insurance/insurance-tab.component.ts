import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ApiClient } from '../../../../core/api/api.client';
import { InsurancePlan } from '../../../../core/models/api.types';

@Component({
  selector: 'app-insurance-tab',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './insurance-tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InsuranceTabComponent implements OnInit {
  private readonly api = inject(ApiClient);

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

  ngOnInit(): void {
    this.api.getInsurancePlans('TENANT').subscribe({
      next: (data) => {
        this.allPlans.set(data ?? []);
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
    // purchaseInsurancePolicy requires a bookingId — surface a message instead
    this.purchasing.set(null);
    this.purchaseSuccess.set(
      `Plan "${plan.name}" noted. Select it when completing a booking checkout.`,
    );
  }
}
