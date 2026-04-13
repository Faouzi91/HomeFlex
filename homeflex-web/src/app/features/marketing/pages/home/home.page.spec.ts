import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomePageComponent } from './home.page';
import { provideRouter } from '@angular/router';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { StatsApi } from '../../../../core/api/services/stats.api';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('HomePageComponent', () => {
  let component: HomePageComponent;
  let fixture: ComponentFixture<HomePageComponent>;

  beforeEach(async () => {
    const mockPropertyApi = {
      search: vi.fn().mockReturnValue(of({ data: [] })),
      getCities: vi.fn().mockReturnValue(of([])),
    };
    const mockVehicleApi = { search: vi.fn().mockReturnValue(of({ data: [] })) };
    const mockStatsApi = { get: vi.fn().mockReturnValue(of({ data: {} })) };

    await TestBed.configureTestingModule({
      imports: [HomePageComponent],
      providers: [
        provideRouter([]),
        { provide: PropertyApi, useValue: mockPropertyApi },
        { provide: VehicleApi, useValue: mockVehicleApi },
        { provide: StatsApi, useValue: mockStatsApi },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
