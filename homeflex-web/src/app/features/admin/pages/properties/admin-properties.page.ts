import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { Property } from '../../../../core/models/api.types';

@Component({
  selector: 'app-admin-properties',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './admin-properties.page.html',
})
export class AdminPropertiesPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly properties = signal<Property[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly pageSize = signal(12);
  protected readonly loading = signal(false);
  protected readonly actionLoading = signal<string | null>(null);

  // Reject modal state
  protected readonly showRejectModal = signal(false);
  protected readonly rejectTargetId = signal<string | null>(null);
  protected readonly rejectReason = signal('');

  constructor() {
    this.loadProperties();
  }

  protected loadProperties(page = 0): void {
    this.loading.set(true);
    this.adminApi
      .getPendingProperties(page, this.pageSize())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.properties.set(res.data);
          this.totalElements.set(res.totalElements);
          this.totalPages.set(res.totalPages);
          this.currentPage.set(res.page);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  protected goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.loadProperties(page);
    }
  }

  protected approveProperty(property: Property): void {
    this.actionLoading.set(property.id);
    this.adminApi
      .approveProperty(property.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.properties.update((list) => list.filter((p) => p.id !== property.id));
          this.totalElements.update((n) => n - 1);
          this.actionLoading.set(null);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected openRejectModal(property: Property): void {
    this.rejectTargetId.set(property.id);
    this.rejectReason.set('');
    this.showRejectModal.set(true);
  }

  protected closeRejectModal(): void {
    this.showRejectModal.set(false);
    this.rejectTargetId.set(null);
    this.rejectReason.set('');
  }

  protected confirmReject(): void {
    const id = this.rejectTargetId();
    if (!id || !this.rejectReason().trim()) return;

    this.actionLoading.set(id);
    this.adminApi
      .rejectProperty(id, this.rejectReason().trim())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.properties.update((list) => list.filter((p) => p.id !== id));
          this.totalElements.update((n) => n - 1);
          this.actionLoading.set(null);
          this.closeRejectModal();
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected getPrimaryImage(property: Property): string | null {
    const primary = property.images?.find((img) => img.isPrimary);
    return primary?.imageUrl ?? property.images?.[0]?.imageUrl ?? null;
  }

  protected pages(): number[] {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];
    const start = Math.max(0, current - 2);
    const end = Math.min(total, current + 3);
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
