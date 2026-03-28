import { TestBed } from '@angular/core/testing';
import { ChatListComponent } from './chat-list.component';

describe('ChatListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatListComponent],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ChatListComponent);
    const comp = fixture.componentInstance;
    expect(comp).toBeTruthy();
  });
});
