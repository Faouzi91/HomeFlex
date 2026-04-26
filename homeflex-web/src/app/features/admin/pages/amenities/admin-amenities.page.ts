import { Component, DestroyRef, inject, signal, computed } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { Amenity } from '../../../../core/models/api.types';
import { catchError, of } from 'rxjs';

type AmenityForm = { name: string; nameFr: string; icon: string; category: string };

@Component({
  selector: 'app-admin-amenities',
  templateUrl: './admin-amenities.page.html',
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class AdminAmenitiesPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly amenities = signal<Amenity[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly saveError = signal<string | null>(null);
  protected readonly deletingId = signal<string | null>(null);

  protected readonly showModal = signal(false);
  protected readonly editingId = signal<string | null>(null);
  protected readonly form = signal<AmenityForm>({ name: '', nameFr: '', icon: '', category: '' });

  protected readonly modalTitle = computed(() => this.editingId() ? 'Edit Amenity' : 'New Amenity');

  protected readonly categories = [
    'GENERAL', 'KITCHEN', 'BATHROOM', 'BEDROOM', 'OUTDOOR',
    'SAFETY', 'ENTERTAINMENT', 'TRANSPORT', 'ACCESSIBILITY', 'OTHER',
  ];

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminApi
      .getAmenities()
      .pipe(
        catchError(() => of([] as Amenity[])),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((list) => {
        this.amenities.set(list);
        this.loading.set(false);
      });
  }

  protected openCreate(): void {
    this.editingId.set(null);
    this.form.set({ name: '', nameFr: '', icon: '', category: '' });
    this.saveError.set(null);
    this.showModal.set(true);
  }

  protected openEdit(a: Amenity): void {
    this.editingId.set(a.id);
    this.form.set({ name: a.name, nameFr: a.nameFr ?? '', icon: a.icon ?? '', category: a.category ?? '' });
    this.saveError.set(null);
    this.showModal.set(true);
  }

  protected closeModal(): void {
    this.showModal.set(false);
  }

  protected save(): void {
    const f = this.form();
    if (!f.name.trim()) return;
    this.saving.set(true);
    this.saveError.set(null);

    const id = this.editingId();
    const call$ = id
      ? this.adminApi.updateAmenity(id, f)
      : this.adminApi.createAmenity(f as { name: string; nameFr: string; icon: string; category: string });

    call$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (result) => {
        this.amenities.update((list) => {
          if (id) return list.map((a) => (a.id === id ? result : a));
          return [...list, result];
        });
        this.saving.set(false);
        this.showModal.set(false);
      },
      error: (err) => {
        this.saveError.set(err?.error?.message ?? 'Failed to save amenity');
        this.saving.set(false);
      },
    });
  }

  protected deleteAmenity(id: string): void {
    this.deletingId.set(id);
    this.adminApi
      .deleteAmenity(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.amenities.update((list) => list.filter((a) => a.id !== id));
          this.deletingId.set(null);
        },
        error: () => this.deletingId.set(null),
      });
  }

  protected updateForm(field: keyof AmenityForm, value: string): void {
    this.form.update((f) => ({ ...f, [field]: value }));
  }
}
