import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { publicAccessGuard } from './public-access.guard';

describe('publicAccessGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => publicAccessGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
