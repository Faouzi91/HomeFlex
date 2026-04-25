import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass, TitleCasePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { MaintenanceApi } from '../../../../core/api/services/maintenance.api';
import { NotificationService } from '../../../../core/service/notification.service';
import { SessionStore } from '../../../../core/state/session.store';
import { WorkspaceStore } from '../../workspace.store';
import {
  MaintenanceCategory,
  MaintenancePriority,
  MaintenanceRequest,
} from '../../../../core/models/api.types';
import { formatDate } from '../../../../core/utils/formatters';

@Component({
  selector: 'app-maintenance-tab',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, TitleCasePipe],
  templateUrl: './maintenance-tab.component.html',
})
export class MaintenanceTabComponent {
  protected readonly session = inject(SessionStore);
  private readonly maintenanceApi = inject(MaintenanceApi);
  protected readonly store = inject(WorkspaceStore);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly notifications = inject(NotificationService);

  protected readonly requests = signal<MaintenanceRequest[]>([]);
  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly showForm = signal(false);
  protected readonly selectedRequest = signal<MaintenanceRequest | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly resolveDialogOpen = signal(false);
  protected readonly cancelDialogOpen = signal(false);
  protected readonly resolutionNotes = signal('');

  protected readonly categories: MaintenanceCategory[] = [
    'PLUMBING',
    'ELECTRICAL',
    'APPLIANCE',
    'STRUCTURAL',
    'OTHER',
  ];
  protected readonly priorities: MaintenancePriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  protected readonly openRequests = computed(() =>
    this.requests().filter((r) => r.status === 'REPORTED' || r.status === 'IN_PROGRESS'),
  );
  protected readonly resolvedRequests = computed(() =>
    this.requests().filter((r) => r.status === 'RESOLVED' || r.status === 'CANCELLED'),
  );

  protected readonly createForm = this.fb.group({
    propertyId: ['', Validators.required],
    title: ['', [Validators.required, Validators.minLength(5)]],
    description: ['', [Validators.required, Validators.minLength(10)]],
    category: ['PLUMBING' as MaintenanceCategory, Validators.required],
    priority: ['MEDIUM' as MaintenancePriority, Validators.required],
  });

  constructor() {
    this.loadRequests();
  }

  private loadRequests(): void {
    this.loading.set(true);
    const source$ =
      this.session.isLandlord() || this.session.isAdmin()
        ? this.maintenanceApi.getLandlord()
        : this.maintenanceApi.getMine();

    source$
      .pipe(
        catchError(() => of([] as MaintenanceRequest[])),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((data) => {
        this.requests.set(data);
        this.loading.set(false);
      });
  }

  protected submitRequest(): void {
    if (this.createForm.invalid || this.submitting()) return;
    this.submitting.set(true);
    this.errorMessage.set('');
    this.maintenanceApi
      .create(this.createForm.value as any)
      .pipe(
        catchError((err) => {
          this.errorMessage.set(err.error?.message ?? 'Failed to submit request.');
          this.submitting.set(false);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((result) => {
        if (result) {
          this.createForm.reset({ category: 'PLUMBING', priority: 'MEDIUM' });
          this.showForm.set(false);
          this.notifications.success('Maintenance request submitted.');
          this.loadRequests();
        }
        this.submitting.set(false);
      });
  }

  protected updateStatus(id: string, status: 'RESOLVED' | 'CANCELLED', notes?: string): void {
    this.maintenanceApi
      .updateStatus(id, { status, resolutionNotes: notes })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.loadRequests();
          this.notifications.success(
            status === 'RESOLVED' ? 'Request marked as resolved.' : 'Request cancelled.',
          );
        },
        error: () => this.notifications.error('Unable to update the request right now.'),
      });
  }

  protected resolve(req: MaintenanceRequest): void {
    this.selectedRequest.set(req);
    this.resolutionNotes.set(req.resolutionNotes ?? '');
    this.resolveDialogOpen.set(true);
  }

  protected cancel(req: MaintenanceRequest): void {
    this.selectedRequest.set(req);
    this.cancelDialogOpen.set(true);
  }

  protected confirmResolve(): void {
    const request = this.selectedRequest();
    if (!request) return;
    const notes = this.resolutionNotes().trim() || undefined;
    this.updateStatus(request.id, 'RESOLVED', notes);
    this.closeDialogs();
  }

  protected confirmCancel(): void {
    const request = this.selectedRequest();
    if (!request) return;
    this.updateStatus(request.id, 'CANCELLED');
    this.closeDialogs();
  }

  protected closeDialogs(): void {
    this.resolveDialogOpen.set(false);
    this.cancelDialogOpen.set(false);
    this.selectedRequest.set(null);
    this.resolutionNotes.set('');
  }

  protected priorityClass(priority: string): string {
    const map: Record<string, string> = {
      URGENT: 'bg-rose-50 text-rose-700',
      HIGH: 'bg-orange-50 text-orange-700',
      MEDIUM: 'bg-amber-50 text-amber-700',
      LOW: 'bg-slate-100 text-slate-600',
    };
    return map[priority] ?? 'bg-slate-100 text-slate-600';
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      REPORTED: 'bg-amber-50 text-amber-700',
      IN_PROGRESS: 'bg-blue-50 text-blue-700',
      RESOLVED: 'bg-emerald-50 text-emerald-700',
      CANCELLED: 'bg-slate-100 text-slate-500',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }

  protected date(v: string): string {
    return formatDate(v);
  }
}
