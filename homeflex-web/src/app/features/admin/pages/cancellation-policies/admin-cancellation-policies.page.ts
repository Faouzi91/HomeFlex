import { Component, DestroyRef, inject, signal, computed } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { CancellationPolicy, CancellationPolicyRequest } from '../../../../core/models/api.types';

type PolicyForm = CancellationPolicyRequest;

@Component({
  selector: 'app-admin-cancellation-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-cancellation-policies.page.html',
})
export class AdminCancellationPoliciesPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly policies = signal<CancellationPolicy[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly showModal = signal(false);
  protected readonly editingId = signal<string | null>(null);
  protected readonly form = signal<PolicyForm>(this.emptyForm());
  protected readonly saving = signal(false);
  protected readonly saveError = signal<string | null>(null);
  protected readonly deletingId = signal<string | null>(null);

  protected readonly modalTitle = computed(() => (this.editingId() ? 'Edit Policy' : 'New Policy'));

  constructor() {
    this.load();
  }

  private emptyForm(): PolicyForm {
    return {
      code: '',
      name: '',
      description: '',
      refundPercentage: 100,
      hoursBeforeCheckin: 24,
      isActive: true,
    };
  }

  private load(): void {
    this.loading.set(true);
    this.adminApi
      .listCancellationPolicies()
      .pipe(
        catchError((err) => {
          this.error.set(err?.error?.message ?? 'Failed to load cancellation policies');
          return of([] as CancellationPolicy[]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((list) => {
        this.policies.set(list);
        this.loading.set(false);
      });
  }

  protected openCreate(): void {
    this.editingId.set(null);
    this.form.set(this.emptyForm());
    this.saveError.set(null);
    this.showModal.set(true);
  }

  protected openEdit(p: CancellationPolicy): void {
    this.editingId.set(p.id);
    this.form.set({
      code: p.code,
      name: p.name,
      description: p.description ?? '',
      refundPercentage: p.refundPercentage,
      hoursBeforeCheckin: p.hoursBeforeCheckin,
      isActive: p.isActive,
    });
    this.saveError.set(null);
    this.showModal.set(true);
  }

  protected closeModal(): void {
    this.showModal.set(false);
  }

  protected updateForm<K extends keyof PolicyForm>(field: K, value: PolicyForm[K]): void {
    this.form.update((f) => ({ ...f, [field]: value }));
  }

  protected save(): void {
    const f = this.form();
    if (!f.code.trim() || !f.name.trim()) return;

    this.saving.set(true);
    this.saveError.set(null);

    const id = this.editingId();
    const call$ = id
      ? this.adminApi.updateCancellationPolicy(id, f)
      : this.adminApi.createCancellationPolicy(f);

    call$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (result) => {
        this.policies.update((list) => {
          if (id) return list.map((p) => (p.id === id ? result : p));
          return [...list, result];
        });
        this.saving.set(false);
        this.showModal.set(false);
      },
      error: (err) => {
        this.saveError.set(err?.error?.message ?? 'Failed to save policy');
        this.saving.set(false);
      },
    });
  }

  protected deletePolicy(p: CancellationPolicy): void {
    if (!confirm(`Delete cancellation policy "${p.name}"? Listings using it will need to be reassigned.`)) return;
    this.deletingId.set(p.id);
    this.adminApi
      .deleteCancellationPolicy(p.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.policies.update((list) => list.filter((x) => x.id !== p.id));
          this.deletingId.set(null);
        },
        error: (err) => {
          this.deletingId.set(null);
          this.error.set(err?.error?.message ?? 'Failed to delete policy');
        },
      });
  }
}
