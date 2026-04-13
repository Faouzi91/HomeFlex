import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class CurrencyApi extends BaseApi {
  getRates(): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(`${this.baseUrl}/currencies/rates`);
  }

  convert(amount: number, from: string, to: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/currencies/convert`, {
      params: this.buildParams({ amount, from, to }),
    });
  }
}
