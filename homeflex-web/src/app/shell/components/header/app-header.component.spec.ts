import { ComponentFixture, TestBed } from '@angular/core/testing';
import { computed, signal } from '@angular/core';
import { of } from 'rxjs';
import { AppHeaderComponent } from './app-header.component';
import { provideRouter } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { SessionStore } from '../../../core/state/session.store';
import { WorkspaceStore } from '../../../features/workspace/workspace.store';
import { NotificationApi } from '../../../core/api/services/notification.api';

describe('AppHeaderComponent', () => {
  let component: AppHeaderComponent;
  let fixture: ComponentFixture<AppHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppHeaderComponent, TranslateModule.forRoot()],
      providers: [
        provideRouter([]),
        {
          provide: SessionStore,
          useValue: {
            user: signal(null),
            isAuthenticated: signal(false),
            currencyPreference: signal('XAF'),
            displayName: computed(() => 'Guest'),
            roleLabel: computed(() => 'Guest'),
            setCurrency: () => void 0,
          },
        },
        {
          provide: WorkspaceStore,
          useValue: {
            load: () => void 0,
            reset: () => void 0,
            unreadNotificationCount: signal(0),
            unreadMessageCount: signal(0),
          },
        },
        { provide: NotificationApi, useValue: { getAll: () => of({ data: [] }) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should default to language menus closed', () => {
    expect(component['langMenuOpen']()).toBe(false);
    expect(component['currencyMenuOpen']()).toBe(false);
  });
});
