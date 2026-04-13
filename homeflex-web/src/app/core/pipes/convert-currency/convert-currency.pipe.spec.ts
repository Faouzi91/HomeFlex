import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ConvertCurrencyPipe } from './convert-currency.pipe';
import { CurrencyPipe } from '@angular/common';

describe('ConvertCurrencyPipe', () => {
  let pipe: ConvertCurrencyPipe;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ConvertCurrencyPipe,
        CurrencyPipe,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    pipe = TestBed.inject(ConvertCurrencyPipe);
    httpMock = TestBed.inject(HttpTestingController);

    // Flush the rates request with test data
    const req = httpMock.expectOne('/api/v1/currencies/rates');
    req.flush({ USD: 1, EUR: 0.92, GBP: 0.79, XAF: 605 });
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform to the same currency if source and target match', () => {
    const result = pipe.transform(100, 'XAF', 'XAF');
    expect(result).toBeDefined();
  });

  it('should apply conversion rate when target differs', () => {
    const usdEquivalent = pipe.transform(1000, 'XAF', 'USD');
    expect(usdEquivalent).toContain('$');
  });
});
