import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'light' | 'dark' | 'system';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private theme = new BehaviorSubject<Theme>('system');
  theme$ = this.theme.asObservable();

  constructor() {
    this.initTheme();
  }

  private initTheme(): void {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme) {
      this.setTheme(savedTheme);
    } else {
      this.setTheme('system');
    }
  }

  setTheme(theme: Theme): void {
    this.theme.next(theme);
    localStorage.setItem('theme', theme);

    if (theme === 'system') {
      const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.applyTheme(systemDark ? 'dark' : 'light');
    } else {
      this.applyTheme(theme);
    }
  }

  toggleTheme(): void {
    const current =
      this.theme.value === 'system'
        ? document.documentElement.classList.contains('dark')
          ? 'dark'
          : 'light'
        : this.theme.value;

    this.setTheme(current === 'dark' ? 'light' : 'dark');
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  getCurrentTheme(): Theme {
    return this.theme.value;
  }
}
