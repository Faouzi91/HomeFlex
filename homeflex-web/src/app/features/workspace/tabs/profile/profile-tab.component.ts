import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { loadStripe } from '@stripe/stripe-js';
import { catchError, of } from 'rxjs';
import { GdprApi } from '../../../../core/api/services/gdpr.api';
import { KycApi } from '../../../../core/api/services/kyc.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { UserApi } from '../../../../core/api/services/user.api';
import { NotificationService } from '../../../../core/service/notification.service';
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
  private readonly notifications = inject(NotificationService);

  protected readonly hostMessage = signal('');
  protected readonly kycStatus = signal<any>(null);
  protected readonly payoutSummary = signal<any>(null);
  protected readonly eraseDialogOpen = signal(false);
  protected readonly eraseConfirmation = signal('');
  private readonly hostDataLoaded = signal(false);

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

    effect(() => {
      const user = this.session.user();
      const isHost = user?.role === 'LANDLORD' || user?.role === 'ADMIN';

      if (!isHost) {
        this.hostDataLoaded.set(false);
        this.kycStatus.set(null);
        this.payoutSummary.set(null);
        return;
      }

      if (this.hostDataLoaded()) {
        return;
      }

      this.hostDataLoaded.set(true);
      this.loadKycStatus();
      this.loadPayoutSummary();
    });
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
          this.notifications.success('Profile picture updated.');
        },
        error: () => this.notifications.error('Failed to upload avatar.'),
      });
  }

  protected saveProfile(): void {
    this.userApi
      .updateProfile(this.profileForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.notifications.success('Profile updated successfully.');
        },
        error: () => this.notifications.error('Failed to update profile.'),
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
          this.notifications.success('Password changed successfully.');
        },
        error: (err) =>
          this.notifications.error(err.error?.message ?? 'Failed to change password.'),
      });
  }

  private loadKycStatus(): void {
    this.kycApi
      .getStatus()
      .pipe(
        catchError(() => {
          this.notifications.error('Unable to load identity verification status.');
          return of({ data: null });
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => this.kycStatus.set(res.data));
  }

  private loadPayoutSummary(): void {
    this.payoutApi
      .getSummary()
      .pipe(
        catchError(() => {
          this.notifications.error('Unable to load payout account details.');
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
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
            this.notifications.error(error.message || 'Verification failed.');
          } else {
            this.hostMessage.set('Verification submitted!');
            this.notifications.success('Verification submitted.');
            this.loadKycStatus();
          }
        } else {
          this.hostMessage.set('Stripe Identity is unavailable right now.');
          this.notifications.error('Stripe Identity is unavailable right now.');
        }
      });
  }

  protected onboardConnect(): void {
    const currentUrl = window.location.href;
    this.payoutApi
      .onboardConnectAccount(currentUrl, currentUrl)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => (window.location.href = res.onboardingUrl),
        error: () => this.notifications.error('Unable to open Stripe onboarding right now.'),
      });
  }

  protected exportMyData(): void {
    this.gdprApi
      .exportData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `homeflex-data-${new Date().toISOString()}.json`;
          a.click();
          window.URL.revokeObjectURL(url);
          this.notifications.success('Your data export is downloading.');
        },
        error: () => this.notifications.error('Unable to export your data right now.'),
      });
  }

  protected eraseMyData(): void {
    if (this.eraseConfirmation().trim().toUpperCase() !== 'DELETE') {
      this.notifications.error('Type DELETE to confirm account removal.');
      return;
    }

    this.gdprApi
      .eraseData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.eraseDialogOpen.set(false);
          this.eraseConfirmation.set('');
          this.notifications.success('Your account data has been erased.');
          this.session.logout().subscribe(() => this.router.navigateByUrl('/'));
        },
        error: () => this.notifications.error('Unable to erase your account right now.'),
      });
  }

  protected openEraseDialog(): void {
    this.eraseConfirmation.set('');
    this.eraseDialogOpen.set(true);
  }

  protected closeEraseDialog(): void {
    this.eraseConfirmation.set('');
    this.eraseDialogOpen.set(false);
  }

  protected userInitials(): string {
    const u = this.session.user();
    if (!u) return '?';
    return initials(u.firstName, u.lastName);
  }
}
