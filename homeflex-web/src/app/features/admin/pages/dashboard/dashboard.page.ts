import { Component, DestroyRef, inject, signal } from '@angular/core';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Analytics } from '../../../../core/models/api.types';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-bold tracking-tight text-slate-900">Dashboard Intelligence</h1>
      </div>

      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <!-- Metric Cards -->
        <div
          class="overflow-hidden rounded-xl bg-white px-4 py-5 shadow sm:p-6 border border-slate-100 relative group transition-all hover:shadow-lg hover:border-brand-200"
        >
          <div
            class="absolute right-0 top-0 h-full w-1 bg-brand-500 rounded-r-xl transition-all scale-y-0 group-hover:scale-y-100"
          ></div>
          <dt class="truncate text-sm font-bold text-slate-500">Total Users</dt>
          <dd class="mt-1 text-3xl font-extrabold tracking-tight text-slate-900">
            {{ analytics()?.totalUsers | number }}
          </dd>
        </div>

        <div
          class="overflow-hidden rounded-xl bg-white px-4 py-5 shadow sm:p-6 border border-slate-100 relative group transition-all hover:shadow-lg hover:border-brand-200"
        >
          <div
            class="absolute right-0 top-0 h-full w-1 bg-brand-500 rounded-r-xl transition-all scale-y-0 group-hover:scale-y-100"
          ></div>
          <dt class="truncate text-sm font-bold text-slate-500">Active Properties</dt>
          <dd class="mt-1 text-3xl font-extrabold tracking-tight text-slate-900">
            {{ analytics()?.totalProperties | number }}
          </dd>
        </div>

        <div
          class="overflow-hidden rounded-xl bg-white px-4 py-5 shadow sm:p-6 border border-slate-100 relative group transition-all hover:shadow-lg hover:border-brand-200"
        >
          <div
            class="absolute right-0 top-0 h-full w-1 bg-brand-500 rounded-r-xl transition-all scale-y-0 group-hover:scale-y-100"
          ></div>
          <dt class="truncate text-sm font-bold text-slate-500">Pending Properties</dt>
          <dd class="mt-1 text-3xl font-extrabold tracking-tight text-blue-600">
            {{ analytics()?.pendingProperties | number }}
          </dd>
        </div>

        <div
          class="overflow-hidden rounded-xl bg-white px-4 py-5 shadow sm:p-6 border border-slate-100 relative group transition-all hover:shadow-lg hover:border-brand-200"
        >
          <div
            class="absolute right-0 top-0 h-full w-1 bg-brand-500 rounded-r-xl transition-all scale-y-0 group-hover:scale-y-100"
          ></div>
          <dt class="truncate text-sm font-bold text-slate-500">Total Bookings</dt>
          <dd class="mt-1 text-3xl font-extrabold tracking-tight text-green-600">
            {{ analytics()?.totalBookings | number }}
          </dd>
        </div>
      </div>

      <!-- Extended Dashboard Block Example -->
      <div class="rounded-xl bg-white shadow border border-slate-100">
        <div class="border-b border-slate-200 px-6 py-5">
          <h3 class="text-base font-bold leading-6 text-slate-900">Recent Platform Activity</h3>
        </div>
        <div class="px-6 py-12 text-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="mx-auto h-12 w-12 text-slate-300"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z"
            />
          </svg>
          <h3 class="mt-2 text-sm font-semibold text-slate-900">Analytics Active</h3>
          <p class="mt-1 text-sm text-slate-500">Live platform throughput rendering active.</p>
        </div>
      </div>
    </div>
  `,
})
export class AdminDashboardPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly analytics = signal<Analytics | null>(null);

  constructor() {
    this.adminApi
      .getAnalytics()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data) => {
        this.analytics.set(data);
      });
  }
}
