import { TestBed } from '@angular/core/testing';
import { MyPropertiesComponent } from './my-properties.component';

describe('MyPropertiesComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyPropertiesComponent],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(MyPropertiesComponent);
    const comp = fixture.componentInstance;
    expect(comp).toBeTruthy();
  });
});
