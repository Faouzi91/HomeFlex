import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SessionStore } from '../../../../core/state/session.store';

@Component({
  selector: 'app-password-reset-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './password-reset.page.html',
  styleUrl: './password-reset.page.scss',
})
export class PasswordResetPageComponent {
  private readonly fb = inject(FormBuilder);
  protected readonly session = inject(SessionStore);
  protected readonly resetMessage = signal('');
  protected readonly submittedEmail = signal('');

  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  protected readonly emailControl = this.form.controls.email;
  protected readonly canSubmit = computed(
    () => this.form.valid && !this.session.pending() && !this.resetMessage(),
  );

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const email = (this.form.getRawValue().email ?? '').trim();

    this.session.forgotPassword(email).subscribe((message) => {
      this.submittedEmail.set(email);
      this.resetMessage.set(message);
      this.form.disable();
    });
  }

  protected editEmail(): void {
    this.resetMessage.set('');
    this.form.enable();
    this.form.controls.email.markAsUntouched();
  }

  protected resend(): void {
    if (!this.submittedEmail() || this.session.pending()) {
      return;
    }

    this.session.forgotPassword(this.submittedEmail()).subscribe((message) => {
      this.resetMessage.set(message);
    });
  }
}
