import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { loadStripe } from '@stripe/stripe-js';
import { GdprApi } from '../../../../core/api/services/gdpr.api';
import { KycApi } from '../../../../core/api/services/kyc.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { UserApi } from '../../../../core/api/services/user.api';
import { SessionStore } from '../../../../core/state/session.store';
import { initials } from '../../../../core/utils/formatters';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-tab',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile-tab.component.html',
})
export class ProfileTabComponent {
  protected readonly session = inject(SessionStore);
  private readonly userApi = inject(UserApi);
  private readonly kycApi = inject(KycApi);
  private readonly payoutApi = inject(PayoutApi);
  private readonly gdprApi = inject(GdprApi);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  protected readonly profileMessage = signal('');
  protected readonly passwordMessage = signal('');
  protected readonly hostMessage = signal('');
  protected readonly kycStatus = signal<any>(null);
  protected readonly payoutSummary = signal<any>(null);

  protected readonly profileForm = this.fb.group({
    firstName: [''],
    lastName: [''],
    phoneNumber: [''],
    languagePreference: ['en'],
    emailNotificationsEnabled: [true],
    pushNotificationsEnabled: [true],
    smsNotificationsEnabled: [true],
  });

  protected readonly passwordForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor() {
    effect(() => {
      const u = this.session.user();
      if (!u) return;
      this.profileForm.patchValue({
        firstName: u.firstName,
        lastName: u.lastName,
        phoneNumber: u.phoneNumber,
        languagePreference: u.languagePreference || 'en',
        emailNotificationsEnabled: u.emailNotificationsEnabled ?? true,
        pushNotificationsEnabled: u.pushNotificationsEnabled ?? true,
        smsNotificationsEnabled: u.smsNotificationsEnabled ?? true,
      });
    });

    if (this.session.isLandlord() || this.session.isAdmin()) {
      this.loadKycStatus();
      this.loadPayoutSummary();
    }
  }

  protected onAvatarSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.userApi
      .uploadAvatar(file)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.flash(this.profileMessage, 'Profile picture updated!');
        },
        error: () => this.flash(this.profileMessage, 'Failed to upload avatar.'),
      });
  }

  protected saveProfile(): void {
    this.userApi
      .updateProfile(this.profileForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.flash(this.profileMessage, 'Profile updated successfully!');
        },
        error: () => this.flash(this.profileMessage, 'Failed to update profile.'),
      });
  }

  protected savePassword(): void {
    if (this.passwordForm.invalid) return;
    this.userApi
      .changePassword(this.passwordForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.passwordForm.reset();
          this.flash(this.passwordMessage, 'Password changed successfully!');
        },
        error: (err) =>
          this.flash(this.passwordMessage, err.error?.message ?? 'Failed to change password.'),
      });
  }

  private loadKycStatus(): void {
    this.kycApi
      .getStatus()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => this.kycStatus.set(res.data));
  }

  private loadPayoutSummary(): void {
    this.payoutApi
      .getSummary()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => this.payoutSummary.set(res));
  }

  protected startKyc(): void {
    this.kycApi
      .createSession()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(async (res) => {
        this.hostMessage.set('Redirecting to Stripe Identity…');
        const stripe = await loadStripe(res.publishableKey);
        if (stripe) {
          const { error } = await stripe.verifyIdentity(res.clientSecret);
          if (error) {
            this.hostMessage.set(`Verification failed: ${error.message}`);
          } else {
            this.flash(this.hostMessage, 'Verification submitted!');
            this.loadKycStatus();
          }
        }
      });
  }

  protected onboardConnect(): void {
    const currentUrl = window.location.href;
    this.payoutApi
      .onboardConnectAccount(currentUrl, currentUrl)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => (window.location.href = res.url));
  }

  protected exportMyData(): void {
    this.gdprApi
      .exportData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data) => {
        const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `homeflex-data-${new Date().toISOString()}.json`;
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  protected eraseMyData(): void {
    const confirmation = prompt('Type DELETE to permanently erase your account and all data:');
    if (confirmation !== 'DELETE') return;
    this.gdprApi
      .eraseData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        alert('Your data has been erased. You will now be logged out.');
        this.session.logout().subscribe(() => this.router.navigateByUrl('/'));
      });
  }

  protected userInitials(): string {
    const u = this.session.user();
    if (!u) return '?';
    return initials(u.firstName, u.lastName);
  }

  private flash(sig: ReturnType<typeof signal<string>>, msg: string): void {
    sig.set(msg);
    setTimeout(() => sig.set(''), 3500);
  }
}
