import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { SessionStore } from '../../../../core/state/session.store';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.page.html',
  styleUrl: './login.page.scss',
})
export class LoginPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected readonly session = inject(SessionStore);

  protected readonly showPassword = signal(false);

  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.session.login(value.email ?? '', value.password ?? '').subscribe(() => {
      this.router.navigateByUrl(
        this.route.snapshot.queryParamMap.get('redirectUrl') ||
          (this.session.isAdmin() ? '/admin' : '/workspace'),
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
