import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { VehicleBooking } from '../../../../../core/models/api.types';
import { formatCurrency, formatDate } from '../../../../../core/utils/formatters';

@Component({
  selector: 'app-vehicle-booking-detail-panel',
  standalone: true,
  imports: [NgClass],
  templateUrl: './vehicle-booking-detail-panel.component.html',
})
export class VehicleBookingDetailPanelComponent {
  @Input({ required: true }) booking!: VehicleBooking;
  @Output() closed = new EventEmitter<void>();
  @Output() bookingChanged = new EventEmitter<VehicleBooking>();

  protected readonly loading = signal(false);

  protected close(): void {
    this.closed.emit();
  }

  protected date(v: string | null): string {
    return formatDate(v);
  }

  protected price(v: number | null, cur = 'XAF'): string {
    return v != null ? formatCurrency(v, cur) : '—';
  }

  protected statusClass(status: string): string {
    const map: Record<string, string> = {
      DRAFT: 'bg-slate-100 text-slate-600',
      PAYMENT_PENDING: 'bg-amber-50 text-amber-700',
      PAYMENT_FAILED: 'bg-rose-50 text-rose-700',
      PENDING_APPROVAL: 'bg-blue-50 text-blue-700',
      APPROVED: 'bg-emerald-50 text-emerald-700',
      ACTIVE: 'bg-emerald-100 text-emerald-800',
      CANCELLED: 'bg-rose-50 text-rose-700',
      REJECTED: 'bg-rose-50 text-rose-700',
      COMPLETED: 'bg-slate-100 text-slate-600',
    };
    return map[status] ?? 'bg-slate-100 text-slate-600';
  }
}
