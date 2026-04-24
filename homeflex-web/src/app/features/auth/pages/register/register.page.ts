import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
  private readonly route = inject(ActivatedRoute);
  protected readonly session = inject(SessionStore);

  protected readonly showPassword = signal(false);

  protected readonly form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    role: ['TENANT', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  private readonly passwordValue = toSignal(this.form.controls.password.valueChanges, {
    initialValue: '',
  });

  protected readonly passwordStrength = computed(() => {
    const pw = this.passwordValue() ?? '';
    if (!pw) return { score: 0, label: '', color: '' };

    let score = 0;
    if (pw.length >= 8) score++;
    if (pw.length >= 12) score++;
    if (/[A-Z]/.test(pw)) score++;
    if (/[0-9]/.test(pw)) score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;

    if (score <= 1) return { score: 1, label: 'Weak', color: 'bg-rose-500' };
    if (score <= 2) return { score: 2, label: 'Fair', color: 'bg-amber-500' };
    if (score <= 3) return { score: 3, label: 'Good', color: 'bg-blue-500' };
    return { score: 4, label: 'Strong', color: 'bg-green-500' };
  });

  protected togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  protected selectRole(role: string): void {
    this.form.get('role')?.setValue(role);
  }

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
        this.router.navigateByUrl(
          this.route.snapshot.queryParamMap.get('redirectUrl') || '/workspace',
        );
      });
  }

  protected socialLogin(provider: string): void {
    const dummyToken = 'dummy-token-' + Date.now();
    this.session.socialLogin(provider, dummyToken).subscribe(() => {
      this.router.navigateByUrl(
        this.route.snapshot.queryParamMap.get('redirectUrl') ||
          (this.session.isAdmin() ? '/admin' : '/workspace'),
      );
    });
  }
}
