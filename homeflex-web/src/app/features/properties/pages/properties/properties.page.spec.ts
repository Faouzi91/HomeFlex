import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PropertiesPageComponent } from './properties.page';
import { provideRouter } from '@angular/router';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('PropertiesPageComponent', () => {
  let component: PropertiesPageComponent;
  let fixture: ComponentFixture<PropertiesPageComponent>;

  beforeEach(async () => {
    const mockPropertyApi = { search: vi.fn().mockReturnValue(of({ data: [] })) };

    await TestBed.configureTestingModule({
      imports: [PropertiesPageComponent, TranslateModule.forRoot()],
      providers: [provideRouter([]), { provide: PropertyApi, useValue: mockPropertyApi }],
    }).compileComponents();

    fixture = TestBed.createComponent(PropertiesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
