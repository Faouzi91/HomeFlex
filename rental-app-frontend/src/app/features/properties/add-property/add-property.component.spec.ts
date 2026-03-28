import { TestBed } from '@angular/core/testing';
import { AddPropertyComponent } from './add-property.component';

describe('AddPropertyComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddPropertyComponent],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(AddPropertyComponent);
    const comp = fixture.componentInstance;
    expect(comp).toBeTruthy();
  });
});
