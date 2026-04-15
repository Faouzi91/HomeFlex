import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SessionStore } from '../../../../core/state/session.store';
import { UserApi } from '../../../../core/api/services/user.api';

@Component({
  selector: 'app-admin-settings',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-settings.page.html',
})
export class AdminSettingsPageComponent {
  protected readonly session = inject(SessionStore);
  private readonly userApi = inject(UserApi);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly profileMessage = signal('');
  protected readonly passwordMessage = signal('');

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
    const user = this.session.user();
    if (user) {
      this.profileForm.patchValue({
        firstName: user.firstName,
        lastName: user.lastName,
        phoneNumber: user.phoneNumber,
        languagePreference: user.languagePreference || 'en',
        emailNotificationsEnabled: user.emailNotificationsEnabled ?? true,
        pushNotificationsEnabled: user.pushNotificationsEnabled ?? true,
        smsNotificationsEnabled: user.smsNotificationsEnabled ?? true,
      });
    }
  }

  protected userInitials(): string {
    const user = this.session.user();
    if (!user) return '?';
    return `${user.firstName?.charAt(0) ?? ''}${user.lastName?.charAt(0) ?? ''}`.toUpperCase();
  }

  protected saveProfile(): void {
    this.userApi
      .updateProfile(this.profileForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.profileMessage.set('Profile updated successfully.');
          setTimeout(() => this.profileMessage.set(''), 3000);
        },
        error: () => this.profileMessage.set('Failed to update profile.'),
      });
  }

  protected savePassword(): void {
    if (this.passwordForm.invalid) return;
    this.userApi
      .changePassword(this.passwordForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.passwordMessage.set('Password changed successfully.');
          this.passwordForm.reset();
          setTimeout(() => this.passwordMessage.set(''), 3000);
        },
        error: (err) =>
          this.passwordMessage.set(err.error?.message || 'Failed to change password.'),
      });
  }

  protected onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.userApi
      .uploadAvatar(file)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.profileMessage.set('Profile picture updated!');
          setTimeout(() => this.profileMessage.set(''), 3000);
        },
        error: () => this.profileMessage.set('Failed to upload avatar.'),
      });
  }
}
