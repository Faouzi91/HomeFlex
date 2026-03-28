import { TestBed } from '@angular/core/testing';
import { BookingsListComponent } from './bookings-list.component';

describe('BookingsListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingsListComponent],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(BookingsListComponent);
    const comp = fixture.componentInstance;
    expect(comp).toBeTruthy();
  });
});
