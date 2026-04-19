import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApi } from './base.api';
import { ConnectOnboardingResponse, PayoutSummary } from '../../models/api.types';

@Injectable({ providedIn: 'root' })
export class PayoutApi extends BaseApi {
  getSummary(): Observable<PayoutSummary> {
    return this.http.get<PayoutSummary>(`${this.baseUrl}/payouts/summary`);
  }

  onboardConnectAccount(
    refreshUrl: string,
    returnUrl: string,
  ): Observable<ConnectOnboardingResponse> {
    return this.http.post<ConnectOnboardingResponse>(`${this.baseUrl}/payouts/connect/onboard`, {
      refreshUrl,
      returnUrl,
    });
  }
}
