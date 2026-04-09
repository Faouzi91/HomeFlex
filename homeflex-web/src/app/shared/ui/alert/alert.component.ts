import { Component, input, output } from '@angular/core';
import { NotificationType } from '../../../core/service/notification.service';

@Component({
  selector: 'app-alert',
  template: `
    <div
      class="flex items-start gap-4 p-4 rounded-xl border shadow-sm transition-all"
      [class.bg-emerald-50]="type() === 'success'"
      [class.border-emerald-100]="type() === 'success'"
      [class.text-emerald-800]="type() === 'success'"
      [class.bg-rose-50]="type() === 'error'"
      [class.border-rose-100]="type() === 'error'"
      [class.text-rose-800]="type() === 'error'"
      [class.bg-amber-50]="type() === 'warning'"
      [class.border-amber-100]="type() === 'warning'"
      [class.text-amber-800]="type() === 'warning'"
      [class.bg-sky-50]="type() === 'info'"
      [class.border-sky-100]="type() === 'info'"
      [class.text-sky-800]="type() === 'info'"
    >
      <div class="flex-grow text-sm font-bold leading-relaxed">
        {{ message() }}
      </div>
      <button
        type="button"
        (click)="dismiss.emit()"
        class="shrink-0 rounded-md p-1 hover:bg-black/5 transition-colors"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="2.5"
          stroke="currentColor"
          class="w-4 h-4"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  `,
})
export class AlertComponent {
  readonly type = input.required<NotificationType>();
  readonly message = input.required<string>();
  readonly dismiss = output();
}
