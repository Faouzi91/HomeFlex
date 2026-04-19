import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DisputeApi } from '../../../../../core/api/services/dispute.api';

const DISPUTE_REASONS = [
  { value: 'DAMAGE', label: 'Property damage' },
  { value: 'DEPOSIT_RETURN', label: 'Deposit not returned' },
  { value: 'SERVICE_QUALITY', label: 'Poor service quality' },
  { value: 'MISREPRESENTATION', label: 'Property misrepresented' },
  { value: 'UNAUTHORIZED_CHARGE', label: 'Unauthorized charge' },
  { value: 'OTHER', label: 'Other' },
];

@Component({
  selector: 'app-dispute-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- Overlay -->
    <div
      class="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4"
    >
      <div class="w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden">
        <!-- Header -->
        <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 class="text-base font-bold text-slate-900">Open a Dispute</h2>
            <p class="text-xs text-slate-500 mt-0.5">
              An admin will review your case within 48 hours.
            </p>
          </div>
          <button
            (click)="cancelled.emit()"
            class="h-8 w-8 rounded-xl flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-700 transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              class="w-4 h-4"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Form -->
        <form [formGroup]="form" (ngSubmit)="submit()" class="px-6 py-5 space-y-4">
          <!-- Reason -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">Reason</label>
            <select
              formControlName="reason"
              class="w-full rounded-xl border border-slate-200 px-3 py-2.5 text-sm text-slate-900 bg-white focus:outline-none focus:ring-2 focus:ring-brand-400"
            >
              <option value="">Select a reason…</option>
              @for (r of reasons; track r.value) {
                <option [value]="r.value">{{ r.label }}</option>
              }
            </select>
          </div>

          <!-- Description -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">
              Describe the issue
            </label>
            <textarea
              formControlName="description"
              rows="4"
              placeholder="Provide as much detail as possible…"
              class="w-full rounded-xl border border-slate-200 px-3 py-2.5 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-400 resize-none"
            ></textarea>
            <p class="text-xs text-slate-400 mt-1 text-right">
              {{ form.value.description?.length ?? 0 }} / 1000
            </p>
          </div>

          @if (error()) {
            <p class="text-sm text-rose-600">{{ error() }}</p>
          }

          @if (submitted()) {
            <div
              class="rounded-xl bg-emerald-50 border border-emerald-200 p-4 text-sm text-emerald-700 text-center"
            >
              Dispute submitted. You'll receive updates via notifications.
            </div>
          }

          <!-- Actions -->
          @if (!submitted()) {
            <div class="flex gap-3 pt-1">
              <button
                type="button"
                (click)="cancelled.emit()"
                class="flex-1 py-2.5 text-sm font-semibold text-slate-600 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                [disabled]="form.invalid || loading()"
                class="flex-1 py-2.5 text-sm font-semibold text-white bg-rose-500 hover:bg-rose-600 rounded-xl transition-colors disabled:opacity-50"
              >
                {{ loading() ? 'Submitting…' : 'Submit Dispute' }}
              </button>
            </div>
          } @else {
            <button
              type="button"
              (click)="cancelled.emit()"
              class="w-full py-2.5 text-sm font-semibold text-slate-700 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors"
            >
              Close
            </button>
          }
        </form>
      </div>
    </div>
  `,
})
export class DisputeModalComponent {
  private readonly disputeApi = inject(DisputeApi);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  readonly bookingId = input.required<string>();
  readonly submitted$ = output<void>();
  readonly cancelled = output<void>();

  protected readonly reasons = DISPUTE_REASONS;
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.group({
    reason: ['', Validators.required],
    description: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(1000)]],
  });

  protected submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);

    const { reason, description } = this.form.value;
    this.disputeApi
      .open(this.bookingId(), reason!, description!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.submitted.set(true);
          this.submitted$.emit();
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Failed to submit dispute. Please try again.');
        },
      });
  }
}
