import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VehiclesPageComponent } from './vehicles.page';
import { provideRouter } from '@angular/router';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('VehiclesPageComponent', () => {
  let component: VehiclesPageComponent;
  let fixture: ComponentFixture<VehiclesPageComponent>;

  beforeEach(async () => {
    const mockApi = { search: vi.fn().mockReturnValue(of({ data: [] })) };

    await TestBed.configureTestingModule({
      imports: [VehiclesPageComponent],
      providers: [provideRouter([]), { provide: VehicleApi, useValue: mockApi }],
    }).compileComponents();

    fixture = TestBed.createComponent(VehiclesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
