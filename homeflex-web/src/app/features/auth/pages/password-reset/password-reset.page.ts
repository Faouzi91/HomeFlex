import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthShowcaseComponent } from '../../components/auth-showcase/auth-showcase.component';
import { SessionStore } from '../../../../core/state/session.store';

@Component({
  selector: 'app-password-reset-page',
  imports: [ReactiveFormsModule, RouterLink, AuthShowcaseComponent],
  templateUrl: './password-reset.page.html',
  styleUrl: './password-reset.page.scss',
})
export class PasswordResetPageComponent {
  private readonly fb = inject(FormBuilder);
  protected readonly session = inject(SessionStore);
  protected readonly message = signal('');

  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.session.forgotPassword(this.form.getRawValue().email ?? '').subscribe((message) => {
      this.message.set(message);
    });
  }
}
