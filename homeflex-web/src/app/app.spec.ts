import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { App } from './app';
import { SessionStore } from './core/state/session.store';
import { WorkspaceStore } from './features/workspace/workspace.store';
import { NotificationApi } from './core/api/services/notification.api';

const sessionStoreMock = {
  init: () => void 0,
  user: signal(null),
  isAuthenticated: signal(false),
  displayName: signal('Guest'),
  roleLabel: signal('Guest'),
  currencyPreference: signal('XAF'),
  setCurrency: () => void 0,
};

const workspaceStoreMock = {
  load: () => void 0,
  reset: () => void 0,
  unreadNotificationCount: signal(0),
  unreadMessageCount: signal(0),
};

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App, TranslateModule.forRoot()],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        TranslateService,
        { provide: SessionStore, useValue: sessionStoreMock },
        { provide: WorkspaceStore, useValue: workspaceStoreMock },
        { provide: NotificationApi, useValue: { getAll: () => of({ data: [] }) } },
      ],
    }).compileComponents();
  });

  it('creates the shell', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('renders the navigation brand', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('HomeFlex');
  });
});
