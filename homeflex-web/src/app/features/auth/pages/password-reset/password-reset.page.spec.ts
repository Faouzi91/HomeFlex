import { TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { vi } from 'vitest';
import { PasswordResetPageComponent } from './password-reset.page';
import { SessionStore } from '../../../../core/state/session.store';

describe('PasswordResetPageComponent', () => {
  it('locks the form and stores the email after a successful request', () => {
    const forgotPassword = vi.fn().mockImplementation((email: string) => ({
      subscribe: (next: (message: string) => void) => next(`Sent to ${email}`),
    }));

    TestBed.configureTestingModule({
      providers: [
        {
          provide: SessionStore,
          useValue: {
            forgotPassword,
            pending: signal(false),
            error: signal(null),
          },
        },
      ],
    });

    const component = TestBed.runInInjectionContext(() => new PasswordResetPageComponent());
    component['form'].controls.email.setValue('guest@homeflex.test');
    component['submit']();

    expect(forgotPassword).toHaveBeenCalledWith('guest@homeflex.test');
    expect(component['submittedEmail']()).toBe('guest@homeflex.test');
    expect(component['resetMessage']()).toContain('guest@homeflex.test');
    expect(component['form'].disabled).toBe(true);
  });
});
