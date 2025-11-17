import { TestBed } from '@angular/core/testing';

import { CapacitorService } from './capacitor.service';

describe('CapacitorService', () => {
  let service: CapacitorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CapacitorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
