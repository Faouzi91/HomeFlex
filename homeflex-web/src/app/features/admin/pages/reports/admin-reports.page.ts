import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { DatePipe, SlicePipe } from '@angular/common';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { ReportItem } from '../../../../core/models/api.types';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [FormsModule, DatePipe, SlicePipe],
  templateUrl: './admin-reports.page.html',
})
export class AdminReportsPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly reports = signal<ReportItem[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly loading = signal(false);
  protected readonly statusFilter = signal('ALL');
  protected readonly actionLoading = signal<string | null>(null);

  // Resolve modal state
  protected readonly showResolveModal = signal(false);
  protected readonly resolveTargetId = signal<string | null>(null);
  protected readonly resolveNotes = signal('');

  constructor() {
    this.loadReports();
  }

  protected loadReports(page = 0): void {
    this.loading.set(true);
    this.adminApi
      .getReports(page, this.pageSize())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.reports.set(res.data);
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
      this.loadReports(page);
    }
  }

  protected openResolveModal(report: ReportItem): void {
    this.resolveTargetId.set(report.id);
    this.resolveNotes.set('');
    this.showResolveModal.set(true);
  }

  protected closeResolveModal(): void {
    this.showResolveModal.set(false);
    this.resolveTargetId.set(null);
    this.resolveNotes.set('');
  }

  protected confirmResolve(): void {
    const id = this.resolveTargetId();
    if (!id) return;

    this.actionLoading.set(id);
    const notes = this.resolveNotes().trim() || undefined;
    this.adminApi
      .resolveReport(id, notes)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.reports.update((list) => list.map((r) => (r.id === updated.id ? updated : r)));
          this.actionLoading.set(null);
          this.closeResolveModal();
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected filteredReports(): ReportItem[] {
    if (this.statusFilter() === 'ALL') return this.reports();
    return this.reports().filter((r) => r.status === this.statusFilter());
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
