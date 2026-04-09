import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthShowcaseComponent } from '../../components/auth-showcase/auth-showcase.component';
import { SessionStore } from '../../../../core/state/session.store';

@Component({
  selector: 'app-register-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.page.html',
  styleUrl: './register.page.scss',
})
export class RegisterPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  protected readonly session = inject(SessionStore);

  protected readonly form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    role: ['TENANT', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.session
      .register({
        email: value.email ?? '',
        password: value.password ?? '',
        firstName: value.firstName ?? '',
        lastName: value.lastName ?? '',
        role: value.role ?? 'TENANT',
        phoneNumber: value.phoneNumber || null,
      })
      .subscribe(() => {
        this.router.navigateByUrl('/workspace');
      });
  }
}
