import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VehicleDetailPageComponent } from './vehicle-detail.page';
import { provideRouter } from '@angular/router';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { SessionStore } from '../../../../core/state/session.store';
import { CurrencyPipe } from '@angular/common';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('VehicleDetailPageComponent', () => {
  let component: VehicleDetailPageComponent;
  let fixture: ComponentFixture<VehicleDetailPageComponent>;

  beforeEach(async () => {
    const mockApi = {
      getById: vi.fn().mockReturnValue(of({})),
      trackView: vi.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [VehicleDetailPageComponent],
      providers: [
        provideRouter([]),
        SessionStore,
        CurrencyPipe,
        ConvertCurrencyPipe,
        { provide: VehicleApi, useValue: mockApi },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VehicleDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
