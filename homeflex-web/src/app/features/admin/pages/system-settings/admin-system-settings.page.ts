import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { SystemConfig } from '../../../../core/models/api.types';

@Component({
  selector: 'app-admin-system-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-system-settings.page.html',
})
export class AdminSystemSettingsPageComponent {
  private readonly adminApi = inject(AdminApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly configs = signal<SystemConfig[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly editing = signal<Record<string, string>>({});
  protected readonly savingKey = signal<string | null>(null);
  protected readonly savedKey = signal<string | null>(null);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.adminApi
      .getSystemConfigs()
      .pipe(
        catchError((err) => {
          this.error.set(err?.error?.message ?? 'Failed to load system configuration');
          return of([] as SystemConfig[]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((list) => {
        this.configs.set(list);
        const draft: Record<string, string> = {};
        for (const c of list) draft[c.configKey] = c.configValue;
        this.editing.set(draft);
        this.loading.set(false);
      });
  }

  protected onValueChange(key: string, value: string): void {
    this.editing.update((d) => ({ ...d, [key]: value }));
  }

  protected isDirty(c: SystemConfig): boolean {
    return this.editing()[c.configKey] !== c.configValue;
  }

  protected editingValue(c: SystemConfig): string {
    return this.editing()[c.configKey] ?? '';
  }

  protected save(c: SystemConfig): void {
    const next = this.editingValue(c);
    if (next === c.configValue) return;

    this.savingKey.set(c.configKey);
    this.adminApi
      .updateSystemConfig(c.configKey, next)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.configs.update((list) =>
            list.map((x) => (x.configKey === updated.configKey ? updated : x)),
          );
          this.savingKey.set(null);
          this.savedKey.set(c.configKey);
          setTimeout(() => {
            if (this.savedKey() === c.configKey) this.savedKey.set(null);
          }, 1800);
        },
        error: () => {
          this.savingKey.set(null);
          this.error.set(`Failed to save '${c.configKey}'`);
        },
      });
  }

  protected reset(c: SystemConfig): void {
    this.editing.update((d) => ({ ...d, [c.configKey]: c.configValue }));
  }

  protected formatKey(key: string): string {
    return key.replaceAll('_', ' ');
  }
}
