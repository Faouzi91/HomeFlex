import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SupportPageComponent } from './support.page';
import { provideRouter } from '@angular/router';

describe('SupportPageComponent', () => {
  let component: SupportPageComponent;
  let fixture: ComponentFixture<SupportPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupportPageComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(SupportPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have FAQs defined', () => {
    expect(component['faqs'].length).toBeGreaterThan(0);
  });
});
