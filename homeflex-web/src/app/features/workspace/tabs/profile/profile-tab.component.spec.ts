import { TestBed } from '@angular/core/testing';
import { computed, signal } from '@angular/core';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProfileTabComponent } from './profile-tab.component';
import { SessionStore } from '../../../../core/state/session.store';
import { UserApi } from '../../../../core/api/services/user.api';
import { KycApi } from '../../../../core/api/services/kyc.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { GdprApi } from '../../../../core/api/services/gdpr.api';
import { NotificationService } from '../../../../core/service/notification.service';
import { User } from '../../../../core/models/api.types';
import { Router } from '@angular/router';

describe('ProfileTabComponent', () => {
  it('loads host data when a landlord session appears after construction', () => {
    const user = signal<User | null>(null);
    const session = {
      user,
      isLandlord: computed(() => user()?.role === 'LANDLORD'),
      isAdmin: computed(() => user()?.role === 'ADMIN'),
    };

    const kycApi = {
      getStatus: vi.fn().mockReturnValue(of({ data: { status: 'PENDING' } })),
    };
    const payoutApi = {
      getSummary: vi.fn().mockReturnValue(of({ stripeAccountConnected: false })),
      onboardConnectAccount: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: SessionStore, useValue: session },
        {
          provide: UserApi,
          useValue: {
            updateProfile: () => of(null),
            changePassword: () => of(null),
            uploadAvatar: () => of(null),
          },
        },
        { provide: KycApi, useValue: kycApi },
        { provide: PayoutApi, useValue: payoutApi },
        { provide: GdprApi, useValue: { exportData: () => of({}), eraseData: () => of(void 0) } },
        { provide: NotificationService, useValue: { success: () => void 0, error: () => void 0 } },
        { provide: Router, useValue: { navigateByUrl: () => Promise.resolve(true) } },
      ],
    });

    TestBed.runInInjectionContext(() => new ProfileTabComponent());
    TestBed.flushEffects();

    expect(kycApi.getStatus).not.toHaveBeenCalled();
    expect(payoutApi.getSummary).not.toHaveBeenCalled();

    user.set({
      id: 'host-1',
      email: 'host@homeflex.test',
      firstName: 'Host',
      lastName: 'User',
      phoneNumber: null,
      profilePictureUrl: null,
      role: 'LANDLORD',
      isActive: true,
      isVerified: true,
      languagePreference: 'en',
      createdAt: new Date().toISOString(),
    });
    TestBed.flushEffects();

    expect(kycApi.getStatus).toHaveBeenCalled();
    expect(payoutApi.getSummary).toHaveBeenCalled();
  });
});
