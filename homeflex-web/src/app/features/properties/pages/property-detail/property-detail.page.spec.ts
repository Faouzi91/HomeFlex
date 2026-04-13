import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PropertyDetailPageComponent } from './property-detail.page';
import { provideRouter } from '@angular/router';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { BookingApi } from '../../../../core/api/services/booking.api';
import { FavoriteApi } from '../../../../core/api/services/favorite.api';
import { ReviewApi } from '../../../../core/api/services/review.api';
import { ChatApi } from '../../../../core/api/services/chat.api';
import { CurrencyPipe } from '@angular/common';
import { ConvertCurrencyPipe } from '../../../../core/pipes/convert-currency/convert-currency.pipe';
import { SessionStore } from '../../../../core/state/session.store';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('PropertyDetailPageComponent', () => {
  let component: PropertyDetailPageComponent;
  let fixture: ComponentFixture<PropertyDetailPageComponent>;

  beforeEach(async () => {
    const mockApi = {
      getById: vi.fn().mockReturnValue(of({})),
      getSimilar: vi.fn().mockReturnValue(of({ data: [] })),
      trackView: vi.fn().mockReturnValue(of({})),
      getByProperty: vi.fn().mockReturnValue(of({ data: [] })),
      getPropertyAverage: vi.fn().mockReturnValue(of({ data: null })),
      check: vi.fn().mockReturnValue(of({ data: false })),
    };

    await TestBed.configureTestingModule({
      imports: [PropertyDetailPageComponent],
      providers: [
        provideRouter([]),
        SessionStore,
        CurrencyPipe,
        ConvertCurrencyPipe,
        { provide: PropertyApi, useValue: mockApi },
        { provide: BookingApi, useValue: mockApi },
        { provide: FavoriteApi, useValue: mockApi },
        { provide: ReviewApi, useValue: mockApi },
        { provide: ChatApi, useValue: mockApi },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PropertyDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
