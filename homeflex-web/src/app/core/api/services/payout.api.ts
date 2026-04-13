import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class PayoutApi extends BaseApi {
  getSummary(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/payouts/summary`);
  }

  onboardConnectAccount(refreshUrl: string, returnUrl: string): Observable<{ url: string }> {
    return this.http.post<any>(`${this.baseUrl}/payouts/connect/onboard`, {
      refreshUrl,
      returnUrl,
    });
  }
}
