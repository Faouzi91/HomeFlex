import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { User } from '../../../../core/models/api.types';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './admin-users.page.html',
})
export class AdminUsersPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly users = signal<User[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly loading = signal(false);
  protected readonly searchQuery = signal('');
  protected readonly roleFilter = signal('ALL');
  protected readonly actionLoading = signal<string | null>(null);

  constructor() {
    this.loadUsers();
  }

  protected loadUsers(page = 0): void {
    this.loading.set(true);
    this.adminApi
      .getUsers(page, this.pageSize())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.users.set(res.data);
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
      this.loadUsers(page);
    }
  }

  protected suspendUser(user: User): void {
    this.actionLoading.set(user.id);
    this.adminApi
      .suspendUser(user.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
          this.actionLoading.set(null);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected activateUser(user: User): void {
    this.actionLoading.set(user.id);
    this.adminApi
      .activateUser(user.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
          this.actionLoading.set(null);
        },
        error: () => this.actionLoading.set(null),
      });
  }

  protected filteredUsers(): User[] {
    let result = this.users();
    const query = this.searchQuery().toLowerCase();
    if (query) {
      result = result.filter(
        (u) =>
          u.email.toLowerCase().includes(query) ||
          (u.firstName ?? '').toLowerCase().includes(query) ||
          (u.lastName ?? '').toLowerCase().includes(query),
      );
    }
    if (this.roleFilter() !== 'ALL') {
      result = result.filter((u) => u.role === this.roleFilter());
    }
    return result;
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
