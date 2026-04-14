import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkspacePageComponent } from './workspace.page';
import { provideRouter } from '@angular/router';
import { AdminApi } from '../../../../core/api/services/admin.api';
import { AgencyApi } from '../../../../core/api/services/agency.api';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { ChatApi } from '../../../../core/api/services/chat.api';
import { DisputeApi } from '../../../../core/api/services/dispute.api';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { FinanceApi } from '../../../../core/api/services/finance.api';
import { GdprApi } from '../../../../core/api/services/gdpr.api';
import { InsuranceApi } from '../../../../core/api/services/insurance.api';
import { KycApi } from '../../../../core/api/services/kyc.api';
import { LeaseApi } from '../../../../core/api/services/lease.api';
import { MaintenanceApi } from '../../../../core/api/services/maintenance.api';
import { NotificationApi } from '../../../../core/api/services/notification.api';
import { PayoutApi } from '../../../../core/api/services/payout.api';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { UserApi } from '../../../../core/api/services/user.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { SessionStore } from '../../../../core/state/session.store';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('WorkspacePageComponent', () => {
  let component: WorkspacePageComponent;
  let fixture: ComponentFixture<WorkspacePageComponent>;

  beforeEach(async () => {
    const stub = () => vi.fn().mockReturnValue(of({ data: [] }));
    const emptyMock = new Proxy({}, { get: () => stub() });

    await TestBed.configureTestingModule({
      imports: [WorkspacePageComponent],
      providers: [
        provideRouter([]),
        SessionStore,
        { provide: AdminApi, useValue: emptyMock },
        { provide: AgencyApi, useValue: emptyMock },
        { provide: BookingApi, useValue: emptyMock },
        { provide: ChatApi, useValue: emptyMock },
        { provide: DisputeApi, useValue: emptyMock },
        { provide: FavoriteApi, useValue: emptyMock },
        { provide: FinanceApi, useValue: emptyMock },
        { provide: GdprApi, useValue: emptyMock },
        { provide: InsuranceApi, useValue: emptyMock },
        { provide: KycApi, useValue: emptyMock },
        { provide: LeaseApi, useValue: emptyMock },
        { provide: MaintenanceApi, useValue: emptyMock },
        { provide: NotificationApi, useValue: emptyMock },
        { provide: PayoutApi, useValue: emptyMock },
        { provide: PropertyApi, useValue: emptyMock },
        { provide: UserApi, useValue: emptyMock },
        { provide: VehicleApi, useValue: emptyMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkspacePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
