import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiClient } from './api.client';

describe('ApiClient', () => {
  let api: ApiClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiClient, provideHttpClient(), provideHttpClientTesting()],
    });

    api = TestBed.inject(ApiClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('builds property search query params', () => {
    api
      .searchProperties({
        q: 'douala',
        city: 'Douala',
        bedrooms: 3,
        size: 12,
      })
      .subscribe();

    const request = httpMock.expectOne(
      (req) =>
        req.url === '/api/v1/properties/search' &&
        req.params.get('q') === 'douala' &&
        req.params.get('city') === 'Douala' &&
        req.params.get('bedrooms') === '3' &&
        req.params.get('size') === '12',
    );

    expect(request.request.method).toBe('GET');
    request.flush({ data: [], page: 0, size: 12, totalElements: 0, totalPages: 0 });
  });
});
