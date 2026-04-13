import { HttpClient, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';

export abstract class BaseApi {
  protected readonly http = inject(HttpClient);
  protected readonly baseUrl = '/api/v1';

  protected buildParams(values: object): HttpParams {
    let params = new HttpParams();

    Object.entries(values as Record<string, unknown>).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, String(value));
      }
    });

    return params;
  }
}
