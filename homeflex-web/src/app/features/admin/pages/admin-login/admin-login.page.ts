import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { SessionStore } from '../../../../core/state/session.store';

@Component({
  selector: 'app-admin-login-page',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-login.page.html',
})
export class AdminLoginPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  protected readonly session = inject(SessionStore);

  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.session.login(value.email ?? '', value.password ?? '').subscribe(() => {
      if (this.session.isAdmin()) {
        this.router.navigateByUrl('/admin');
      } else {
        // Not an admin — log them out and show error
        this.session.logout().subscribe();
        this.session.error.set('Access denied. This portal is for platform administrators only.');
      }
    });
  }
}
